package com.atlassian.DecisionDocumentation.rest.treants;

import java.util.Collection;
import java.util.HashSet;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.treants.model.Treant;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableMap;

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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage(@QueryParam("projectKey")final String projectKey, @QueryParam("issueKey") String issueKey,  @QueryParam("depthOfTree") String depthOfTree) throws GenericEntityException
    {
    	if(projectKey!=null){
    		ProjectManager projectManager = ComponentAccessor.getProjectManager();
    		IssueManager issueManager = ComponentAccessor.getIssueManager();
        	Project project = projectManager.getProjectObjByKey(projectKey);
        	if(project == null){
    			/*projekt mit diesem projectKey existiert nicht*/
    			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Can not find Project corresponding to given Query Parameter 'projectKey'")).build();
    		}else if(issueKey != null){
        		int depth;
        		if (depthOfTree != null){
        			try {
            			depth = Integer.parseInt(depthOfTree);
            		} catch (NumberFormatException e) {
            			//default wert
            			depth = 4;
            		}
        		} else {
        			depth = 4;
        		}
        		
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
						Collection<Long> issueIds = issueManager.getIssueIdsForProject(project.getId());
		            	Issue rootIssue = null;
		            	Collection<Issue> issueCollection = new HashSet<Issue>();
		            	for (Long id : issueIds){
		            		issueCollection.add(issueManager.getIssueObject(id));
		            	}
		            	for (Issue issue : issueCollection){
		            		if (issue.getKey().equals(issueKey)){
		            			rootIssue = issue;
		            		}
		            	}	
		            	if(rootIssue == null){
		            		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Can not find Issue corresponding to given Query Parameter 'issueKey'")).build();
		            	}
		            	Treant treantRestModel = strategy.createTreant(rootIssue.getKey(), depth);
	            		return Response.ok(treantRestModel).build();
					} else {
						strategy = new AoStrategy();
						Treant treantRestModel = strategy.createTreant(issueKey, depth);
	            		return Response.ok(treantRestModel).build();
					}
				} else {
					Strategy strategy = new AoStrategy();
					Treant treantRestModel = strategy.createTreant(issueKey, depth);
            		return Response.ok(treantRestModel).build();
				}
    		}
    	} else {
    		/*projectKey wurde nicht als Query-Parameter angegeben*/
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query Parameter 'projectKey' has been omitted, please add a valid projectKey")).build();
    	}
    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query Parameters 'projectKey' and 'issueKey' do not lead to a valid result")).build();
    }
}