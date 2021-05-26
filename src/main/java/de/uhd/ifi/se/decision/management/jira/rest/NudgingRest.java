package de.uhd.ifi.se.decision.management.jira.rest;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.PromptingEventConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST resource for nudging functionality, in particular, for just-in-time
 * prompts.
 */
@Path("/nudging")
public class NudgingRest {

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

	@Path("/activatePromptEventForNonValidatedElements")
	@POST
	public Response activatePromptEventForNonValidatedElementsChecking(@Context HttpServletRequest request,
																	   @QueryParam("projectKey") String projectKey,
																	   @QueryParam("eventKey") String eventKey,
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
