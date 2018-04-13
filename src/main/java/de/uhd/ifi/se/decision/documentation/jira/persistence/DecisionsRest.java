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
 * @description REST resource: Enables creation, editing and deletion of
 *              decision knowledge elements and their links
 */
@Path("/decisions")
public class DecisionsRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUnlinkedDecisionComponents(@QueryParam("issueId") long issueId,
			@QueryParam("projectKey") String projectKey) {
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

	@Path("/createDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createDecisionKnowledgeElement(@Context HttpServletRequest request,
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
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
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
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		}
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean isDeleted = strategy.deleteDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (isDeleted) {
				return Response.status(Status.OK).entity(isDeleted).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();

		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
		}
	}

	@Path("/createLink")
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			long issueLinkId = strategy.insertLink(link, user);
			if (issueLinkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
			}
			return Response.status(Status.OK).entity(ImmutableMap.of("id", issueLinkId)).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Creation of link failed."))
					.build();
		}
	}

	@Path("/deleteLink")
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLinks(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean isDeleted = strategy.deleteLink(link, user);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}
	}

	private ApplicationUser getCurrentUser(HttpServletRequest request) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(request);
		return jiraUserManager.getUserByName(userName);
	}
}