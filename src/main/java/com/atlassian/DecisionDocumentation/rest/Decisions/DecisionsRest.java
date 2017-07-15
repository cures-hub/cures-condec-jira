package com.atlassian.DecisionDocumentation.rest.Decisions;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.LinkRepresentation;
import com.atlassian.DecisionDocumentation.rest.Decisions.model.SimpleDecisionRepresentation;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

/**
 * @author Ewald Rode
 * @description
 */
@Path("/decisions")
public class DecisionsRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionsRest.class);

	@GET
    @Produces({MediaType.APPLICATION_JSON})
	public Response getUnlinkedIssues(@QueryParam("issueId") long issueId, @QueryParam("projectKey")final String projectKey) {
		if(projectKey != null) {
			TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
			final PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
			final String pluginStorageKey = ComponentGetter.getPluginStorageKey();
			Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".isIssueStrategy");
                    return o;
                }
            });
			if(ob instanceof String) {
				String strategyType = (String) ob;
				Strategy strategy = null;
				if (strategyType.equalsIgnoreCase("true")) {
					strategy = new IssueStrategy();
				} else {
					strategy = new AoStrategy();
				}
				List<SimpleDecisionRepresentation> decList = null;
				if(strategy instanceof IssueStrategy ||strategy instanceof AoStrategy) {
		    		decList = strategy.searchUnlinkedDecisionComponents(issueId, projectKey);
		    	} else {
		    		//error TODO logger
		    		return Response.ok("Neither IssueStrategy nor AoStrategy").build();
		    	}
		    	return Response.ok(decList).build();
			} else {
				//TODO return error corrupt settings
			}
		} else {
			//error TODO logger
			return Response.ok("projectKey or issueId = null").build();
		}
		return Response.ok("Generic").build();
	}
	
	@POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response postDecision(@QueryParam("actionType") String actionType, 
    		@Context HttpServletRequest req, final DecisionRepresentation dec)
    {
		if(actionType != null && dec != null) {
			TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
			final PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
			final String pluginStorageKey = ComponentGetter.getPluginStorageKey();
			final String projectKey = dec.getProjectKey();
			Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".isIssueStrategy");
                    return o;
                }
            });
			if(ob instanceof String) {
				String strategyType = (String) ob;
				Strategy strategy = null;
				if (strategyType.equalsIgnoreCase("true")) {
					strategy = new IssueStrategy();
				} else {
					strategy = new AoStrategy();
				}
	    		ApplicationUser user = getCurrentUser(req);
	    		if(actionType.equalsIgnoreCase("create")) {
	    			LOGGER.error("before createDecisionComponent");
	    			final long issueId = strategy.createDecisionComponent(dec, user);
	    			if(issueId!=0) {
	    				return Response.status(Status.OK).entity(new HashMap<String, Long>() {
	    					{
	    						put("id", issueId);
	    					}
	    				}).build();
	    			}
	    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new HashMap<String, String>() {
						{
							put("error", "Creation of Issue failed.");
						}
					}).build();
	    		} else if(actionType.equalsIgnoreCase("edit")) {
	    			//TODO: IssueStrategy edit
	    			strategy.editDecisionComponent(dec, user);
	    			return Response.ok("edit success").build();
	    		} else if(actionType.equalsIgnoreCase("delete")) {
	    			//TODO: IssueStrategy delete
	    			strategy.deleteDecisionComponent(dec, user);
	    			return Response.ok("delete success").build();
	    		} else {
	    			//error TODO logger
	    			return Response.ok("Unknown actionType. Pick either 'create', 'edit' or 'delete'").build();
	    		}
			} else {
				Strategy strategy = new AoStrategy();
				ApplicationUser user = getCurrentUser(req);
	    		if(actionType.equalsIgnoreCase("create")) {
	    			LOGGER.error("before createDecisionComponent");
	    			final long issueId = strategy.createDecisionComponent(dec, user);
	    			if(issueId!=0) {
	    				return Response.status(Status.OK).entity(new HashMap<String, Long>() {
	    					{
	    						put("id", issueId);
	    					}
	    				}).build();
	    			}
	    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new HashMap<String, String>() {
						{
							put("error", "Creation of Issue failed.");
						}
					}).build();
	    		} else if(actionType.equalsIgnoreCase("edit")) {
	    			//TODO: IssueStrategy edit
	    			strategy.editDecisionComponent(dec, user);
	    			return Response.ok("edit success").build();
	    		} else if(actionType.equalsIgnoreCase("delete")) {
	    			//TODO: IssueStrategy delete
	    			strategy.deleteDecisionComponent(dec, user);
	    			return Response.ok("delete success").build();
	    		} else {
	    			//error TODO logger
	    			return Response.ok("Unknown actionType. Pick either 'create', 'edit' or 'delete'").build();
	    		}
			}
		} else {
			//error TODO logger
			return Response.ok("dec or actionType = null").build();
		}
		//return Response.ok("POST Generic").build();
    }
	
	@PUT
    @Produces({MediaType.APPLICATION_JSON})
    public Response putLink(@QueryParam("actionType") String actionType, @QueryParam("projectKey")final String projectKey, 
    		@Context HttpServletRequest req, final LinkRepresentation link)
    {
		if(actionType != null) {
			TransactionTemplate transactionTemplate = ComponentGetter.getTransactionTemplate();
			final PluginSettingsFactory pluginSettingsFactory = ComponentGetter.getPluginSettingsFactory();
			final String pluginStorageKey = ComponentGetter.getPluginStorageKey();
			link.getOutgoingId();
			Object ob = transactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction() {
                    PluginSettings settings = pluginSettingsFactory.createSettingsForKey(projectKey);
                    Object o = settings.get(pluginStorageKey + ".isIssueStrategy");
                    return o;
                }
            });
			if(ob instanceof String) {
				String strategyType = (String) ob;
				Strategy strategy = null;
				if (strategyType.equalsIgnoreCase("true")) {
					strategy = new IssueStrategy();
				} else {
					strategy = new AoStrategy();
				}
	    		ApplicationUser user = getCurrentUser(req);
	    		if(actionType.equalsIgnoreCase("create")) {
	    			strategy.createLink(link, user);
	    			return Response.ok().build();
	    		} else if(actionType.equalsIgnoreCase("delete")) {
	    			//TODO: IssueStrategy edit
	    			strategy.deleteLink(link, user);
	    			return Response.ok().build();
	    		} else {
	    			//error TODO logger
	    			return Response.ok("Unknown actionType. Pick either 'create' or 'delete'").build();
	    		}
			} else {
				Strategy strategy = new AoStrategy(); 
				ApplicationUser user = getCurrentUser(req);
	    		if(actionType.equalsIgnoreCase("create")) {
	    			strategy.createLink(link, user);
	    			return Response.ok().build();
	    		} else if(actionType.equalsIgnoreCase("delete")) {
	    			//TODO: IssueStrategy edit
	    			strategy.deleteLink(link, user);
	    			return Response.ok().build();
	    		} else {
	    			//error TODO logger
	    			return Response.ok("Unknown actionType. Pick either 'create' or 'delete'").build();
	    		}
			}
		} else {
			//error TODO logger
			return Response.ok("dec or actionType = null").build();
		}
    }
	
	private ApplicationUser getCurrentUser(HttpServletRequest req) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(req);
		return jiraUserManager.getUserByName(userName);
	} 
}