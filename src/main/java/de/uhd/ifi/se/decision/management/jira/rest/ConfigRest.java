package de.uhd.ifi.se.decision.management.jira.rest;

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
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;

/**
 * REST resource for basic plug-in configuration (see
 * {@link BasicConfiguration}) and other basic REST methods.
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
		BasicConfiguration basicConfiguration = ConfigPersistenceManager.getBasicConfiguration(projectKey);
		basicConfiguration.setActivated(isActivated);
		ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfiguration);
		ComponentGetter.removeInstances(projectKey);
		return Response.ok().build();
	}

	@Path("/isActivated")
	@GET
	public Response isActivated(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isActivated = ConfigPersistenceManager.getBasicConfiguration(projectKey).isActivated();
		return Response.ok(isActivated).build();
	}

	@Path("/isJiraIssueDocumentationLocationActivated")
	@GET
	public Response isJiraIssueDocumentationLocationActivated(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isActivated = ConfigPersistenceManager.getBasicConfiguration(projectKey)
				.isJiraIssueDocumentationLocationActivated();
		return Response.ok(isActivated).build();
	}

	@Path("/setJiraIssueDocumentationLocationActivated")
	@POST
	public Response setJiraIssueDocumentationLocationActivated(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		BasicConfiguration basicConfiguration = ConfigPersistenceManager.getBasicConfiguration(projectKey);
		basicConfiguration.setJiraIssueDocumentationLocationActivated(isActivated);
		ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfiguration);
		manageDefaultIssueTypes(projectKey, isActivated);
		return Response.ok().build();
	}

	public static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		JiraSchemeManager jiraSchemeManager = new JiraSchemeManager(projectKey);
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				BasicConfiguration basicConfig = ConfigPersistenceManager.getBasicConfiguration(projectKey);
				basicConfig.setKnowledgeTypeEnabled(knowledgeType, true);
				ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfig);
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
		boolean isKnowledgeTypeEnabled = ConfigPersistenceManager.getBasicConfiguration(projectKey)
				.isKnowledgeTypeEnabled(knowledgeType);
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
		BasicConfiguration basicConfig = ConfigPersistenceManager.getBasicConfiguration(projectKey);
		basicConfig.setKnowledgeTypeEnabled(KnowledgeType.getKnowledgeType(knowledgeType), isKnowledgeTypeEnabled);
		ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfig);
		if (ConfigPersistenceManager.getBasicConfiguration(projectKey).isJiraIssueDocumentationLocationActivated()) {
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
		BasicConfiguration basicConfig = ConfigPersistenceManager.getBasicConfiguration(projectKey);
		basicConfig.setCriteriaJiraQuery(query);
		ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfig);
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
			JiraSchemeManager.createLinkType(LinkType.getLinkType(linkType));
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
	 *      if we model transitive links or links to code classes.
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
		Set<String> linkTypes = DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes();
		return Response.ok(linkTypes).build();
	}

	@Path("/setChangeImpactAnalysisConfiguration")
	@POST
	public Response setChangeImpactAnalysisConfiguration(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, ChangeImpactAnalysisConfiguration ciaConfig) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (ciaConfig == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The CIA config must not be null!")).build();
		}
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration(projectKey, ciaConfig);
		return Response.ok().build();
	}

	@GET
	@Path("/getChangeImpactAnalysisConfiguration")
	public Response getChangeImpactAnalysisConfiguration(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		return Response.ok(ConfigPersistenceManager.getChangeImpactAnalysisConfiguration(projectKey)).build();
	}

	// TODO Refactor: too many ifs
	@Path("/getDecisionGroups")
	@GET
	public Response getDecisionGroups(@QueryParam("elementId") long id, @QueryParam("location") String location,
			@QueryParam("projectKey") String projectKey) {
		if (id == -1 || location == null || projectKey == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		KnowledgeElement element = KnowledgePersistenceManager.getInstance(projectKey).getKnowledgeElement(id,
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
		List<String> keys = DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup(group, projectKey);
		return Response.ok(keys).build();
	}

	@Path("/getAllClassElementsWithCertainGroup")
	@GET
	public Response getAllClassElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupPersistenceManager.getAllClassElementsWithCertainGroup(group, projectKey);
		return Response.ok(keys).build();
	}

	@Path("/renameDecisionGroup")
	@GET
	public Response renameDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupPersistenceManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to rename found")).build();
	}

	@Path("/deleteDecisionGroup")
	@GET
	public Response deleteDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		if (DecisionGroupPersistenceManager.deleteGroup(groupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to delete found")).build();
	}

	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		Set<String> allGroups = DecisionGroupPersistenceManager.getAllDecisionGroups(projectKey);
		return Response.ok(allGroups).build();
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

	/**
	 * Removes invalid entries e.g. of knowledge elements, links, and decision
	 * groups from the database tables.
	 */
	@Path("/cleanDatabases")
	@POST
	public Response cleanDatabases(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getJiraIssueTextManager();
		ApplicationUser user = AuthenticationManager.getUser(request);
		persistenceManager.deleteInvalidElements(user);
		GenericLinkManager.deleteInvalidLinks();
		// If there are some "lonely" sentences, link them to their Jira issues.
		persistenceManager.createLinksForNonLinkedElements();
		DecisionGroupPersistenceManager.deleteInvalidGroups();
		return Response.ok().build();
	}
}