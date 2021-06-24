package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.opensymphony.workflow.loader.ActionDescriptor;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.PromptingEventConfiguration;

/**
 * REST resource for nudging functionality, in particular, for just-in-time
 * prompts.
 */
@Path("/nudging")
public class NudgingRest {

	@Path("/isPromptEventActivated")
	@POST
	public Response isPromptEventActivated(@Context HttpServletRequest request,
			@QueryParam("jiraIssueKey") String jiraIssueKey, @QueryParam("actionId") int actionId) {
		Issue issue = JiraIssuePersistenceManager.getJiraIssue(jiraIssueKey);
		ActionDescriptor actionDescriptor = ComponentAccessor.getWorkflowManager().getActionDescriptor(issue, actionId);
		System.out.println(actionDescriptor.getName());
		System.out.println(actionDescriptor.getUnconditionalResult().getStatus());
		PromptingEventConfiguration promptingEventConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(issue.getProjectObject().getKey());
		boolean isActivated = promptingEventConfiguration
				.isPromptEventForDefinitionOfDoneCheckingActivated(actionDescriptor.getName());
		return Response.ok(isActivated).build();
	}

	@Path("/activatePromptEventForLinkSuggestion")
	@POST
	public Response activatePromptEventForLinkSuggestion(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("eventKey") String eventKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		PromptingEventConfiguration promptingEventConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(projectKey);
		promptingEventConfiguration.setPromptEventForLinkSuggestion(eventKey, isActivated);
		ConfigPersistenceManager.savePromptingEventConfiguration(projectKey, promptingEventConfiguration);
		return Response.ok().build();
	}

	@Path("/activatePromptEventForDefinitionOfDoneChecking")
	@POST
	public Response activatePromptEventForDefinitionOfDoneChecking(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("eventKey") String eventKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		PromptingEventConfiguration linkSuggestionConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(projectKey);
		linkSuggestionConfiguration.setPromptEventForDefinitionOfDoneChecking(eventKey, isActivated);
		ConfigPersistenceManager.savePromptingEventConfiguration(projectKey, linkSuggestionConfiguration);
		return Response.ok().build();
	}

	@Path("/activatePromptEventForNonValidatedElementsChecking")
	@POST
	public Response activatePromptEventForNonValidatedElementsChecking(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("eventKey") String eventKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		PromptingEventConfiguration nonValidatedElementsConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(projectKey);
		nonValidatedElementsConfiguration.setPromptEventForNonValidatedElementsChecking(eventKey, isActivated);
		ConfigPersistenceManager.savePromptingEventConfiguration(projectKey, nonValidatedElementsConfiguration);
		return Response.ok().build();
	}
}
