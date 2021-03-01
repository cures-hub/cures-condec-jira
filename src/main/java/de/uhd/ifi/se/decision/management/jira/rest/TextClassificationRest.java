package de.uhd.ifi.se.decision.management.jira.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationManagerForJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassificationConfiguration;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import smile.validation.ClassificationMetrics;

/**
 * REST resource for text classification and its configuration.
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
			@QueryParam("trainingFileName") String trainingFileName) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (trainingFileName == null || trainingFileName.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"The classifier could not be trained since the training file name is invalid.")).build();
		}
		ConfigPersistenceManager.setTrainingFileForClassifier(projectKey, trainingFileName);
		TextClassifier trainer = new TextClassifier(projectKey, trainingFileName);
		if (trainer.train()) {
			return Response.ok().build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "The classifier could not be trained.")).build();
	}

	@Path("/evaluateTextClassifier")
	@POST
	public Response evaluateTextClassifier(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("trainingFileName") String trainingFileName,
			@QueryParam("numberOfFolds") int numberOfFolds) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		TextClassifier trainer = new TextClassifier(projectKey, trainingFileName);
		Map<String, ClassificationMetrics> evaluationResults = trainer.evaluateClassifier(numberOfFolds);
		String evaluationResultsMessage = "Ground truth file name: " + trainingFileName + " ";
		evaluationResultsMessage += numberOfFolds > 1 ? "Number of folds k = " + numberOfFolds : "\n\r";
		evaluationResultsMessage += evaluationResults.toString();
		TextClassificationConfiguration config = ConfigPersistenceManager
				.getTextClassificationConfiguration(projectKey);
		config.setLastEvaluationResults(evaluationResultsMessage);
		ConfigPersistenceManager.saveTextClassificationConfiguration(projectKey, config);
		return Response.ok(ImmutableMap.of("content", evaluationResultsMessage)).build();
	}

	@Path("/classifyText")
	@POST
	public Response classifyText(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("text") String text) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		TextClassifier classifier = TextClassifier.getInstance(projectKey);
		boolean isRelevant = classifier.getBinaryClassifier().predict(text);
		String classificationResult = isRelevant ? "Relevant" : "Irrelevant";
		if (isRelevant) {
			KnowledgeType type = classifier.getFineGrainedClassifier().predict(text);
			classificationResult += ": " + type.toString();
		}
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
		if (!Boolean.valueOf(config.isActivated())) {
			return Response.status(Status.FORBIDDEN)
					.entity(ImmutableMap.of("error", "Automatic classification is disabled for this project.")).build();
		}
		ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		ClassificationManagerForJiraIssueText classificationManager = new ClassificationManagerForJiraIssueText(
				projectKey);
		for (Issue issue : JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey)) {
			classificationManager.classifyDescriptionAndAllComments(issue);
		}
		return Response.ok().build();
	}
}
