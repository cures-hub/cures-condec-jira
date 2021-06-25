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
import com.google.common.collect.ImmutableMap;
import com.opensymphony.workflow.loader.ActionDescriptor;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.prompts.FeatureWithPrompt;
import de.uhd.ifi.se.decision.management.jira.recommendation.prompts.PromptingEventConfiguration;

/**
 * REST resource for nudging functionality, in particular, for just-in-time
 * prompts.
 */
@Path("/nudging")
public class NudgingRest {

	@Path("/isPromptEventActivated")
	@POST
	public Response isPromptEventActivated(@Context HttpServletRequest request, @QueryParam("feature") String feature,
			@QueryParam("jiraIssueId") long jiraIssueId, @QueryParam("actionId") int actionId) {
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
		ActionDescriptor actionDescriptor = ComponentAccessor.getWorkflowManager().getActionDescriptor(issue, actionId);
		PromptingEventConfiguration promptingEventConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(issue.getProjectObject().getKey());
		boolean isActivated = promptingEventConfiguration.isPromptEventActivated(feature, actionDescriptor.getName());
		return Response.ok(isActivated).build();
	}

	@Path("/activatePromptEvent")
	@POST
	public Response activatePromptEvent(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, @QueryParam("feature") String feature,
			@QueryParam("eventKey") String eventKey, @QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		PromptingEventConfiguration promptingEventConfiguration = ConfigPersistenceManager
				.getPromptingEventConfiguration(projectKey);
		FeatureWithPrompt featureWithPrompt = FeatureWithPrompt.getFeatureByName(feature);
		if (!promptingEventConfiguration.isValidFeature(featureWithPrompt)) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "No just-in-time prompt exists for the given feature.")).build();
		}
		promptingEventConfiguration.setPromptEvent(featureWithPrompt, eventKey, isActivated);
		ConfigPersistenceManager.savePromptingEventConfiguration(projectKey, promptingEventConfiguration);
		return Response.ok().build();
	}
}
