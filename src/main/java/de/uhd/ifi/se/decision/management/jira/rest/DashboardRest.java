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

		GeneralMetricCalculator generalMetricsCalculator = new GeneralMetricCalculator(user, filterSettings);

		return Response.status(Status.OK).entity(generalMetricsCalculator).build();
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

		RationaleCompletenessCalculator rationaleCompletenessCalculator =
			new RationaleCompletenessCalculator(user, filterSettings);

		return Response.status(Status.OK).entity(rationaleCompletenessCalculator).build();
	}

	@Path("/rationaleCoverage")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getRationaleCoverage(@Context HttpServletRequest request, FilterSettings filterSettings,
			@QueryParam("sourceKnowledgeTypes") String sourceKnowledgeTypes) {
		if (request == null || filterSettings == null || sourceKnowledgeTypes == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);

		RationaleCoverageCalculator rationaleCoverageCalculator = new RationaleCoverageCalculator(user, filterSettings, sourceKnowledgeTypes);

		return Response.status(Status.OK).entity(rationaleCoverageCalculator).build();
	}
}