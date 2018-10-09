package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;


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

		ActiveObjectsManager.clearInValidLinks();
		TreeViewer treeViewer = new TreeViewer(projectKey, KnowledgeType.getKnowledgeType(rootElementType));
		return Response.ok(treeViewer).build();
	}

	@Path("/getTreeViewer2")
	@GET
	public Response getTreeViewer2(@QueryParam("issueKey") String issueKey,
			@QueryParam("showRelevant") boolean showRelevant) {
		if(!issueKey.contains("-")) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Issue Key is not valid.")).build();
		}
		String projectKey = issueKey.substring(0, issueKey.indexOf("-"));
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}

		ActiveObjectsManager.clearInValidLinks();
		TreeViewer treeViewer = new TreeViewer(issueKey, showRelevant);
		return Response.ok(treeViewer).build();
	}

	@Path("/getTreant")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@QueryParam("elementKey") String elementKey,
			@QueryParam("depthOfTree") String depthOfTree) {

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
		Treant treant = new Treant(projectKey, elementKey, depth);
		return Response.ok(treant).build();
	}

	@Path("/getTreantFiltered")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@QueryParam("elementKey") String elementKey,
							  @QueryParam("depthOfTree") String depthOfTree,
							  @QueryParam("searchTerm") String searchTerm,
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
		ApplicationUser user = getCurrentUser(request);
		boolean isFilteredByCreationDate;
		GraphFiltering filter = new GraphFiltering(projectKey, searchTerm,user);
		filter.produceResultsFromQuery();
		isFilteredByCreationDate = filter.isQueryContainsCreationDate();
		List<DecisionKnowledgeElement> filteredElements = filter.getQueryResults();
		Treant treantFiltered = new Treant(projectKey, elementKey, depth, filteredElements,isFilteredByCreationDate);
		return Response.ok(treantFiltered).build();
	}


	@Path("/getQuery")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getQuery(@QueryParam("projectKey") String projectKey, @QueryParam("URISearch") String URISearch,  @Context HttpServletRequest request) {
		ApplicationUser user = getCurrentUser(request);

		GraphFiltering filter = new GraphFiltering(projectKey, URISearch,user);
		filter.produceResultsFromQuery();

		List<DecisionKnowledgeElement> filteredElements = filter.getQueryResults();

		return Response.ok(filteredElements).build();
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

	private ApplicationUser getCurrentUser(HttpServletRequest request) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(request);
		return jiraUserManager.getUserByName(userName);
	}

}