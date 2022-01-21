package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangeImpactAnalysisConfiguration;
import de.uhd.ifi.se.decision.management.jira.changeimpactanalysis.ChangePropagationRule;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * REST resource for configuration and usage of change impact analysis
 */
@Path("/change-impact-analysis")
public class ChangeImpactAnalysisRest {
    
    @Path("/setChangeImpactAnalysisConfiguration")
	@POST
	public Response setChangeImpactAnalysisConfiguration(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, ChangeImpactAnalysisConfiguration ciaConfig) {
		Response response = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return response;
		}
		if (ciaConfig == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The CIA config must not be null!")).build();
		}
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration(projectKey, ciaConfig);
		return Response.ok().build();
	}

	@GET
	@Path("/getChangeImpactAnalysisConfiguration")
	public Response getChangeImpactAnalysisConfiguration(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		return Response.ok(ConfigPersistenceManager.getChangeImpactAnalysisConfiguration(projectKey)).build();
	}

	@GET
	@Path("/getActiveChangeImpactAnalysisRules")
	public Response getActiveChangeImpactAnalysisRules(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		List<String> activeRules = new ArrayList<String>();
		for (ChangePropagationRule rule : ConfigPersistenceManager.getChangeImpactAnalysisConfiguration(projectKey).getPropagationRules()) {
			if (rule.isActive()) {
				activeRules.add(rule.getDescription());
			}
		}
		return Response.ok(activeRules).build();
	}
}
