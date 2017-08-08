package com.atlassian.DecisionDocumentation.rest.treeviewer;

import com.atlassian.DecisionDocumentation.db.strategy.Strategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.AoStrategy;
import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Core;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.TreeViewerRepresentation;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.jira.component.ComponentAccessor;
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
 * @description TreeViewer Rest API Listener
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
					//TreeViewerRepresentation treeViewerRep = new TreeViewerRepresentation(strategy, project);
					//return Response.ok(treeViewerRep).build();
					Core core = strategy.createCore(project);
					return Response.ok(core).build();
				} else {
					Strategy strategy = new AoStrategy();
					//TreeViewerRepresentation treeViewerRep = new TreeViewerRepresentation(strategy, project);
					//return Response.ok(treeViewerRep).build();
					Core core = strategy.createCore(project);
					return Response.ok(core).build();
				}
			}
		} else {
			/* projectKey wurde nicht als Query-Parameter angegeben */
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query Parameter 'projectKey' has been omitted, please add a valid projectKey")).build();
		}
	}
}