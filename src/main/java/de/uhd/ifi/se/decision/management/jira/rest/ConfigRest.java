package de.uhd.ifi.se.decision.management.jira.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.ClassificationManagerForJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.classification.ClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.TextClassifier;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.QualityCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * REST resource for plug-in configuration
 */
@Path("/config")
public class ConfigRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

	@Path("/setActivated")
	@POST
	public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		LOGGER.info("ConDec activation was set to " + isActivated + " for project " + projectKey);
		ConfigPersistenceManager.setActivated(projectKey, isActivated);
		setDefaultKnowledgeTypesEnabled(projectKey, isActivated);
		ComponentGetter.removeInstances(projectKey);
		return Response.ok().build();
	}

	private static void setDefaultKnowledgeTypesEnabled(String projectKey, boolean isActivated) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), isActivated);
		}
	}

	@Path("/isActivated")
	@GET
	public Response isActivated(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isActivated = ConfigPersistenceManager.isActivated(projectKey);
		return Response.ok(isActivated).build();
	}

	@Path("/isIssueStrategy")
	@GET
	public Response isIssueStrategy(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isIssueStrategy = ConfigPersistenceManager.isIssueStrategy(projectKey);
		return Response.ok(isIssueStrategy).build();
	}

	@Path("/setIssueStrategy")
	@POST
	public Response setIssueStrategy(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isIssueStrategy") boolean isIssueStrategy) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setIssueStrategy(projectKey, isIssueStrategy);
		manageDefaultIssueTypes(projectKey, isIssueStrategy);
		return Response.ok().build();
	}

	public static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		JiraSchemeManager jiraSchemeManager = new JiraSchemeManager(projectKey);
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), true);
				IssueType jiraIssueType = JiraSchemeManager.createIssueType(knowledgeType.toString());
				jiraSchemeManager.addIssueTypeToScheme(jiraIssueType);
			} else {
				jiraSchemeManager.removeIssueTypeFromScheme(knowledgeType.toString());
			}
		}
	}

	@Path("/isKnowledgeTypeEnabled")
	@GET
	public Response isKnowledgeTypeEnabled(@QueryParam("projectKey") String projectKey,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (knowledgeType == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "The knowledge type is null."))
					.build();
		}
		boolean isKnowledgeTypeEnabled = ConfigPersistenceManager.isKnowledgeTypeEnabled(projectKey, knowledgeType);
		return Response.ok(isKnowledgeTypeEnabled).build();
	}

	@Path("/setKnowledgeTypeEnabled")
	@POST
	public Response setKnowledgeTypeEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeTypeEnabled") boolean isKnowledgeTypeEnabled,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (knowledgeType == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge type could not be enabled because it is null."))
					.build();
		}
		ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType, isKnowledgeTypeEnabled);
		if (ConfigPersistenceManager.isIssueStrategy(projectKey)) {
			JiraSchemeManager jiraSchemeManager = new JiraSchemeManager(projectKey);
			if (isKnowledgeTypeEnabled) {
				IssueType jiraIssueType = JiraSchemeManager.createIssueType(knowledgeType);
				jiraSchemeManager.addIssueTypeToScheme(jiraIssueType);
			} else {
				jiraSchemeManager.removeIssueTypeFromScheme(knowledgeType);
			}
		}
		return Response.ok().build();
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 * @return all knowledge types including Jira issue types such as work items
	 *         (tasks) or requirements.
	 */
	@Path("/getKnowledgeTypes")
	@GET
	public Response getKnowledgeTypes(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Set<String> knowledgeTypesAsString = new DecisionKnowledgeProject(projectKey).getNamesOfKnowledgeTypes();
		return Response.ok(knowledgeTypesAsString).build();
	}

	/**
	 * @param projectKey
	 *            of the Jira project.
	 * @return all decision knowledge (=rationale) types such as issue (=decision
	 *         problem), alternative, decision, and argument.
	 */
	@Path("/getDecisionKnowledgeTypes")
	@GET
	public Response getDecisionKnowledgeTypes(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Set<String> rationaleTypesAsString = new DecisionKnowledgeProject(projectKey).getNamesOfConDecKnowledgeTypes();
		return Response.ok(rationaleTypesAsString).build();
	}

	@Path("/setDecisionTableCriteriaQuery")
	@POST
	public Response setDecisionTableCriteriaQuery(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("query") String query) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		JiraQueryHandler queryHandler = new JiraQueryHandler(user, projectKey, "?jql=" + query);
		int numberOfCriteria = queryHandler.getJiraIssuesFromQuery().size();
		ConfigPersistenceManager.setDecisionTableCriteriaQuery(projectKey, query);
		return Response.ok(numberOfCriteria).build();
	}

	@Path("/isLinkTypeEnabled")
	@GET
	public Response isLinkTypeEnabled(@QueryParam("projectKey") String projectKey,
			@QueryParam("linkType") String linkType) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		boolean isLinkTypeEnabled = issueLinkTypeManager.getIssueLinkTypes().stream().map(IssueLinkType::getName)
				.anyMatch(e -> e.equals(linkType));
		return Response.ok().entity(isLinkTypeEnabled).build();
	}

	@Path("/setLinkTypeEnabled")
	@POST
	public Response setLinkTypeEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isLinkTypeEnabled") boolean isLinkTypeEnabled, @QueryParam("linkType") String linkType) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (linkType == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The link type could not be enabled because it is null.")).build();
		}
		JiraSchemeManager jiraSchemeManager = new JiraSchemeManager(projectKey);
		if (isLinkTypeEnabled) {
			JiraSchemeManager.createLinkType(linkType);
			jiraSchemeManager.addLinkTypeToScheme(linkType);
		} else {
			jiraSchemeManager.removeLinkTypeFromScheme(linkType);
		}
		return Response.ok().build();
	}

	/**
	 * @return all available link types for a project. That covers the
	 *         {@link LinkType}s for linking decision knowledge elements (=rationale
	 *         elements) and also other Jira issue links in the project.
	 * @issue How can we access the availiable link types in the client
	 *        side/frontend of the plugin?
	 * @decision We provide our own getLinkTypes REST API!
	 * @pro In the future, we will have transitive links in the knowledge graph. The
	 *      link type "transitive" needs to be added, which will not be a real Jira
	 *      issue link type.
	 * @pro Easy to extend.
	 * @alternative Jira API could be called using GET "/rest/api/2/issueLinkType"!
	 *              This call "returns a list of available issue link types. Each
	 *              issue link type has an id, a name and a label for the outward
	 *              and inward link relationship."
	 * @con All link types need to be Jira issue links, which might be problematic
	 *      if we model transitive links or links to code classes. *
	 */
	@Path("/getLinkTypes")
	@GET
	public Response getLinkTypes(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Set<String> linkTypes = DecisionKnowledgeProject.getNamesOfLinkTypes();
		return Response.ok(linkTypes).build();
	}

	// TODO Refactor: too many ifs
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
		return Response.ok(keys).build();
	}

	@Path("/getAllClassElementsWithCertainGroup")
	@GET
	public Response getAllClassElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupManager.getAllClassElementsWithCertainGroup(group, projectKey);
		return Response.ok(keys).build();
	}

	@Path("/renameDecisionGroup")
	@GET
	public Response renameDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to rename found")).build();
	}

	@Path("/deleteDecisionGroup")
	@GET
	public Response deleteDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		if (DecisionGroupManager.deleteGroup(groupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to delete found")).build();
	}

	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		List<String> groups = DecisionGroupManager.getAllDecisionGroups(projectKey);
		return Response.ok(groups).build();
	}

	@Path("/setWebhookEnabled")
	@POST
	public Response setWebhookEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") String isActivatedString) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Webhook activation boolean = null")).build();
		}
		boolean isActivated = Boolean.parseBoolean(isActivatedString);
		ConfigPersistenceManager.setWebhookEnabled(projectKey, isActivated);
		return Response.ok().build();
	}

	@Path("/setWebhookData")
	@POST
	public Response setWebhookData(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookUrl") String webhookUrl, @QueryParam("webhookSecret") String webhookSecret) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (webhookUrl == null || webhookSecret == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "webhook Data = null")).build();
		}
		ConfigPersistenceManager.setWebhookUrl(projectKey, webhookUrl);
		ConfigPersistenceManager.setWebhookSecret(projectKey, webhookSecret);
		return Response.ok(Status.OK).build();
	}

	@Path("/setWebhookType")
	@POST
	public Response setWebhookType(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookType") String webhookType,
			@QueryParam("isWebhookTypeEnabled") boolean isWebhookTypeEnabled) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setWebhookType(projectKey, webhookType, isWebhookTypeEnabled);
		return Response.ok().build();
	}

	@Path("/sendTestPost")
	@POST
	public Response sendTestPost(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		WebhookConnector connector = new WebhookConnector(projectKey);
		if (connector.sendTestPost()) {
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Test webhook post failed."))
				.build();
	}

	@Path("/setReleaseNoteMapping")
	@POST
	public Response setReleaseNoteMapping(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("releaseNoteCategory") ReleaseNotesCategory category, List<String> selectedIssueNames) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setReleaseNoteMapping(projectKey, category, selectedIssueNames);
		return Response.ok().build();
	}

	@Path("/releaseNoteMapping")
	@GET
	public Response getReleaseNoteMapping(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Map<ReleaseNotesCategory, List<String>> mapping = new HashMap<>();
		ReleaseNotesCategory.toOriginalList().forEach(category -> mapping.put(category,
				ConfigPersistenceManager.getReleaseNoteMapping(projectKey, category)));
		return Response.ok(mapping).build();
	}

	@Path("/cleanDatabases")
	@POST
	public Response cleanDatabases(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
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
		return Response.ok().build();
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
			@QueryParam("isKnowledgeExtractedFromGit") boolean isKnowledgeExtractedFromGit) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey, isKnowledgeExtractedFromGit);
		ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, "Code", isKnowledgeExtractedFromGit);

		// deactivate other git extraction if false
		if (!isKnowledgeExtractedFromGit) {
			GitClient.instances.remove(projectKey);
		} else {
			// clone or fetch the git repositories
			GitClient.getOrCreate(projectKey);
			// read all code files, decision knowledge elements and links from the default
			// branch
			new CodeFileExtractorAndMaintainer(projectKey).extractAllChangedFiles();
		}
		return Response.ok().build();
	}

	@Path("/setPostFeatureBranchCommits")
	@POST
	public Response setPostFeatureBranchCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("newSetting") String checked) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (checked == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "PostFeatureBranchCommits-checked = null")).build();
		}
		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			ConfigPersistenceManager.setPostFeatureBranchCommits(projectKey, Boolean.valueOf(checked));
			return Response.ok().build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
		}
	}

	@Path("/setPostSquashedCommits")
	@POST
	public Response setPostDefaultBranchCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("newSetting") String checked) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (checked == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "setPostDefaultBranchCommits-checked = null")).build();
		}

		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			boolean isActivated = Boolean.valueOf(checked);
			ConfigPersistenceManager.setPostSquashedCommits(projectKey, isActivated);
			if (isActivated) {
				ApplicationUser user = AuthenticationManager.getUser(request);
				List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey);
				jiraIssues.forEach(
						jiraIssue -> new CommitMessageToCommentTranscriber(jiraIssue).postDefaultBranchCommits());
			}
			return Response.ok().build();
		} else {
			return Response.status(Status.CONFLICT)
					.entity(ImmutableMap.of("error", "Git Extraction needs to be active!")).build();
		}
	}

	@Path("/setGitRepositoryConfigurations")
	@POST
	public Response setGitRepositoryConfigurations(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, List<GitRepositoryConfiguration> gitRepositoryConfigurations) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (gitRepositoryConfigurations == null
				|| !GitRepositoryConfiguration.areAllGitRepositoryConfigurationsValid(gitRepositoryConfigurations)) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Git repository configurations could not be set because it is null."))
					.build();
		}
		ConfigPersistenceManager.setGitRepositoryConfigurations(projectKey, gitRepositoryConfigurations);
		return Response.ok().build();
	}

	@Path("/deleteGitRepos")
	@POST
	public Response deleteGitRepos(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitClient gitClient = GitClient.getOrCreate(projectKey);
		if (!gitClient.deleteRepositories()) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Git repositories could not be deleted.")).build();
		}
		new CodeClassPersistenceManager(projectKey).deleteKnowledgeElements();
		return Response.ok().build();
	}

	/* **************************************/
	/*										*/
	/* Configuration for Classifier */
	/*										*/
	/* **************************************/

	@Path("/setTextClassifierEnabled")
	@POST
	public Response setTextClassifierEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("isTextClassifierEnabled") boolean isActivated) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ConfigPersistenceManager.setTextClassifierEnabled(projectKey, isActivated);
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
		ClassifierTrainer trainer = new ClassifierTrainer(projectKey, trainingFileName);
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

		ClassifierTrainer trainer = new ClassifierTrainer(projectKey, trainingFileName);
		Map<String, Double> evaluationResults = trainer.evaluateClassifier(numberOfFolds);

		if (evaluationResults.size() == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "No evaluation results were calculated!")).build();
		}

		String prefix = "";
		StringBuilder prettyMapOutput = new StringBuilder();
		prettyMapOutput.append("{");
		for (Map.Entry<String, Double> e : evaluationResults.entrySet()) {
			prettyMapOutput.append(prefix).append(System.lineSeparator()).append("\"").append(e.getKey())
			.append("\" : \"").append(e.getValue()).append("\"");
			prefix = ",";
		}
		prettyMapOutput.append(System.lineSeparator()).append("}");

		return Response.ok(ImmutableMap.of("content", prettyMapOutput.toString())).build();
	}

	@Path("/testClassifierWithText")
	@POST
	public Response testClassifierWithText(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("text") String text) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		StringBuilder builder = new StringBuilder();
		List<String> textList = Collections.singletonList(text);

		boolean relevant = TextClassifier.getInstance().getBinaryClassifier().predict(textList)[0];
		builder.append(relevant ? "Relevant" : "Irrelevant");

		if (relevant) {
			builder.append(": ");
			KnowledgeType type = TextClassifier.getInstance().getFineGrainedClassifier().predict(textList).get(0);
			builder.append(type.toString());
		}
		return Response.ok(ImmutableMap.of("content", builder.toString())).build();

	}

	@Path("/saveTrainingFile")
	@POST
	public Response saveTrainingFileForTextClassifier(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ClassifierTrainer trainer = new ClassifierTrainer(projectKey);
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
		if (!ConfigPersistenceManager.isTextClassifierEnabled(projectKey)) {
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

	/* **************************************/
	/*										*/
	/* Configuration for Consistency */
	/*										*/
	/* **************************************/
	@Path("/setMinimumLinkSuggestionProbability")
	@POST
	public Response setMinimumLinkSuggestionProbability(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("minLinkSuggestionProbability") double minLinkSuggestionProbability) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (1. < minLinkSuggestionProbability || minLinkSuggestionProbability < 0.) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The minimum of the score value is invalid.")).build();
		}

		ConfigPersistenceManager.setMinLinkSuggestionScore(projectKey, minLinkSuggestionProbability);
		return Response.ok().build();
	}

	@Path("/setMinimumDuplicateLength")
	@POST
	public Response setMinimumDuplicateLength(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("fragmentLength") int fragmentLength) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (fragmentLength < 3) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The minimum length for the duplicates is invalid.")).build();
		}
		ConfigPersistenceManager.setFragmentLength(projectKey, fragmentLength);
		return Response.ok().build();
	}

	/* **************************************/
	/*										*/
	/* Configuration for Decision Guidance */
	/*										*/
	/* **************************************/

	@Path("/setMaxNumberRecommendations")
	@POST
	public Response setMaxNumberRecommendations(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("maxNumberRecommendations") int maxNumberRecommendations) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (maxNumberRecommendations < 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The maximum number of results cannot be smaller 0.")).build();
		}

		ConfigPersistenceManager.setMaxNumberRecommendations(projectKey, maxNumberRecommendations);
		return Response.ok().build();
	}

	@Path("/setSimilarityThreshold")
	@POST
	public Response setSimilarityThreshold(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("threshold") double threshold) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (threshold < 0 || threshold > 1) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The threshold must be between 0 and 1.")).build();
		}

		ConfigPersistenceManager.setSimilarityThreshold(projectKey, threshold);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setIrrelevantWords")
	@POST
	public Response setIrrelevantWords(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("words") String words) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (words.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "The words should not be blank"))
					.build();
		}

		ConfigPersistenceManager.setIrrelevantWords(projectKey, words);
		return Response.ok(Status.ACCEPTED).build();
	}

	@Path("/setRDFKnowledgeSource")
	@POST
	public Response setRDFKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, String rdfSourceJSON) {

		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}

		// TODO Remove JSON conversion and directly pass RDFSource object instead of
		// String (see setDefinitionOfDone method)
		Gson gson = new Gson();
		// TODO Please avoid acronyms like "RDF" and rather write complete names
		RDFSource rdfSource = gson.fromJson(rdfSourceJSON, RDFSource.class);

		if (rdfSource == null || rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty")).build();
		}

		try {
			int timeout = Integer.parseInt(rdfSource.getTimeout());
			if (timeout <= 0) {
				return Response.status(Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", "The timeout must be greater zero!")).build();
			}

		} catch (NumberFormatException e) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The timeout must be an Integer")).build();
		}

		for (RDFSource rdfSourceCheck : ConfigPersistenceManager.getRDFKnowledgeSource(projectKey)) {
			if (rdfSourceCheck.getName().equals(rdfSource.getName()))
				return Response.status(Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", "The name of the knowledge already exists.")).build();
		}

		ConfigPersistenceManager.setRDFKnowledgeSource(projectKey, rdfSource);
		return Response.ok().build();
	}

	@Path("/deleteKnowledgeSource")
	@POST
	public Response deleteKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("knowledgeSourceName") String knowledgeSourceName) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (knowledgeSourceName.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		ConfigPersistenceManager.deleteKnowledgeSource(projectKey, knowledgeSourceName);
		return Response.ok().build();
	}

	@Path("/updateKnowledgeSource")
	@POST
	public Response updateKnowledgeSource(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("knowledgeSourceName") String knowledgeSourceName,
			String rdfSourceJSON) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}

		Gson gson = new Gson();
		RDFSource rdfSource = gson.fromJson(rdfSourceJSON, RDFSource.class);
		if (rdfSource.getName().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		ConfigPersistenceManager.updateKnowledgeSource(projectKey, knowledgeSourceName, rdfSource);
		return Response.ok().build();
	}

	@Path("/setKnowledgeSourceActivated")
	@POST
	public Response setKnowledgeSourceActivated(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("knowledgeSourceName") String knowledgeSourceName,
			@QueryParam("isActivated") boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (knowledgeSourceName.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The knowledge source must not be empty.")).build();
		}

		ConfigPersistenceManager.setRDFKnowledgeSourceActivation(projectKey, knowledgeSourceName, isActivated);
		return Response.ok().build();
	}

	@Path("/setProjectSource")
	@POST
	public Response setProjectSource(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("projectSourceKey") String projectSourceKey, @QueryParam("isActivated") boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (projectSourceKey.isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The Project Source must not be empty.")).build();
		}
		ConfigPersistenceManager.setProjectSource(projectKey, projectSourceKey, isActivated);
		return Response.ok().build();
	}

	@Path("/setAddRecommendationDirectly")
	@POST
	public Response setAddRecommendationDirectly(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("addRecommendationDirectly") String addRecommendationDirectly) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}

		ConfigPersistenceManager.setAddRecommendationDirectly(projectKey, Boolean.valueOf(addRecommendationDirectly));
		return Response.ok().build();
	}

	@Path("/setRecommendationInput")
	@POST
	public Response setRecommendationInput(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("recommendationInput") String recommendationInput,
			@QueryParam("isActivated") boolean isActivated) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}

		ConfigPersistenceManager.setRecommendationInput(projectKey, recommendationInput, isActivated);
		return Response.ok().build();
	}

	/* **************************************/
	/*										*/
	/* Configuration for Rationale Backlog */
	/*										*/
	/* **************************************/

	@Path("/setDefinitionOfDone")
	@POST
	public Response setDefinitionOfDone(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, DefinitionOfDone definitionOfDone) {

		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}

		if (definitionOfDone == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty")).build();
		}

		ConfigPersistenceManager.setDefinitionOfDone(projectKey, definitionOfDone);
		return Response.ok().build();
	}

	/* **********************************************************/
	/*															*/
	/* Configuration for quality = completeness + consistency */
	/*															*/
	/* **********************************************************/

	@Path("/isQualityEventActivated")
	@GET
	public Response isQualityEventActivated(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("eventKey") String eventKey) {

		Response isValidDataResponse = this.checkIfQualityTriggerRequestIsValid(request, projectKey, eventKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		boolean isActivated = ConfigPersistenceManager.getActivationStatusOfQualityEvent(projectKey, eventKey);
		return Response.ok().entity(ImmutableMap.of("isActivated", isActivated)).build();
	}

	@Path("/activateQualityEvent")
	@POST
	public Response activateQualityEvent(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("eventKey") String eventKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = this.checkIfQualityTriggerRequestIsValid(request, projectKey, eventKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		ConfigPersistenceManager.setActivationStatusOfQualityEvent(projectKey, eventKey, isActivated);
		return Response.ok().build();
	}

	private boolean checkIfQualityTriggerExists(String triggerName) {
		String triggerNameShort = triggerName;
		String[] triggerNameParts = triggerName.split("-");
		if (triggerNameParts.length > 0) {
			triggerNameShort = triggerNameParts[triggerNameParts.length - 1];
		}
		return ((QualityCheckEventListenerSingleton) QualityCheckEventListenerSingleton.getInstance())
				.doesQualityCheckEventTriggerNameExist(triggerNameShort);
	}

	private Response checkIfQualityTriggerRequestIsValid(HttpServletRequest request, String projectKey,
			String eventKey) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != 200) {
			return response;
		}
		if (!checkIfQualityTriggerExists(eventKey)) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "No trigger exists for this event.")).build();
		}
		return Response.status(Status.OK).build();
	}

	@Path("/getQualityEventTriggerNames")
	@GET
	public Response getAllConsistencyCheckEventTriggerNames() {
		QualityCheckEventListenerSingleton listener = (QualityCheckEventListenerSingleton) QualityCheckEventListenerSingleton
				.getInstance();
		return Response.ok(ImmutableMap.of("names", listener.getAllQualityCheckEventTriggerNames())).build();
	}
}