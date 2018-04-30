package de.uhd.ifi.se.decision.documentation.jira.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.TreeViewer;

/**
 * @description REST resource for TreeViewer list
 */
@Path("/treeviewer")
public class TreeViewerRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewerRest.class);

	@Path("/getTreeViewer")
	@GET
	public Response getTreeViewer(@QueryParam("projectKey") String projectKey) {
		if (projectKey == null) {
			return projectKeyIsInvalid();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project == null) {
			return projectKeyIsInvalid();
		}
		TreeViewer treeViewer = new TreeViewer(projectKey);
		return Response.ok(treeViewer).build();
	}

	private Response projectKeyIsInvalid() {
		LOGGER.error("Tree Viewer can not be shown since project key is invalid.");
		return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Tree Viewer can not be shown since project key is invalid."))
				.build();
	}
}