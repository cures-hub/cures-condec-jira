package com.atlassian.DecisionDocumentation.rest.Decisions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.db.strategy.StrategyProvider;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.SimpleDecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

/**
 * @author Ewald Rode
 * @description Rest resource: Enables creation, editing and deletion of decision components and their links
 */
@Path("/decisions")
public class DecisionsRest {

	@GET
    @Produces({MediaType.APPLICATION_JSON})
	public Response getUnlinkedIssues(@QueryParam("issueId") long issueId, @QueryParam("projectKey")final String projectKey) {
		if(projectKey != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
    		Strategy strategy = strategyProvider.getStrategy(projectKey);
			List<SimpleDecisionRepresentation> decList = strategy.searchUnlinkedDecisionComponents(issueId, projectKey);
	    	return Response.ok(decList).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey or issueId = null")).build();
		}
	}
	
	@POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response postDecision(@QueryParam("actionType") String actionType, 
    		@Context HttpServletRequest req, final DecisionRepresentation dec)
    {
		if(actionType != null && dec != null) {
			final String projectKey = dec.getProjectKey();
			StrategyProvider strategyProvider = new StrategyProvider();
    		Strategy strategy = strategyProvider.getStrategy(projectKey);
    		ApplicationUser user = getCurrentUser(req);
    		if(actionType.equalsIgnoreCase("create")) {
    			final Data data = strategy.createDecisionComponent(dec, user);
    			if(data != null) {
    				return Response.status(Status.OK).entity(data).build();
    			}
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Creation of Issue failed.")).build();
    		} else if(actionType.equalsIgnoreCase("edit")) {
    			final Data data = strategy.editDecisionComponent(dec, user);
    			if(data != null) {
    				return Response.status(Status.OK).entity(data).build();
    			}
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Update of Issue failed.")).build();
    		} else if(actionType.equalsIgnoreCase("delete")) {
    			boolean successful = strategy.deleteDecisionComponent(dec, user);
    			if(successful) {
    				return Response.status(Status.OK).entity(successful).build();
    			} else {
    				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Deletion of Issue failed.")).build();
    			}
    		} else {
    			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create', 'edit' or 'delete'")).build();
    		}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build();
		}
    }
	
	@PUT
    @Produces({MediaType.APPLICATION_JSON})
    public Response putLink(@QueryParam("actionType") String actionType, @QueryParam("projectKey")final String projectKey, 
    		@Context HttpServletRequest req, final LinkRepresentation link)
    {
		if(actionType != null) {
			StrategyProvider strategyProvider = new StrategyProvider();
    		Strategy strategy = strategyProvider.getStrategy(projectKey);
    		ApplicationUser user = getCurrentUser(req);
    		if(actionType.equalsIgnoreCase("create")) {
    			long issueLinkId = strategy.createLink(link, user);
    			if(issueLinkId == 0) {
    				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Creation of Link failed.")).build();
    			} else {
    				return Response.status(Status.OK).entity(ImmutableMap.of("id",issueLinkId)).build();
    			}
    		} else {
    			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Unknown actionType. Pick either 'create' or 'delete'")).build();
    		}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "dec or actionType = null")).build();
		}
    }
	
	private ApplicationUser getCurrentUser(HttpServletRequest req) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(req);
		return jiraUserManager.getUserByName(userName);
	} 
}