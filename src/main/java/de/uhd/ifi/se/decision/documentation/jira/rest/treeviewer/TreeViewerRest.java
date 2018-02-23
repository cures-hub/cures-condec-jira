package de.uhd.ifi.se.decision.documentation.jira.rest.treeviewer;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.db.strategy.Strategy;
import de.uhd.ifi.se.decision.documentation.jira.db.strategy.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.rest.treeviewer.model.Core;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ofbiz.core.entity.GenericEntityException;

/**
 * @author Ewald Rode
 * @description Rest resource for treeviewer navigation
 */
@Path("/treeviewer")
public class TreeViewerRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey")final String projectKey) throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Can not find Project corresponding to given Query Parameter 'projectKey'")).build();
			} else {
				StrategyProvider strategyProvider = new StrategyProvider();
        		Strategy strategy = strategyProvider.getStrategy(projectKey);
				Core core = strategy.createCore(project);
				return Response.ok(core).build();
			}
		} else {
			/* projectKey wurde nicht als Query-Parameter angegeben */
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query Parameter 'projectKey' has been omitted, please add a valid projectKey")).build();
		}
	}
}