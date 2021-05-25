package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Locale;

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

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.service.CiaService;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.DecisionTable;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisTimeLine;

/**
 * REST resource for creating views (e.g. tree and graph view)
 */
@Path("/view")
public class ViewRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewRest.class);

	@Path("/elementsFromBranchesOfProject")
	@GET
	public Response getElementsFromAllBranchesOfProject(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}

		LOGGER.info("Feature branch dashboard opened for project:" + projectKey);
		return Response.ok(new DiffViewer(projectKey)).build();
	}

	@Path("/elementsFromBranchesOfJiraIssue")
	@GET
	public Response getElementsOfFeatureBranchForJiraIssue(@Context HttpServletRequest request,
			@QueryParam("issueKey") String issueKey) {
		if (request == null || issueKey == null || issueKey.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Invalid parameters given. Knowledge from feature branch cannot be shown.")).build();
		}
		String projectKey = getProjectKey(issueKey);
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
		if (issue == null) {
			return jiraIssueKeyIsInvalid();
		}
		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}
		new CommitMessageToCommentTranscriber(issue).postCommitsIntoJiraIssueComments();

		LOGGER.info("Feature branch dashboard opened for Jira issue:" + issueKey);
		return Response.ok(new DiffViewer(projectKey, issueKey)).build();
	}

	/**
	 * Returns a jstree tree viewer that matches the {@link FilterSettings}. If a
	 * knowledge element is selected in the {@link FilterSettings}, the tree viewer
	 * comprises only one tree with the selected element as the root element. If no
	 * element is selected, the tree viewer contains a list of trees.
	 *
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown. The selected element can be
	 *            null.
	 */
	@Path("/getTreeViewer")
	@POST
	public Response getTreeViewer(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Invalid parameters given. Tree viewer not be created.")).build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (filterSettings.isCiaRequest()) {
			return Response.ok(CiaService.calculateTreeImpact(filterSettings)).build();
		} else {
			return Response.ok(new TreeViewer(filterSettings)).build();
		}
	}

	@Path("/getEvolutionData")
	@POST
	public Response getEvolutionData(@Context HttpServletRequest request, FilterSettings filterSettings,
			@QueryParam("isPlacedAtCreationDate") boolean isPlacedAtCreationDate,
			@QueryParam("isPlacedAtUpdatingDate") boolean isPlacedAtUpdatingDate) {
		if (request == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "HttpServletRequest is null. Timeline could not be created."))
					.build();
		}
		if (filterSettings == null || filterSettings.getProjectKey() == null
				|| filterSettings.getProjectKey().isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Project key is not valid."))
					.build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisTimeLine timeLine = new VisTimeLine(user, filterSettings, isPlacedAtCreationDate, isPlacedAtUpdatingDate);
		return Response.ok(timeLine).build();
	}

	@Path("/decisionTable")
	@POST
	public Response getDecisionTable(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Decision Table cannot be shown due to missing or invalid parameters."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		KnowledgeElement decisionProblem = filterSettings.getSelectedElement();
		return Response.ok(new DecisionTable(decisionProblem)).build();
	}

	/**
	 * @return all available criteria (e.g. quality attributes, non-functional
	 *         requirements) for a project.
	 */
	@Path("/decisionTableCriteria")
	@GET
	public Response getDecisionTableCriteria(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		if (request == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Decision Table cannot be shown due to missing or invalid parameters."))
					.build();
		}
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		DecisionTable decisionTable = new DecisionTable(projectKey);
		ApplicationUser user = AuthenticationManager.getUser(request);
		return Response.ok(decisionTable.getAllDecisionTableCriteriaForProject(user)).build();
	}

	@Path("/getTreant")
	@POST
	public Response getTreant(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since request or element key is invalid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Treant treant = new Treant(filterSettings);
		return Response.ok(treant).build();
	}

	@Path("/getVis")
	@POST
	public Response getVis(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"The HttpServletRequest or the filter settings are null. Vis graph could not be created."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisGraph visGraph;
		if (filterSettings.isCiaRequest()) {
			visGraph = CiaService.calculateGraphImpact(filterSettings);
		} else {
			visGraph = new VisGraph(user, filterSettings);
		}
		return Response.ok(visGraph).build();
	}

	@Path("/getFilterSettings")
	@GET
	public Response getFilterSettings(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("searchTerm") String searchTerm) {
		if (request == null || projectKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"The HttpServletRequest or the projectKey are null. FilterSettings could not be created."))
					.build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		return Response.ok(new FilterSettings(projectKey, searchTerm, user)).build();
	}

	/**
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the
	 *            {@link KnowledgeType}s to be shown.
	 * @return adjacency matrix of the {@link KnowledgeGraph} or a filtered subgraph
	 *         provided by the {@link FilteringManager}.
	 */
	@Path("/getMatrix")
	@POST
	public Response getMatrix(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Matrix cannot be shown since the HttpServletRequest or filter settings are invalid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Matrix matrix;
		if (filterSettings.isCiaRequest()) {
			matrix = CiaService.calculateMatrixImpact(filterSettings);
		} else {
			matrix = new Matrix(filterSettings);
		}

		return Response.ok(matrix).build();
	}

	private String getProjectKey(String elementKey) {
		return elementKey.split("-")[0].toUpperCase(Locale.ENGLISH);
	}

	private Response jiraIssueKeyIsInvalid() {
		String message = "Decision knowledge elements cannot be shown since the Jira issue key is invalid.";
		LOGGER.error(message);
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", message)).build();
	}
}