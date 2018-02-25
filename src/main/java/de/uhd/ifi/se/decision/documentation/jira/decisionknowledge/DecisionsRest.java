package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;
import de.uhd.ifi.se.decision.documentation.jira.persistence.IPersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @author Ewald Rode
 * @description Rest resource: Enables creation, editing and deletion of
 *              decision knowledge elements and their links
 */
@Path("/decisions")
public class DecisionsRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUnlinkedIssues(@QueryParam("issueId") long issueId,
			@QueryParam("projectKey") final String projectKey) {
		if (projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			List<SimpleDecisionKnowledgeElement> decisions = strategy.searchUnlinkedDecisionComponents(issueId, projectKey);
			return Response.ok(decisions).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey or issueId = null"))
					.build();
		}
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response postDecision(@QueryParam("actionType") String actionType, @Context HttpServletRequest request,
			final DecisionKnowledgeElement decisionKnowledgeElement) {
		if (actionType != null && decisionKnowledgeElement != null && request != null) {
			final String projectKey = decisionKnowledgeElement.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
			IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			if (actionType.equalsIgnoreCase("create")) {
				final Data data = strategy.createDecisionComponent(decisionKnowledgeElement, user);
				if (data != null) {
					return Response.status(Status.OK).entity(data).build();
				}
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
			} else if (actionType.equalsIgnoreCase("edit")) {
				final Data data = strategy.editDecisionComponent(decisionKnowledgeElement, user);
				if (data != null) {
					return Response.status(Status.OK).entity(data).build();
				}
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
			} else if (actionType.equalsIgnoreCase("delete")) {
				boolean successful = strategy.deleteDecisionComponent(decisionKnowledgeElement, user);
				if (successful) {
					return Response.status(Status.OK).entity(successful).build();
				} else {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST).entity(
						ImmutableMap.of("error", "Unknown actionType. Pick either 'create', 'edit' or 'delete'"))
						.build();
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null"))
					.build();
		}
	}

	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putLink(@QueryParam("actionType") String actionType,
			@QueryParam("projectKey") final String projectKey, @Context HttpServletRequest req,
			final Link link) {
		if (actionType != null && projectKey != null && req != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(req);
			if (actionType.equalsIgnoreCase("create")) {
				long issueLinkId = strategy.createLink(link, user);
				if (issueLinkId == 0) {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(ImmutableMap.of("error", "Creation of Link failed.")).build();
				} else {
					return Response.status(Status.OK).entity(ImmutableMap.of("id", issueLinkId)).build();
				}
			} else {
				return Response.status(Status.BAD_REQUEST)
						.entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create' or 'delete'"))
						.build();
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null"))
					.build();
		}
	}

	private ApplicationUser getCurrentUser(HttpServletRequest req) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(req);
		return jiraUserManager.getUserByName(userName);
	}
}