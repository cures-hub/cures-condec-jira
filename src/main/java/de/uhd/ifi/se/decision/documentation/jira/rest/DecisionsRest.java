package de.uhd.ifi.se.decision.documentation.jira.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description REST resource: Enables creation, editing and deletion of
 *              decision knowledge elements and their links
 */
@Path("/decisions")
public class DecisionsRest {

	@Path("/getDecisionKnowledgeElement")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionKnowledgeElement(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			DecisionKnowledgeElement decisionKnowledgeElement = strategy.getDecisionKnowledgeElement(id);
			if (decisionKnowledgeElement != null) {
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Decision knowledge element was not found for the given id.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Decision knowledge element could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/getLinkedDecisionComponents")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getLinkedDecisionComponents(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			List<DecisionKnowledgeElement> linkedDecisionKnowledgeElements = strategy.getLinkedElements(id);
			return Response.ok(linkedDecisionKnowledgeElements).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision components could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/getUnlinkedDecisionComponents")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUnlinkedDecisionComponents(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			List<DecisionKnowledgeElement> unlinkedDecisionKnowledgeElements = strategy.getUnlinkedDecisionComponents(id, projectKey);
			return Response.ok(unlinkedDecisionKnowledgeElements).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision components could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/createDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createDecisionKnowledgeElement(@Context HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement) {
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
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Creation of decision knowledge element failed."))
					.build();
		}
	}

	@Path("/updateDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateDecisionKnowledgeElement(@Context HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement) {
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
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Update of decision knowledge element failed."))
					.build();
		}
	}

	@Path("/deleteDecisionKnowledgeElement")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement) {
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
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed."))
					.build();
		}
	}

	@Path("/createLink")
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request, Link link) {
		if (projectKey != null && request != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			long linkId = strategy.insertLink(link, user);
			if (linkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Creation of link failed.")).build();
			}
			return Response.status(Status.OK).entity(ImmutableMap.of("id", linkId)).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
	}

	@Path("/deleteLink")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLinks(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request, Link link) {
		if (projectKey != null && request != null && link != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
			PersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean isDeleted = strategy.deleteLink(link, user);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
		}
	}

	private ApplicationUser getCurrentUser(HttpServletRequest request) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(request);
		return jiraUserManager.getUserByName(userName);
	}
}