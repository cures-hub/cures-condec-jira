package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.*;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.CommentMetricCalculator;

/**
 * REST resource for dashboards
 */
@Path("/dashboard")
public class DashboardRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewRest.class);

	@Path("/getNumberOfCommentsPerJiraIssue")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getNumberOfCommentsPerJiraIssue(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		if (request == null || projectKey == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "There is no project selected"))
				.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey);
		CommentMetricCalculator commentCalculator = new CommentMetricCalculator(jiraIssues);

		Map<String, Integer> numberOfCommentsPerIssue = commentCalculator.getNumberOfCommentsPerIssue();

		return Response.status(Status.OK).entity(numberOfCommentsPerIssue).build();
	}
}
