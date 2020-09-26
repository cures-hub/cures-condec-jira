package de.uhd.ifi.se.decision.management.jira.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

public class RestParameterChecker {

	public static Response checkIfProjectKeyIsValid(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			return projectKeyIsInvalid();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectByCurrentKey(projectKey);
		if (project == null) {
			return projectKeyIsInvalid();
		}
		return Response.status(Status.OK).build();
	}

	private static Response projectKeyIsInvalid() {
		String message = "The project key is invalid.";
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", message)).build();
	}
}
