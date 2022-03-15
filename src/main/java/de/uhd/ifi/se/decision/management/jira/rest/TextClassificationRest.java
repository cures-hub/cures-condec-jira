package de.uhd.ifi.se.decision.management.jira.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationManagerForJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.classification.ClassifierType;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import smile.validation.ClassificationMetrics;

/**
 * REST resource for automatic text classification and its configuration.
 */
@Path("/classification")
public class TextClassificationRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TextClassificationRest.class);

	@Path("/setTextClassifierEnabled")
	@POST
	public Response setTextClassifierEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("isTextClassifierEnabled") boolean isActivated) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ConfigPersistenceManager.setTextClassifierActivated(projectKey, isActivated);
		return Response.ok().build();
	}

	@Path("/useTrainedClassifier")
	@POST
	public Response useTrainedClassifier(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("trainedClassifier") String trainedClassifier,
			@QueryParam("isOnlineLearningActivated") boolean isOnlineLearningActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (trainedClassifier == null || trainedClassifier.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The classifier could not be set since the file name is invalid."))
					.build();
		}
		TextClassificationConfiguration config = ConfigPersistenceManager
				.getTextClassificationConfiguration(projectKey);
		config.setSelectedTrainedClassifier(trainedClassifier);
		config.setOnlineLearningActivated(isOnlineLearningActivated);
		ConfigPersistenceManager.saveTextClassificationConfiguration(projectKey, config);
		TextClassifier.getInstance(projectKey).setSelectedTrainedClassifier(trainedClassifier);
		return Response.ok().build();
	}

	@Path("/trainClassifier")
	@POST
	public Response trainClassifier(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("trainingFileName") String trainingFileName,
			@QueryParam("binaryClassifierType") String binaryClassifierType,
			@QueryParam("fineGrainedClassifierType") String fineGrainedClassifierType) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (trainingFileName == null || trainingFileName.isEmpty()
				|| !new File(TextClassifier.CLASSIFIER_DIRECTORY + trainingFileName).exists()) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"The classifier could not be trained since the training file name is invalid.")).build();
		}
		ConfigPersistenceManager.setTrainingFileForClassifier(projectKey, trainingFileName);
		TextClassifier classifier = TextClassifier.getInstance(projectKey);
		if (classifier.train(trainingFileName, ClassifierType.valueOfOrDefault(binaryClassifierType),
				ClassifierType.valueOfOrDefault(fineGrainedClassifierType))) {
			return Response.ok().build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "The classifier could not be trained.")).build();
	}

	@Path("/evaluateTextClassifier")
	@POST
	public Response evaluateTextClassifier(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("trainingFileName") String trainingFileName,
			@QueryParam("numberOfFolds") int numberOfFolds,
			@QueryParam("binaryClassifierType") String binaryClassifierType,
			@QueryParam("fineGrainedClassifierType") String fineGrainedClassifierType) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		TextClassifier classifier = TextClassifier.getInstance(projectKey);
		classifier.setGroundTruthFile(trainingFileName);
		Map<String, ClassificationMetrics> evaluationResults = classifier.evaluate(numberOfFolds,
				ClassifierType.valueOfOrDefault(binaryClassifierType),
				ClassifierType.valueOfOrDefault(fineGrainedClassifierType));
		String evaluationResultsMessage = "Ground truth file name: " + trainingFileName + System.lineSeparator();
		String trainedClassifierName = "Name of trained classifier: " + ConfigPersistenceManager
				.getTextClassificationConfiguration(projectKey).getSelectedTrainedClassifier();
		evaluationResultsMessage += numberOfFolds > 1
				? "Trained and evaluated using " + numberOfFolds + "-fold cross-validation"
				: trainedClassifierName;
		evaluationResultsMessage += System.lineSeparator() + evaluationResults.toString();
		TextClassificationConfiguration config = ConfigPersistenceManager
				.getTextClassificationConfiguration(projectKey);
		config.setLastEvaluationResults(evaluationResultsMessage);
		ConfigPersistenceManager.saveTextClassificationConfiguration(projectKey, config);
		return Response.ok(ImmutableMap.of("content", evaluationResultsMessage)).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project. The trained {@link TextClassifier} of the
	 *            project is used for classification.
	 * @param text
	 *            to be classified as decision knowledge or irrelevant wrt. decision
	 *            knowledge.
	 * @return {@link KnowledgeType}, "other" for irrelevant.
	 */
	@Path("/classify/{projectKey}")
	@POST
	public Response classifyText(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
			String text) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		TextClassifier classifier = TextClassifier.getInstance(projectKey);
		String classificationResult = KnowledgeType.OTHER.toString();

		boolean isRelevant = classifier.getBinaryClassifier().predict(text);
		if (isRelevant) {
			KnowledgeType type = classifier.getFineGrainedClassifier().predict(text);
			classificationResult = type.toString();
		}
		LOGGER.info("Automatic text classification result is " + classificationResult + " for sentence " + text);)
		return Response.ok(ImmutableMap.of("classificationResult", classificationResult)).build();
	}

	@Path("/saveTrainingFile")
	@POST
	public Response saveTrainingFileForTextClassifier(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		TextClassifier trainer = TextClassifier.getInstance(projectKey);
		File trainingFile = trainer.saveTrainingFile();

		if (trainingFile != null) {
			String fileContent = "";
			try {
				fileContent = FileUtils.readFileToString(trainingFile, StandardCharsets.UTF_8);
			} catch (IOException e) {
				LOGGER.error("Training file content could not be read. " + e.getMessage());
			}
			return Response.ok(ImmutableMap.of("trainingFile", trainingFile.getPath(), "content", fileContent)).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error",
						"Training file for text classifier could not be created because of an internal server error."))
				.build();
	}

	@Path("/classifyWholeProject")
	@POST
	public Response classifyWholeProject(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		TextClassificationConfiguration config = new DecisionKnowledgeProject(projectKey)
				.getTextClassificationConfiguration();
		if (!config.isActivated()) {
			return Response.status(Status.FORBIDDEN)
					.entity(ImmutableMap.of("error", "Automatic classification is disabled for this project.")).build();
		}
		ClassificationManagerForJiraIssueText classificationManager = new ClassificationManagerForJiraIssueText(
				projectKey);
		List<Issue> jiraIssuesPerProject = KnowledgePersistenceManager.getInstance(projectKey).getJiraIssueManager()
				.getAllJiraIssuesForProject();
		for (Issue issue : jiraIssuesPerProject) {
			classificationManager.classifyDescriptionAndAllComments(issue);
		}
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param jiraIssueKey
	 *            of a Jira issue, e.g. requirement or work item.
	 * @return all parts of Jira issue text in the description and comments of the
	 *         Jira issue that are not manually approved.
	 */
	@Path("/non-validated-elements/{projectKey}/{jiraIssueKey}")
	@GET
	public Response getNonValidatedElements(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, @PathParam("jiraIssueKey") String jiraIssueKey) {

		if (request == null || projectKey == null || jiraIssueKey == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Non-validated elements could not be found due to a bad request."))
					.build();
		}

		Issue jiraIssue = JiraIssuePersistenceManager.getJiraIssue(jiraIssueKey);
		long id = jiraIssue.getId();

		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		List<KnowledgeElement> elements = manager.getElementsInJiraIssue(id);
		List<KnowledgeElement> nonValidatedElements = new ArrayList<KnowledgeElement>();
		for (KnowledgeElement element : elements) {
			PartOfJiraIssueText issueTextPart = (PartOfJiraIssueText) element;
			if (!issueTextPart.isValidated()) {
				nonValidatedElements.add(issueTextPart);
			}
		}

		LOGGER.info("Non-validated elements were viewed for Jira issue " + jiraIssueKey);
		return Response.ok(nonValidatedElements).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @return all parts of Jira issue text that are not manually approved for the
	 *         entire project. Iterates over all Jira issues in the project.
	 */
	@Path("/non-validated-elements/{projectKey}")
	@GET
	public Response getAllNonValidatedElements(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey) {

		if (request == null || projectKey == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Non-validated elements could not be found due to a bad request."))
					.build();
		}
		List<Issue> jiraIssuesPerProject = KnowledgePersistenceManager.getInstance(projectKey).getJiraIssueManager()
				.getAllJiraIssuesForProject();
		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		List<KnowledgeElement> nonValidatedElements = new ArrayList<KnowledgeElement>();

		for (Issue jiraIssue : jiraIssuesPerProject) {
			List<KnowledgeElement> elements = manager.getElementsInJiraIssue(jiraIssue.getId());
			for (KnowledgeElement element : elements) {
				PartOfJiraIssueText issueTextPart = (PartOfJiraIssueText) element;
				if (!issueTextPart.isValidated()) {
					nonValidatedElements.add(issueTextPart);
				}
			}
		}

		LOGGER.info("Non-validated elements were viewed for project " + projectKey);
		return Response.ok(nonValidatedElements).build();
	}

	/**
	 * @issue How should setting a single element "validated" be handled?
	 * @alternative Change the API of updateDecisionKnowledgeElement to allow this
	 *              attribute!
	 * @con This could be a breaking change
	 * @con This would make the code confusing
	 * @decision Make a new REST endpoint "setSentenceValidated"!
	 * @pro This would be backwards compatible
	 * @pro The code stays cleaner this way
	 * @con It might be confusing that this is documented as part of the SF: Change
	 *      decision knowledge element, but not inside the function of the same name
	 * 
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param knowledgeElement
	 *            JSON object containing at least the id, documentation location.
	 * @return ok if setting the sentence validated was successful
	 */
	@Path("/validate")
	@POST
	public Response setSentenceValidated(@Context HttpServletRequest request, KnowledgeElement knowledgeElement) {
		if (request == null || knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Setting element validated failed due to a bad request.")).build();
		}
		if (knowledgeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Only decision knowledge elements documented in the description "
							+ "or comments of a Jira issue can be set to validated."))
					.build();
		}

		String projectKey = knowledgeElement.getProject().getProjectKey();
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getJiraIssueTextManager();
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) persistenceManager.getKnowledgeElement(knowledgeElement);
		if (sentence == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Element could not be found in database.")).build();
		}

		LOGGER.info(sentence + " was manually approved.");

		sentence.setValidated(true);
		persistenceManager.updateInDatabase(sentence);
		persistenceManager.createLinksForNonLinkedElements(sentence.getJiraIssue());
		return Response.ok().build();
	}
}