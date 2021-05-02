package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.JiraSchemeManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.GeneralMetricCalculator;

/**
 * REST resource for dashboards
 */
@Path("/dashboard")
public class DashboardRest {

	@Path("/generalMetrics")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getGeneralMetrics(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		Map<String, Object> metrics = new LinkedHashMap<>();

		GeneralMetricCalculator metricCalculator = new GeneralMetricCalculator(user, filterSettings);

		metrics.put("numberOfCommentsPerJiraIssue", metricCalculator.numberOfCommentsPerIssue());
		metrics.put("numberOfCommitsPerJiraIssue", metricCalculator.getNumberOfCommits());
		metrics.put("distributionOfKnowledgeTypes", metricCalculator.getDistributionOfKnowledgeTypes());
		metrics.put("requirementsAndCodeFiles", metricCalculator.getReqAndClassSummary());
		metrics.put("numberOfElementsPerDocumentationLocation", metricCalculator.getElementsFromDifferentOrigins());
		metrics.put("numberOfRelevantComments", metricCalculator.getNumberOfRelevantComments());

		return Response.status(Status.OK).entity(metrics).build();
	}

	@Path("/rationaleCompleteness")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRationaleCompleteness(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		Map<String, Object> metrics = new LinkedHashMap<>();

		RationaleCompletenessCalculator rationaleCompletenessCalculator = new RationaleCompletenessCalculator(user,
				filterSettings);

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
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRationaleCoverage(@Context HttpServletRequest request, FilterSettings filterSettings,
			@QueryParam("sourceKnowledgeTypes") String sourceKnowledgeTypesString) {
		if (request == null || filterSettings == null || sourceKnowledgeTypesString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}

		Set<String> sourceKnowledgeTypes;
		if (sourceKnowledgeTypesString.isEmpty()) {
			sourceKnowledgeTypes = new DecisionKnowledgeProject(filterSettings.getProjectKey()).getNamesOfKnowledgeTypes();
		} else {
			sourceKnowledgeTypes = new HashSet<>(Arrays.asList(sourceKnowledgeTypesString.split(",")));
		}

		ApplicationUser user = AuthenticationManager.getUser(request);

		Map<String, Object> metrics = new LinkedHashMap<>();

		RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(user, filterSettings);

		if (!sourceKnowledgeTypes.isEmpty()) {
			metrics.put("decisionsPerSelectedJiraIssue", rationaleCoverageCalculator
				.getNumberOfDecisionKnowledgeElementsForKnowledgeElements(sourceKnowledgeTypes, KnowledgeType.DECISION));
			metrics.put("issuesPerSelectedJiraIssue", rationaleCoverageCalculator
				.getNumberOfDecisionKnowledgeElementsForKnowledgeElements(sourceKnowledgeTypes, KnowledgeType.ISSUE));
			metrics.put("decisionDocumentedForSelectedJiraIssue", rationaleCoverageCalculator
				.getKnowledgeElementsWithNeighborsOfOtherType(sourceKnowledgeTypes, KnowledgeType.ISSUE));
			metrics.put("issueDocumentedForSelectedJiraIssue", rationaleCoverageCalculator
				.getKnowledgeElementsWithNeighborsOfOtherType(sourceKnowledgeTypes, KnowledgeType.DECISION));
		}

		return Response.status(Status.OK).entity(metrics).build();
	}
}