package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
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
import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * REST resource for basic plug-in configuration (see
 * {@link BasicConfiguration}) and other basic REST methods.
 */
@Path("/config")
public class ConfigRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

	@Path("{projectKey}/activate")
	@POST
	public Response setActivated(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
			boolean isActivated) {
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

	@Path("{projectKey}/activate-jira-issue-documentation")
	@POST
	public Response setJiraIssueDocumentationLocationActivated(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, boolean isActivated) {
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

	/**
	 * Removes invalid entries e.g. of knowledge elements, links, and decision
	 * groups from the database tables.
	 */
	@Path("/clean-database/{projectKey}")
	@POST
	public Response cleanDatabases(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}

		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);

		JiraIssueTextPersistenceManager jiraIssueTextManager = persistenceManager.getJiraIssueTextManager();
		ApplicationUser user = AuthenticationManager.getUser(request);

		jiraIssueTextManager.deleteInvalidElements(user);
		GenericLinkManager.deleteInvalidLinks();

		for (Issue jiraIssue : persistenceManager.getJiraIssueManager().getAllJiraIssuesForProject()) {
			jiraIssueTextManager.updateElementsOfJiraIssueInDatabase(jiraIssue, false);
		}

		// If there are some "lonely" sentences, link them to their Jira issues.
		jiraIssueTextManager.createLinksForNonLinkedElements();

		DecisionGroupPersistenceManager.deleteInvalidGroups(projectKey);

		return Response.ok().build();
	}
}