package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.metric.BranchMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.GeneralMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.RationaleCompletenessCalculator;
import de.uhd.ifi.se.decision.management.jira.metric.RationaleCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.view.dashboard.ConDecDashboardItem;

/**
 * REST resource for dashboard items to present metrics calculated on the
 * {@link KnowledgeGraph} data structure. Note that also the
 * {@link ConDecDashboardItem} is necessary to create dashboard items.
 * 
 * @see GeneralMetricCalculator
 * @see RationaleCompletenessCalculator
 * @see RationaleCoverageCalculator
 * @see BranchMetricCalculator
 */
@Path("/dashboard")
public class DashboardRest {

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} object.
	 * @return general metrics for the elements of the knowledge graph filtered with
	 *         the filter settings.
	 */
	@Path("/general-metrics")
	@POST
	public Response getGeneralMetrics(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
					.build();
		}
		return Response.ok(new GeneralMetricCalculator(filterSettings)).build();
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
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected."))
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
	 *         knowledge graph filtered with the filter settings.
	 */
	@Path("/rationale-coverage")
	@POST
	public Response getRationaleCoverage(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected."))
					.build();
		}

		return Response.ok(new RationaleCoverageCalculator(filterSettings)).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} object.
	 * @return metrics about the knowledge in git filtered with the filter settings.
	 */
	@Path("/git")
	@POST
	public Response getBranchMetrics(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected."))
					.build();
		}

		return Response.ok(new BranchMetricCalculator(filterSettings)).build();
	}
}