package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.issue.Issue;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * REST resource for definition of done (DoD) configuration and checking.
 */
@Path("/dodChecking")
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

		ConfigPersistenceManager.saveDefinitionOfDone(projectKey, definitionOfDone);
		return Response.ok().build();
	}

	@Path("/getFailedDefinitionOfDoneCriteria")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFailedDefinitionOfDoneCriteria(@Context HttpServletRequest request,
			FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Completeness check could not be performed due to a bad request."))
					.build();
		}
		KnowledgeElement knowledgeElement = filterSettings.getSelectedElement();
		if (knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"DoD check could not be performed because the element could not be found.")).build();
		}

		return Response.ok().entity(DefinitionOfDoneChecker.
			getFailedDefinitionOfDoneCheckCriteria(knowledgeElement, filterSettings)).build();
	}

	@Path("/getCoverageOfJiraIssue")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCoverageOfJiraIssue(@Context HttpServletRequest request,
	   		@QueryParam("projectKey") String projectKey, @QueryParam("issueKey") String issueKey) {
		if (request == null || projectKey == null || issueKey == null) {
			return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Quality check could not be performed due to a bad request."))
				.build();
		}

		Issue jiraIssue = JiraIssuePersistenceManager.getJiraIssue(issueKey);

		KnowledgeElement knowledgeElement = new KnowledgeElement(jiraIssue);

		RationaleCoverageCalculator calculator = new RationaleCoverageCalculator(projectKey);

		Map<String, Integer> results = new HashMap<>();

		results.put(KnowledgeType.ISSUE.toString(),
			calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(knowledgeElement, KnowledgeType.ISSUE));
		results.put(KnowledgeType.DECISION.toString(),
			calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(knowledgeElement, KnowledgeType.DECISION));

		return Response.ok().entity(results).build();
	}
}
