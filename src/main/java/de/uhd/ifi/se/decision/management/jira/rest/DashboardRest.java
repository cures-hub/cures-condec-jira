package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.GeneralMetricCalculator;

/**
 * REST resource for dashboards
 */
@Path("/dashboard")
public class DashboardRest {

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} object.
	 * @return general metrics for the elements of the knowledge graph filtered by
	 *         the filter settings.
	 */
	@Path("/general-metrics")
	@POST
	public Response getGeneralMetrics(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}
		GeneralMetricCalculator generalMetricsCalculator = new GeneralMetricCalculator(filterSettings);

		return Response.ok(generalMetricsCalculator).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} object.
	 * @return metrics about the intra-rationale completeness for the elements of
	 *         the knowledge graph filtered by the filter settings.
	 */
	@Path("/rationale-completeness")
	@POST
	public Response getRationaleCompleteness(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}
		return Response.ok(new RationaleCompletenessCalculator(filterSettings)).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} object.
	 * @return metrics about the rationale coverage for the elements of the
	 *         knowledge graph filtered by the filter settings.
	 */
	@Path("/rationale-coverage")
	@POST
	public Response getRationaleCoverage(@Context HttpServletRequest request, FilterSettings filterSettings,
			@QueryParam("sourceKnowledgeTypes") String sourceKnowledgeTypes) {
		if (request == null || filterSettings == null || sourceKnowledgeTypes == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}
		return Response.ok(new RationaleCoverageCalculator(filterSettings)).build();
	}
}