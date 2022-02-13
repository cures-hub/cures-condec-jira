package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisService;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.releasenotes.MarkdownCreator;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.DecisionTable;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisTimeLine;

/**
 * REST resource for creating the following views on the {@link KnowledgeGraph}:
 * 1) node-link diagram, 2) tree views (indented outline and node-link tree
 * diagram), 3) adjacency matrix, 4) criteria matrix (decision table), and 5)
 * chronology view. Also responsible for exporting the {@link KnowledgeGraph} in
 * markdown format.
 */
@Path("/view")
public class ViewRest {

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown. The selected element can be
	 *            null.
	 * @return intended outline (jstree tree viewer) that matches the
	 *         {@link FilterSettings}. If a knowledge element is selected in the
	 *         {@link FilterSettings}, the tree viewer comprises only one tree with
	 *         the selected element as the root element. If no element is selected,
	 *         the tree viewer contains a list of trees.
	 */
	@Path("/indented-outline")
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
		if (filterSettings.areChangeImpactsHighlighted()) {
			return Response.ok(ChangeImpactAnalysisService.calculateTreeImpact(filterSettings)).build();
		} else {
			return Response.ok(new TreeViewer(filterSettings)).build();
		}
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 * @param isPlacedAtCreationDate
	 * @param isPlacedAtUpdatingDate
	 * @return content for the chronology/evolution/timeline view. The chronology
	 *         view is rendered with the vis timeline library.
	 */
	@Path("/evolution")
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
		if (request == null || filterSettings == null || filterSettings.getSelectedElement() == null) {
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
		if (request == null || filterSettings == null || filterSettings.getSelectedElement() == null
				|| filterSettings.getSelectedElement().getKey() == null) {
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
		VisGraph visGraph;
		if (filterSettings.areChangeImpactsHighlighted()) {
			visGraph = ChangeImpactAnalysisService.calculateGraphImpact(filterSettings);
		} else {
			visGraph = new VisGraph(filterSettings);
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
	@Path("/adjacency-matrix")
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
		if (filterSettings.areChangeImpactsHighlighted()) {
			matrix = ChangeImpactAnalysisService.calculateMatrixImpact(filterSettings);
		} else {
			matrix = new Matrix(filterSettings);
		}

		return Response.ok(matrix).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            object of the {@link FilterSettings} class.
	 * @return knowledge subgraph that matches the {@link FilterSettings} as a
	 *         String in markdown format.
	 */
	@Path("/markdown")
	@POST
	public Response getMarkdown(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null || filterSettings.getProjectKey().isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Getting elements as markdown text failed due to a bad request. You need to provide the filter settings."))
					.build();
		}
		MarkdownCreator markdownCreator = new MarkdownCreator();
		String markDownString = markdownCreator.getMarkdownString(filterSettings);
		return Response.ok(ImmutableMap.of("markdown", markDownString)).build();
	}
}