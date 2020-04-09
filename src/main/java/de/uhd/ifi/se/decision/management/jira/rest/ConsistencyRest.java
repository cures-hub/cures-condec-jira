package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public interface ConsistencyRest {

	Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
						  @QueryParam("issueKey") String issueKey);
}
