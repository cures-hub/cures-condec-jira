package de.uhd.ifi.se.decision.management.jira.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.classification.OnlineTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;

/**
 * REST resource for plug-in configuration
 */
@Path("/config")
public class ConfigRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

	@Path("/setActivated")
	@POST
	public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") String isActivatedString) {
		Response response = this.checkRequest(request, projectKey, isActivatedString);
		if (response != null) {
			return response;
		}
		boolean isActivated = Boolean.valueOf(isActivatedString);
		ConfigPersistenceManager.setActivated(projectKey, isActivated);
		setDefaultKnowledgeTypesEnabled(projectKey, isActivated);
		resetKnowledgeGraph(projectKey);
		return Response.ok(Status.ACCEPTED).build();
	}

	private Response checkRequest(HttpServletRequest request, String projectKey, String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		return null;
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

	@Path("/isActivated")
	@GET
	public Response isActivated(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isActivated = ConfigPersistenceManager.isActivated(projectKey);
		return Response.ok(isActivated).build();
	}

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
		boolean isIssueStrategy = Boolean.valueOf(isIssueStrategyString);
		ConfigPersistenceManager.setIssueStrategy(projectKey, isIssueStrategy);
		manageDefaultIssueTypes(projectKey, isIssueStrategy);
		return Response.ok(Status.ACCEPTED).build();
	}

	public static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), true);
				PluginInitializer.createIssueType(knowledgeType.toString());
				PluginInitializer.addIssueTypeToScheme(knowledgeType.toString(), projectKey);
			} else {
				PluginInitializer.removeIssueTypeFromScheme(knowledgeType.toString(), projectKey);
			}
		}
	}

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
	}

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

	@Path("/getDecisionGroups")
	@GET
	public Response getDecisionGroups(@QueryParam("elementId") long id, @QueryParam("location") String location,
			@QueryParam("projectKey") String projectKey) {
		if (id == -1 || location == null || projectKey == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey).getKnowledgeElement(id,
				location);
		if (element != null) {
			List<String> groups = element.getDecisionGroups();
			if (groups != null) {
				for (String group : groups) {
					if (("High_Level").equals(group) || ("Medium_Level").equals(group)
							|| ("Realization_Level").equals(group)) {
						int index = groups.indexOf(group);
						if (index != 0) {
							Collections.swap(groups, 0, index);
						}
					}
				}
				return Response.ok(groups).build();
			}
		}
		return Response.ok(Collections.emptyList()).build();
	}

	@Path("/getAllDecisionElementsWithCertainGroup")
	@GET
	public Response getAllDecisionElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupManager.getAllDecisionElementsWithCertainGroup(group, projectKey);
		if (keys == null) {
			return Response.ok(Collections.emptyList()).build();
		} else {
			return Response.ok(keys).build();
		}
	}

	@Path("/getAllClassElementsWithCertainGroup")
	@GET
	public Response getAllClassElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupManager.getAllClassElementsWithCertainGroup(group, projectKey);
		if (keys == null) {
			return Response.ok(Collections.emptyList()).build();
		} else {
			return Response.ok(keys).build();
		}
	}

	@Path("/renameDecisionGroup")
	@GET
	public Response renameDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			return Response.ok(true).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No Group to Rename found"))
					.build();
		}
	}

	@Path("/deleteDecisionGroup")
	@GET
	public Response deleteDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		System.out.println("Group: " + groupName);
		System.out.println("ProjectKey: " + projectKey);
		if (DecisionGroupManager.deleteGroup(groupName, projectKey)) {
			return Response.ok(true).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No Group to Rename found"))
					.build();
		}
	}

	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		List<String> groups = DecisionGroupManager.getAllDecisionGroups(projectKey);
		if (groups == null) {
			return Response.ok(Collections.emptyList()).build();
		} else {
			return Response.ok(groups).build();
		}
	}

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
		boolean isActivated = Boolean.valueOf(isActivatedString);
		ConfigPersistenceManager.setWebhookEnabled(projectKey, isActivated);
		return Response.ok(Status.ACCEPTED).build();
	}

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
		ConfigPersistenceManager.setWebhookUrl(projectKey, webhookUrl);
		ConfigPersistenceManager.setWebhookSecret(projectKey, webhookSecret);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setWebhookType")
	@POST
	public Response setWebhookType(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookType") String webhookType,
			@QueryParam("isWebhookTypeEnabled") boolean isWebhookTypeEnabled) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setWebhookType(projectKey, webhookType, isWebhookTypeEnabled);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setReleaseNoteMapping")
	@POST
	public Response setReleaseNoteMapping(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("releaseNoteCategory") ReleaseNoteCategory category, List<String> selectedIssueNames) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setReleaseNoteMapping(projectKey, category, selectedIssueNames);
		return Response.ok(Status.ACCEPTED).build();
	}

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

	@Path("/setIconParsing")
	@POST
	public Response setIconParsing(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivatedString") String isActivatedString) {
		Response response = this.checkRequest(request, projectKey, isActivatedString);
		if (response == null) {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistenceManager.setIconParsing(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} else {
			return response;
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

	/* **************************************/
	/*										*/
	/* Configuration for Git integration */
	/*										*/
	/* **************************************/

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
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey, Boolean.valueOf(isKnowledgeExtractedFromGit));
		// deactivate other git extraction if false
		if (!Boolean.valueOf(isKnowledgeExtractedFromGit)) {
			ConfigPersistenceManager.setPostFeatureBranchCommits(projectKey, false);
			ConfigPersistenceManager.setPostSquashedCommits(projectKey, false);
		}
		return Response.ok(Status.ACCEPTED).build();
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
		if (Boolean.parseBoolean(ConfigPersistenceManager.getValue(projectKey, "isKnowledgeExtractedFromGit"))) {
			ConfigPersistenceManager.setPostFeatureBranchCommits(projectKey, Boolean.valueOf(checked));
			return Response.ok(Status.ACCEPTED).build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
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
		if (Boolean.parseBoolean(ConfigPersistenceManager.getValue(projectKey, "isKnowledgeExtractedFromGit"))) {
			ConfigPersistenceManager.setPostSquashedCommits(projectKey, Boolean.valueOf(checked));
			return Response.ok(Status.ACCEPTED).build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
		}
	}

	@Path("/setGitUris")
	@POST
	public Response setGitUris(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("gitUris") String gitUris, @QueryParam("defaultBranches") String defaultBranches) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (gitUris == null || defaultBranches == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Git URI could not be set because it is null.")).build();
		}
		// List<String> gitUriList = Arrays.asList(gitUris.split(";;"));
		ConfigPersistenceManager.setGitUris(projectKey, gitUris);
		ConfigPersistenceManager.setDefaultBranches(projectKey, defaultBranches);
		return Response.ok(Status.ACCEPTED).build();
	}

	/* **************************************/
	/*										*/
	/* Configuration for Classifier */
	/*										*/
	/* **************************************/

	@Path("/setUseClassifierForIssueComments")
	@POST
	public Response setUseClassifierForIssueComments(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isClassifierUsedForIssues") String isActivatedString) {
		Response response = this.checkRequest(request, projectKey, isActivatedString);
		if (response != null) {
			return response;
		}
		boolean isActivated = Boolean.valueOf(isActivatedString);
		ConfigPersistenceManager.setUseClassifierForIssueComments(projectKey, isActivated);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/trainClassifier")
	@POST
	public Response trainClassifier(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("arffFileName") String arffFileName) {// , @Suspended final AsyncResponse asyncResponse) {

		Response returnResponse;
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Response.Status.OK.getStatusCode()) {
			returnResponse = isValidDataResponse;
		} else if (arffFileName == null || arffFileName.isEmpty()) {
			returnResponse = Response.status(Response.Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"The classifier could not be trained since the ARFF file name is invalid.")).build();
		} else {
			ConfigPersistenceManager.setArffFileForClassifier(projectKey, arffFileName);

			OnlineTrainer trainer = new OnlineFileTrainerImpl(projectKey, arffFileName);
			boolean isTrained = trainer.train();

			if (isTrained) {
				returnResponse = Response.ok(Response.Status.ACCEPTED).entity(ImmutableMap.of("isSucceeded", true))
						.build();
			} else {
				returnResponse = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
						"The classifier could not be trained due to an internal server error.")).build();
			}
		}

		return returnResponse;
	}

	@Path("/evaluateModel")
	@POST
	public Response evaluateModel(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl(projectKey);

		try {
			Map<String, Double> evaluationResults = trainer.evaluateClassifier();

			if (evaluationResults.size() == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "No evaluation results were calculated!")).build();
			}

			String prefix = "";
			StringBuilder prettyMapOutput = new StringBuilder();
			prettyMapOutput.append("{");
			for (Map.Entry<String, Double> e : evaluationResults.entrySet()) {
				prettyMapOutput
						.append(prefix + System.lineSeparator() + "\"" + e.getKey() + "\" : \"" + e.getValue() + "\"");
				prefix = ",";
			}
			prettyMapOutput.append(System.lineSeparator() + "}");

			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("content", prettyMapOutput.toString())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", e.getMessage()))
					.build();
		}
	}

	@Path("/testClassifierWithText")
	@POST
	public Response testClassifierWithText(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("text") String text) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		try {
			OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl(projectKey);

			StringBuilder builder = new StringBuilder();
			List<String> textList = Collections.singletonList(text);

			Boolean relevant = trainer.getClassifier().makeBinaryPredictions(textList).get(0);
			builder.append(relevant ? "Relevant" : "Irrelevant");

			if (relevant) {
				builder.append(": ");
				KnowledgeType type = trainer.getClassifier().makeFineGrainedPredictions(textList).get(0);
				builder.append(type.toString());
			}
			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("content", builder.toString())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", e.getMessage()))
					.build();
		}
	}

	@Path("/saveArffFile")
	@POST
	public Response saveArffFile(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("useOnlyValidatedData") boolean useOnlyValidatedData) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl(projectKey);
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

	@Path("/classifyWholeProject")
	@POST
	public Response classifyWholeProject(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		if (!ConfigPersistenceManager.isClassifierEnabled(projectKey)) {
			return Response.status(Status.FORBIDDEN)
					.entity(ImmutableMap.of("error", "Automatic classification is disabled for this project.")).build();
		}
		try {
			ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
			ClassificationManagerForJiraIssueComments classificationManager = new ClassificationManagerForJiraIssueComments();
			for (Issue issue : JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey)) {
				classificationManager.classifyAllCommentsOfJiraIssue(issue);
			}

			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("isSucceeded", true)).build();
		} catch (Exception e) {
			LOGGER.error("Failed to classify the whole project. Message: " + e.getMessage());
			return Response.status(Status.CONFLICT).entity(ImmutableMap.of("isSucceeded", false)).build();
		}
	}

	/* **************************************/
	/*										*/
	/* Configuration for Consistency		*/
	/*										*/
	/* **************************************/
	@Path("/setConsistencyActivated")
	@POST
	public Response setConsistencyActivated(@Context HttpServletRequest request,
											@QueryParam("projectKey") String projectKey,
											@QueryParam("isConsistencyActivated") String isActivatedString) {
		Response response = this.checkRequest(request, projectKey, isActivatedString);
		if (response != null) {
			return response;
		}
		boolean isActivated = Boolean.valueOf(isActivatedString);
		ConfigPersistenceManager.setConsistencyActivated(projectKey, isActivated);
		return Response.ok(Status.ACCEPTED).build();
	}

}