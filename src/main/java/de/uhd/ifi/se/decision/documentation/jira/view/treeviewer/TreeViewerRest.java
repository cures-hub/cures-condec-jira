package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.persistence.IPersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ofbiz.core.entity.GenericEntityException;

/**
 * @author Ewald Rode
 * @description Rest resource for TreeViewer list
 */
@Path("/treeviewer")
public class TreeViewerRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey) throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else {
				StrategyProvider strategyProvider = new StrategyProvider();
				IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
				Core core = strategy.createCore(project);
				return Response.ok(core).build();
			}
		} else {
			// projectKey is not provided as a query parameter
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
	}
}