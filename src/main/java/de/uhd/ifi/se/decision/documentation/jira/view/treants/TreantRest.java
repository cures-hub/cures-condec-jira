package de.uhd.ifi.se.decision.documentation.jira.view.treants;

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
 * @description Rest resource for Treants
 */
@Path("/treant")
public class TreantRest {

	public Treant createTreant(String key, int depth, String projectKey) {
		StrategyProvider strategyProvider = new StrategyProvider();
		IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);

		Treant treant = new Treant();
		treant.setChart(new Chart());

		treant.setNodeStructure(strategy.createNodeStructure(key, depth));
		return treant;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey,
			@QueryParam("issueKey") String issueKey, @QueryParam("depthOfTree") String depthOfTree)
			throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else if (issueKey != null) {
				int depth;
				if (depthOfTree != null) {
					try {
						depth = Integer.parseInt(depthOfTree);
					} catch (NumberFormatException e) {
						// default value
						depth = 4;
					}
				} else {
					depth = 4;
				}
				Treant treantRestModel = this.createTreant(issueKey, depth, projectKey);
				return Response.ok(treantRestModel).build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "Query parameters 'projectKey' and 'issueKey' do not lead to a valid result"))
				.build();
	}
}