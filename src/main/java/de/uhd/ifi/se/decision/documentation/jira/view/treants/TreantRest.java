package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.IDecisionStorageStrategy;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.StrategyProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ofbiz.core.entity.GenericEntityException;

/**
 * @author Ewald Rode
 * @description Rest resource for Treants
 */
@Path("/treant")
public class TreantRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey,
			@QueryParam("issueKey") String issueKey, @QueryParam("depthOfTree") String depthOfTree)
			throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				/* projekt mit diesem projectKey existiert nicht */
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
						"Cannot find project for the given query parameter 'projectKey'")).build();
			} else if (issueKey != null) {
				int depth;
				if (depthOfTree != null) {
					try {
						depth = Integer.parseInt(depthOfTree);
					} catch (NumberFormatException e) {
						// default wert
						depth = 4;
					}
				} else {
					depth = 4;
				}
				StrategyProvider strategyProvider = new StrategyProvider();
				IDecisionStorageStrategy strategy = strategyProvider.getStrategy(projectKey);
				Treant treantRestModel = strategy.createTreant(issueKey, depth);
				return Response.ok(treantRestModel).build();
			}
		} else {
			/* projectKey wurde nicht als Query-Parameter angegeben */
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "Query parameters 'projectKey' and 'issueKey' do not lead to a valid result"))
				.build();
	}
}