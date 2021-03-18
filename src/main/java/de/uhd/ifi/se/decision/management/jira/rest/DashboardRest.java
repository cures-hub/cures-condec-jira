package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.GeneralMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.CodeCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;


import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

/**
 * REST resource for dashboards
 */
@Path("/dashboard")
public class DashboardRest {

	@Path("/generalMetrics")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getGeneralMetrics(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		if (request == null || projectKey == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);

		Map<String, Object> metrics = new LinkedHashMap<>();
		GeneralMetricCalculator metricCalculator = new GeneralMetricCalculator(user, projectKey);

		metrics.put("numberOfCommentsPerJiraIssue", metricCalculator.numberOfCommentsPerIssue());
		metrics.put("numberOfCommitsPerJiraIssue", metricCalculator.getNumberOfCommits());
		metrics.put("distributionOfKnowledgeTypes", metricCalculator.getDistributionOfKnowledgeTypes());
		metrics.put("requirementsAndCodeFiles", metricCalculator.getReqAndClassSummary());
		metrics.put("numberOfElementsPerDocumentationLocation", metricCalculator.getElementsFromDifferentOrigins());
		metrics.put("numberOfRelevantComments", metricCalculator.getNumberOfRelevantComments());

		return Response.status(Status.OK).entity(metrics).build();
	}

	@Path("/rationaleCompleteness")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRationaleCompleteness(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		if (request == null || projectKey == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		Map<String, Object> metrics = new LinkedHashMap<>();

		RationaleCompletenessCalculator rationaleCompletenessCalculator = new RationaleCompletenessCalculator(projectKey);

		metrics.put("issuesSolvedByDecision", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.ISSUE, KnowledgeType.DECISION));
		metrics.put("decisionsSolvingIssues", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.ISSUE));
		metrics.put("proArgumentDocumentedForAlternative", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE, KnowledgeType.PRO));
		metrics.put("conArgumentDocumentedForAlternative", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.ALTERNATIVE, KnowledgeType.CON));
		metrics.put("proArgumentDocumentedForDecision", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.PRO));
		metrics.put("conArgumentDocumentedForDecision", rationaleCompletenessCalculator
			.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION, KnowledgeType.CON));

		return Response.status(Status.OK).entity(metrics).build();
	}

	@Path("/rationaleCoverage")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getRationaleCoverage(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
										 @QueryParam("issueType") String issueType, @QueryParam("linkDistance") String linkDistance) {
		if (request == null || projectKey == null || issueType == null || linkDistance == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		IssueType jiraIssueType = JiraSchemeManager.createIssueType(issueType);
		FilterSettings filterSettings = new FilterSettings(projectKey, "", user);
		filterSettings.setLinkDistance(Integer.parseInt(linkDistance));

		Map<String, Object> metrics = new LinkedHashMap<>();

		if (jiraIssueType != null) {
			RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(user, projectKey, filterSettings);

			metrics.put("decisionsPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));
			metrics.put("issuesPerJiraIssue",
				rationaleCoverageCalculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));
			metrics.put("decisionDocumentedForSelectedJiraIssue",
				rationaleCoverageCalculator.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.ISSUE));
			metrics.put("issueDocumentedForSelectedJiraIssue",
				rationaleCoverageCalculator.getJiraIssuesWithNeighborsOfOtherType(jiraIssueType, KnowledgeType.DECISION));
		}

		return Response.status(Status.OK).entity(metrics).build();
	}

	@Path("/codeCoverage")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCodeCoverage(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("linkDistance") String linkDistance) {
		if (request == null || projectKey == null || linkDistance == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		Map<String, Object> metrics = new LinkedHashMap<>();

		CodeCoverageCalculator codeCoverageCalculator = new CodeCoverageCalculator(projectKey, Integer.parseInt(linkDistance));

		metrics.put("issuesPerCodeFile",
			codeCoverageCalculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.ISSUE));
		metrics.put("decisionsPerCodeFile",
			codeCoverageCalculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.DECISION));
		metrics.put("decisionDocumentedForCodeFile",
			codeCoverageCalculator.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.ISSUE));
		metrics.put("issueDocumentedForCodeFile",
			codeCoverageCalculator.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.DECISION));

		return Response.status(Status.OK).entity(metrics).build();
	}

	@Path("/jiraIssueTypes")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getJiraIssueTypes(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		if (request == null || projectKey == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		Collection<IssueType> jiraIssueTypesCollection = new JiraSchemeManager(projectKey).getJiraIssueTypes();

		List<String> jiraIssueTypes =  new ArrayList<>();

		for (IssueType issueType : jiraIssueTypesCollection) {
			jiraIssueTypes.add(issueType.getName());
		}

		return Response.status(Status.OK).entity(jiraIssueTypes).build();
	}
}
