package de.uhd.ifi.se.decision.documentation.jira.persistence;

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
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.Link;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;

/**
 * @description Rest resource: Enables creation, editing and deletion of
 *              decision knowledge elements and their links
 */
@Path("/decisions")
public class DecisionsRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUnlinkedDecisionComponents(@QueryParam("issueId") long issueId,
			@QueryParam("projectKey") final String projectKey) {
		if (projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			List<DecisionKnowledgeElement> decisions = strategy.getUnlinkedDecisionComponents(issueId, projectKey);
			return Response.ok(decisions).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey or issueId = null"))
					.build();
		}
	}

	@Path("/insertDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response insertDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			decisionKnowledgeElement = strategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (decisionKnowledgeElement != null) {
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build();
		}
	}

	@Path("/updateDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			if (strategy.updateDecisionKnowledgeElement(decisionKnowledgeElement, user)) {
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Decision knowledge element or actionType = null")).build();
		}
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request,	final DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			final String projectKey = decisionKnowledgeElement.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean successful = strategy.deleteDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (successful) {
				return Response.status(Status.OK).entity(successful).build();
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null"))
					.build();
		}
	}

	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putLink(@QueryParam("projectKey") final String projectKey, @Context HttpServletRequest req, final Link link) {
		if (projectKey != null && req != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(req);
			long issueLinkId = strategy.insertLink(link, user);
			if (issueLinkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of Link failed.")).build();
			} else {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", issueLinkId)).build();
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