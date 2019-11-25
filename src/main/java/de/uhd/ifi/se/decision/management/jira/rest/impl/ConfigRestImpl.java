package de.uhd.ifi.se.decision.management.jira.rest.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraSearchServiceHelper;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;

/**
 * REST resource for plug-in configuration
 */
@Path("/config")
public class ConfigRestImpl implements ConfigRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

	@Override
	@Path("/setActivated")
	@POST
	public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		boolean isActivated = Boolean.valueOf(isActivatedString);
		ConfigPersistenceManager.setActivated(projectKey, isActivated);
		setDefaultKnowledgeTypesEnabled(projectKey, isActivated);
		resetKnowledgeGraph(projectKey);
		return Response.ok(Status.ACCEPTED).build();
	}

	private static void setDefaultKnowledgeTypesEnabled(String projectKey, boolean isActivated) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), isActivated);
		}
	}

	private static void resetKnowledgeGraph(String projectKey) {
		KnowledgeGraph.instances.remove(projectKey);
		KnowledgePersistenceManager.instances.remove(projectKey);
	}

	@Override
	@Path("/isIssueStrategy")
	@GET
	public Response isIssueStrategy(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isIssueStrategy = ConfigPersistenceManager.isIssueStrategy(projectKey);
		return Response.ok(isIssueStrategy).build();
	}

	@Override
	@Path("/setIssueStrategy")
	@POST
	public Response setIssueStrategy(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isIssueStrategy") String isIssueStrategyString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isIssueStrategyString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null"))
					.build();
		}
		try {
			boolean isIssueStrategy = Boolean.valueOf(isIssueStrategyString);
			ConfigPersistenceManager.setIssueStrategy(projectKey, isIssueStrategy);
			ConfigRest.manageDefaultIssueTypes(projectKey, isIssueStrategy);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the JIRA issue persistence strategy. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setKnowledgeExtractedFromGit")
	@POST
	public Response setKnowledgeExtractedFromGit(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromGit") String isKnowledgeExtractedFromGit) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeExtractedFromGit == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "isKnowledgeExtractedFromGit = null")).build();
		}
		try {
			ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey,
					Boolean.valueOf(isKnowledgeExtractedFromGit));
			// deactivate other git extraction
			ConfigPersistenceManager.setPostFeatureBranchCommits(projectKey, false);
			ConfigPersistenceManager.setPostSquashedCommits(projectKey, false);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the knowledge extraction from git. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/setPostFeatureBranchCommits")
	@POST
	public Response setPostFeatureBranchCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("newSetting") String checked) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (checked == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "PostFeatureBranchCommits-checked = null")).build();
		}

		try {
			if (Boolean.parseBoolean(ConfigPersistenceManager.getValue(projectKey, "isKnowledgeExtractedFromGit"))) {
				ConfigPersistenceManager.setPostFeatureBranchCommits(projectKey, Boolean.valueOf(checked));
				return Response.ok(Status.ACCEPTED).build();
			} else {
				return Response.status(Status.CONFLICT)
						.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
			}

		} catch (Exception e) {
			LOGGER.error(
					"Failed to enable or disable the setting PostFeatureBranchCommits. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/setPostSquashedCommits")
	@POST
	public Response setPostSquashedCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("newSetting") String checked) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (checked == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "setPostSquashedCommits-checked = null")).build();
		}
		try {
			if (Boolean.parseBoolean(ConfigPersistenceManager.getValue(projectKey, "isKnowledgeExtractedFromGit"))) {
				ConfigPersistenceManager.setPostSquashedCommits(projectKey, Boolean.valueOf(checked));
				return Response.ok(Status.ACCEPTED).build();
			} else {
				return Response.status(Status.CONFLICT)
						.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
			}

		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the setting PostSquashedCommits. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setGitUri")
	@POST
	public Response setGitUri(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("gitUri") String gitUri) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (gitUri == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Git URI could not be set because it is null.")).build();
		}
		ConfigPersistenceManager.setGitUri(projectKey, gitUri);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Override
	@Path("/setKnowledgeExtractedFromIssues")
	@POST
	public Response setKnowledgeExtractedFromIssues(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromIssues") String isKnowledgeExtractedFromIssues) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeExtractedFromIssues == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "isKnowledgeExtractedFromIssues = null")).build();
		}
		try {
			ConfigPersistenceManager.setKnowledgeExtractedFromIssues(projectKey,
					Boolean.valueOf(isKnowledgeExtractedFromIssues));
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the extraction of knowledge from JIRA issues. Message: "
					+ e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/isKnowledgeTypeEnabled")
	@GET
	public Response isKnowledgeTypeEnabled(@QueryParam("projectKey") String projectKey,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Boolean isKnowledgeTypeEnabled = ConfigPersistenceManager.isKnowledgeTypeEnabled(projectKey, knowledgeType);
		return Response.ok(isKnowledgeTypeEnabled).build();
	}

	@Override
	@Path("/setKnowledgeTypeEnabled")
	@POST
	public Response setKnowledgeTypeEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeTypeEnabled") String isKnowledgeTypeEnabledString,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeTypeEnabledString == null || knowledgeType == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isKnowledgeTypeEnabled = null"))
					.build();
		}
		try {
			boolean isKnowledgeTypeEnabled = Boolean.valueOf(isKnowledgeTypeEnabledString);
			ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType, isKnowledgeTypeEnabled);
			if (ConfigPersistenceManager.isIssueStrategy(projectKey)) {
				if (isKnowledgeTypeEnabled) {
					PluginInitializer.createIssueType(knowledgeType);
					PluginInitializer.addIssueTypeToScheme(knowledgeType, projectKey);
				} else {
					PluginInitializer.removeIssueTypeFromScheme(knowledgeType, projectKey);
				}
			}
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable the knowledge type: " + knowledgeType + " Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/getKnowledgeTypes")
	@GET
	public Response getKnowledgeTypes(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		List<String> knowledgeTypes = new ArrayList<String>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistenceManager.isKnowledgeTypeEnabled(projectKey, knowledgeType);
			if (isEnabled) {
				knowledgeTypes.add(knowledgeType.toString());
			}
		}
		return Response.ok(knowledgeTypes).build();
	}

	@Override
	@Path("/getLinkTypes")
	@GET
	public Response getLinkTypes(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Map<String, String> linkTypes = new HashMap<>();
		for (LinkType linkType : LinkType.values()) {
			linkTypes.put(linkType.getName(), linkType.getColor());
		}
		return Response.ok(linkTypes).build();
	}

	@Override
	@Path("/setWebhookEnabled")
	@POST
	public Response setWebhookEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Webhook activation boolean = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistenceManager.setWebhookEnabled(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the webhook. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setWebhookData")
	@POST
	public Response setWebhookData(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookUrl") String webhookUrl, @QueryParam("webhookSecret") String webhookSecret) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (webhookUrl == null || webhookSecret == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "webhook Data = null")).build();
		}
		try {
			ConfigPersistenceManager.setWebhookUrl(projectKey, webhookUrl);
			ConfigPersistenceManager.setWebhookSecret(projectKey, webhookSecret);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to set the webhook data. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setWebhookType")
	@POST
	public Response setWebhookType(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookType") String webhookType,
			@QueryParam("isWebhookTypeEnabled") boolean isWebhookTypeEnabled) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		try {
			ConfigPersistenceManager.setWebhookType(projectKey, webhookType, isWebhookTypeEnabled);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setReleaseNoteMapping")
	@POST
	public Response setReleaseNoteMapping(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("releaseNoteCategory") ReleaseNoteCategory category, List<String> selectedIssueNames) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		try {
			ConfigPersistenceManager.setReleaseNoteMapping(projectKey, category, selectedIssueNames);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/getReleaseNoteMapping")
	@GET
	public Response getReleaseNoteMapping(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Map<ReleaseNoteCategory, List<String>> mapping = new HashMap<>();
		ReleaseNoteCategory.toOriginalList().forEach(category -> {
			mapping.put(category, ConfigPersistenceManager.getReleaseNoteMapping(projectKey, category));
		});
		return Response.ok(mapping).build();
	}

	@Override
	@Path("/cleanDatabases")
	@POST
	public Response cleanDatabases(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		ApplicationUser user = AuthenticationManager.getUser(request);
		persistenceManager.deleteInvalidElements(user);
		GenericLinkManager.deleteInvalidLinks();
		// If there are some "lonely" sentences, link them to their Jira issues.
		persistenceManager.createLinksForNonLinkedElements();
		return Response.ok(Status.ACCEPTED).build();
	}

	@Override
	@Path("/classifyWholeProject")
	@POST
	public Response classifyWholeProject(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		if (!ConfigPersistenceManager.isUseClassiferForIssueComments(projectKey)) {
			return Response.status(Status.FORBIDDEN)
					.entity(ImmutableMap.of("error", "Automatic classification is disabled for this project.")).build();
		}
		try {
			ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
			JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
			SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);

			Query query = jqlClauseBuilder.project(projectKey).buildQuery();
			SearchResults<Issue> searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());

			ClassificationManagerForJiraIssueComments classificationManager = new ClassificationManagerForJiraIssueComments();
			for (Issue issue : JiraSearchServiceHelper.getJiraIssues(searchResults)) {
				classificationManager.classifyAllCommentsOfJiraIssue(issue);
			}

			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("isSucceeded", true)).build();
		} catch (Exception e) {
			LOGGER.error("Failed to classify the whole project. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).entity(ImmutableMap.of("isSucceeded", false)).build();
		}
	}

	@Override
	@Path("/trainClassifier")
	@POST
	public Response trainClassifier(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("arffFileName") String arffFileName) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (arffFileName == null || arffFileName.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"The classifier could not be trained since the ARFF file name is invalid.")).build();
		}
		ConfigPersistenceManager.setArffFileForClassifier(projectKey, arffFileName);
		ClassificationTrainer trainer = new OnlineClassificationTrainerImpl(projectKey, arffFileName);
		boolean isTrained = trainer.train();
		if (isTrained) {
			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("isSucceeded", true)).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "The classifier could not be trained due to an internal server error."))
				.build();
	}

	@Override
	@Path("/evaluateModel")
	@POST
	public Response evaluateModel(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		OnlineClassificationTrainerImpl trainer = new OnlineClassificationTrainerImpl(projectKey);

		try {
			Map<String, Double> evaluationResults = trainer.evaluateClassifier();

			StringBuilder prettyMapOutput = new StringBuilder();
			prettyMapOutput.append("{" + System.lineSeparator());
			for (Map.Entry<String, Double> e : evaluationResults.entrySet()) {
				prettyMapOutput.append("\"" + e.getKey() + "\" : \"" + e.getValue() + "\"," + System.lineSeparator());
			}
			prettyMapOutput.append("}");

			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("content", prettyMapOutput.toString())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", e.getMessage()))
					.build();
		}
	}

	@Override
	@Path("/saveArffFile")
	@POST
	public Response saveArffFile(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("useOnlyValidatedData") boolean useOnlyValidatedData) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		OnlineClassificationTrainerImpl trainer = new OnlineClassificationTrainerImpl(projectKey);
		File arffFile = trainer.saveTrainingFile(useOnlyValidatedData);

		if (arffFile != null) {
			return Response.ok(Status.ACCEPTED).entity(
					ImmutableMap.of("arffFile", arffFile.toString(), "content", trainer.getInstances().toString()))
					.build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "ARFF file could not be created because of an internal server error."))
				.build();
	}

	@Override
	@Path("/setIconParsing")
	@POST
	public Response setIconParsing(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivatedString") String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistenceManager.setIconParsing(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable icon parsing. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Override
	@Path("/setUseClassifierForIssueComments")
	@POST
	public Response setUseClassifierForIssueComments(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isClassifierUsedForIssues") String isActivatedString) {
		System.out.println(isActivatedString);
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistenceManager.setUseClassifierForIssueComments(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error("Failed to enable or disable the classifier for JIRA issue text. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	private Response checkIfDataIsValid(HttpServletRequest request, String projectKey) {
		if (request == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
		}

		Response projectResponse = checkIfProjectKeyIsValid(projectKey);
		if (projectResponse.getStatus() != Status.OK.getStatusCode()) {
			return projectResponse;
		}

		Response userResponse = checkIfUserIsAuthorized(request, projectKey);
		if (userResponse.getStatus() != Status.OK.getStatusCode()) {
			return userResponse;
		}

		return Response.status(Status.OK).build();
	}

	private Response checkIfUserIsAuthorized(HttpServletRequest request, String projectKey) {
		String username = AuthenticationManager.getUsername(request);
		boolean isProjectAdmin = AuthenticationManager.isProjectAdmin(username, projectKey);
		if (isProjectAdmin) {
			return Response.status(Status.OK).build();
		}
		LOGGER.warn("Unauthorized user (name:{}) tried to change configuration.", username);
		return Response.status(Status.UNAUTHORIZED).entity(ImmutableMap.of("error", "Authorization failed.")).build();
	}

	private Response checkIfProjectKeyIsValid(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			LOGGER.error("Project configuration could not be changed since the project key is invalid.");
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Project key is invalid."))
					.build();
		}
		return Response.status(Status.OK).build();
	}
}