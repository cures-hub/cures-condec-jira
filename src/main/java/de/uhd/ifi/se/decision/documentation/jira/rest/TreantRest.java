package de.uhd.ifi.se.decision.documentation.jira.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.view.treants.Treant;

/**
 * @description REST resource for Treant
 */
@Path("")
public class TreantRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewerRest.class);

	@Path("/getTreant")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@QueryParam("projectKey") final String projectKey,
			@QueryParam("elementKey") String elementKey, @QueryParam("depthOfTree") String depthOfTree) {
		if (projectKey == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project == null) {
			LOGGER.error("getMessage no project with this ProjectKey found");
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
					.build();
		}
		if (elementKey != null) {
			int depth;
			if (depthOfTree != null) {
				try {
					depth = Integer.parseInt(depthOfTree);
				} catch (NumberFormatException e) {
					depth = 4; // default value
				}
			} else {
				depth = 4;
			}
			Treant treant = new Treant(projectKey, elementKey, depth);
			return Response.ok(treant).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "Query parameters 'projectKey' and 'issueKey' do not lead to a valid result"))
				.build();
	}
}