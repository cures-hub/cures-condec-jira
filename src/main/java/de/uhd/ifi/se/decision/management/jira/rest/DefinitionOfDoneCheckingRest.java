package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CompletenessHandler;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;

/**
 * REST resource for definition of done (DoD) configuration and checking.
 */
@Path("/dodchecking")
public class DefinitionOfDoneCheckingRest {

	@Path("/setDefinitionOfDone")
	@POST
	public Response setDefinitionOfDone(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, DefinitionOfDone definitionOfDone) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}

		if (definitionOfDone == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The name of the knowledge source must not be empty")).build();
		}

		ConfigPersistenceManager.setDefinitionOfDone(projectKey, definitionOfDone);
		return Response.ok().build();
	}

	@Path("/doesElementNeedCompletenessApproval")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doesElementNeedCompletenessApproval(@Context HttpServletRequest request,
			FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Completeness check could not be performed due to a bad request."))
					.build();
		}
		KnowledgeElement knowledgeElement = filterSettings.getSelectedElement();
		if (knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Completeness check could not be performed because the element could not be found."))
					.build();
		}
		boolean doesIssueNeedApproval = CompletenessHandler.hasIncompleteKnowledgeLinked(knowledgeElement);
		return Response.ok().entity(ImmutableMap.of("needsCompletenessApproval", doesIssueNeedApproval)).build();
	}
}
