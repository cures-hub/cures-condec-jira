package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.Vis;

/**
 * REST resource for view
 */
@Path("/view")
public class ViewRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewRest.class);

	@Path("/getTreeViewer")
	@GET
	public Response getTreeViewer(@QueryParam("projectKey") String projectKey,
			@QueryParam("rootElementType") String rootElementType) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (rootElementType == null) {
			rootElementType = "decision";
		}

		TreeViewer treeViewer = new TreeViewer(projectKey, KnowledgeType.getKnowledgeType(rootElementType));
		return Response.ok(treeViewer).build();
	}

	@Path("/getTreeViewer2")
	@GET
	public Response getTreeViewer2(@QueryParam("issueKey") String issueKey,
			@QueryParam("showRelevant") String showRelevant) {
		if (!issueKey.contains("-")) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Issue Key is not valid."))
					.build();
		}
		Boolean[] booleanArray = Arrays.stream(showRelevant.split(",")).map(Boolean::parseBoolean)
				.toArray(Boolean[]::new);
		String projectKey = issueKey.substring(0, issueKey.indexOf("-"));
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}

		TreeViewer treeViewer = new TreeViewer(issueKey, booleanArray);
		return Response.ok(treeViewer).build();
	}

	@Path("/getTreant")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@QueryParam("elementKey") String elementKey,
			@QueryParam("depthOfTree") String depthOfTree, @QueryParam("searchTerm") String searchTerm,
			@Context HttpServletRequest request) {

		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since element key is invalid.")).build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		int depth = 4; // default value
		try {
			depth = Integer.parseInt(depthOfTree);
		} catch (NumberFormatException e) {
			LOGGER.error("Depth of tree could not be parsed, the default value of 4 is used.");
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since depth of Tree is NaN")).build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		Treant treant = new Treant(projectKey, elementKey, depth, searchTerm, user);
		return Response.ok(treant).build();
	}

	@Path("/getVis")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getVis(@QueryParam("elementKey") String elementKey, @QueryParam("searchTerm") String searchTerm,
						   @Context HttpServletRequest request) {
		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Visualization cannot be shown since element key is invalid.")).build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		Vis vis = new Vis(projectKey,elementKey,false,searchTerm,user);
		return Response.ok(vis).build();
	}

	@Path("/getFilterData")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFilterData(@QueryParam("elementKey") String elementKey, @QueryParam("searchTerm") String query,
										  @Context HttpServletRequest request) {
		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Visualization cannot be shown since element key is invalid.")).build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		FilterDataProvider filterDataProvider = new FilterDataProvider(projectKey,query,user);
		return Response.ok(filterDataProvider).build();
	}

	private String getProjectKey(String elementKey) {
		return elementKey.split("-")[0];
	}

	private Response checkIfProjectKeyIsValid(String projectKey) {
		if (projectKey == null) {
			return projectKeyIsInvalid();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project == null) {
			return projectKeyIsInvalid();
		}
		return Response.status(Status.OK).build();
	}

	private Response projectKeyIsInvalid() {
		LOGGER.error("Decision knowledge elements cannot be shown since project key is invalid.");
		return Response.status(Status.BAD_REQUEST).entity(
				ImmutableMap.of("error", "Decision knowledge elements cannot be shown since project key is invalid."))
				.build();
	}
}