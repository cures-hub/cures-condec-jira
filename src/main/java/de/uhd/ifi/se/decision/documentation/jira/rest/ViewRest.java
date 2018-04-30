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
			return projectKeyIsInvalid();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectObjByKey(projectKey);
		if (project == null) {
			return projectKeyIsInvalid();
		}
		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant can not be shown since element key is invalid.")).build();
		}
		int depth = 4; // default value
		try {
			depth = Integer.parseInt(depthOfTree);
		} catch (NumberFormatException e) {
			LOGGER.error("Depth of tree could not be parsed, the default value of 4 is used.");
		}
		Treant treant = new Treant(projectKey, elementKey, depth);
		return Response.ok(treant).build();
	}

	private Response projectKeyIsInvalid() {
		LOGGER.error("Treant can not be shown since project key is invalid.");
		return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Treant can not be shown since project key is invalid.")).build();
	}
}