package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.PassRule;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CiaSettings;
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
		Set<String> linkTypes = DecisionKnowledgeProject.getNamesOfLinkTypes();
		return Response.ok(linkTypes).build();
	}

	@Path("/getAllLinkTypes")
	@GET
	public Response getAllLinkTypes(@QueryParam("projectKey") String projectKey) {
		Set<String> linkTypes = DecisionKnowledgeProject.getAllNamesOfLinkTypes();
		return Response.ok(linkTypes).build();
	}

	@Path("/getPropagationRules")
	@GET
	public Response getPropagationRules() {
		Set<String> propagationRulesAsString = Arrays.stream(PassRule.values()).map(PassRule::getTranslation)
				.filter(entry -> !entry.equals("undefined")).collect(Collectors.toSet());
		return Response.ok(propagationRulesAsString).build();
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
			if (GitClient.getInstance(projectKey) == null) {
				ConfigPersistenceManager.setKnowledgeExtractedFromGit(projectKey, false);
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Unable to clone git repository")).build();
			}
			new CodeFileExtractorAndMaintainer(projectKey).extractAllChangedFiles(GitClient.getInstance(projectKey));
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
					ImmutableMap.of("error", "Git repository configurations could not be set because they are null."))
					.build();
		}
		ConfigPersistenceManager.setGitRepositoryConfigurations(projectKey, gitRepositoryConfigurations);
		return Response.ok().build();
	}

	@Path("/setCodeFileEndings")
	@POST
	public Response setCodeFileEndings(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			Map<String, String> codeFileEndings) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (codeFileEndings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Code file endings could not be set because they are null."))
					.build();
		}
		ConfigPersistenceManager.setCodeFileEndings(projectKey, codeFileEndings);
		return Response.ok().build();
	}

	@Path("/deleteGitRepos")
	@POST
	public Response deleteGitRepos(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitClient gitClient = GitClient.getInstance(projectKey);
		if (!gitClient.deleteRepositories()) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Git repositories could not be deleted.")).build();
		}
		new CodeClassPersistenceManager(projectKey).deleteKnowledgeElements();
		return Response.ok().build();
	}

	/* **************************************/
	/*										*/
	/* Configuration for Change Impact Analysis */
	/*										*/
	/* **************************************/

	@Path("/setCiaSettings")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCiaSettings(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			CiaSettings ciaSettings) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		if (ciaSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty")).build();
		}

		ConfigPersistenceManager.setCiaSettings(projectKey, ciaSettings);
		return Response.ok(Status.ACCEPTED).build();
	}

	@GET
	@Path("/getCiaSettings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCiaSettings(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		return Response.ok(Status.ACCEPTED).entity(ConfigPersistenceManager.getCiaSettings(projectKey)).build();
	}
}