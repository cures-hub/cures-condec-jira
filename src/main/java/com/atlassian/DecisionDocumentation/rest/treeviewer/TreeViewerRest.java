package com.atlassian.DecisionDocumentation.rest.treeviewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ofbiz.core.entity.GenericEntityException;

/**
 * 
 * @author Ewald Rode
 * @description TreeViewer Rest API Listener
 */
@Path("/treeviewer")
public class TreeViewerRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") String projectKey) throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				/* projekt mit diesem projectKey existiert nicht */
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new HashMap<String, String>() {
					{
						put("error", "Can not find Project corresponding to given Query Parameter 'projectKey'");
					}
				}).build();
			} else {
				Collection<Long> issueIds = issueManager.getIssueIdsForProject(project.getId());
				List<Long> issueIdList = new ArrayList<Long>();
				for (Long id : issueIds) {
					issueIdList.add(id);
				}
				List<Issue> issueList = new ArrayList<Issue>();
				for (int index = 0; index < issueIdList.size(); ++index) {
					Issue issue = issueManager.getIssueObject(issueIdList.get(index));
					issueList.add(issue);
				}
				TreeViewerRestModel treeViewerModel = new TreeViewerRestModel(issueList);
				return Response.ok(treeViewerModel).build();
			}
		} else {
			/* projectKey wurde nicht als Query-Parameter angegeben */
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new HashMap<String, String>() {
				{
					put("error", "Query Parameter 'projectKey' has been omitted, please add a valid projectKey");
				}
			}).build();
		}
	}
}