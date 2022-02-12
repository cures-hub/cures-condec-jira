package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * REST resource for configuration and usage of change impact analysis
 */
@Path("/change-impact-analysis")
public class ChangeImpactAnalysisRest {

	@Path("/cia-configuration")
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
		/**
		 * @issue How can we make sure that a new number of CIA rules in the backend is
		 *        available to the users?
		 * @decision Check whether the rules stored in the settings is equal to the 
		 *           provided ruleset to fix inconsistency between stored config and 
		 *           new code!
		 */
		for (ChangePropagationRule rule : ChangePropagationRule.getDefaultRules()) {
			if (!ciaConfig.getPropagationRules().contains(rule)) {
				ciaConfig.setPropagationRules(ChangePropagationRule.getDefaultRules());
			}
		}
		// Ensuring that linkTypeImpact is always up to date in case new linkTypes
		// are added, same reasoning/problem as with the ChangePropagationRules above
		Set<String> defaultLinkTypes = DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes();
		defaultLinkTypes.add(LinkType.WRONG_LINK.getInwardName());
		defaultLinkTypes.add(LinkType.WRONG_LINK.getOutwardName());
		for (String linkType : defaultLinkTypes) {
			if (!ciaConfig.getLinkImpact().containsKey(linkType)) {
				Map<String, Float> linkImpact = new HashMap<>();
				defaultLinkTypes.forEach(entry -> {
					if (entry == LinkType.WRONG_LINK.getInwardName() || entry == LinkType.WRONG_LINK.getOutwardName()) {
						linkImpact.put(entry, 0.0f);
					} else {
						linkImpact.put(entry, 1.0f);
					}
				});
				ciaConfig.setLinkImpact(linkImpact);
				break;
			}
		}
		ConfigPersistenceManager.saveChangeImpactAnalysisConfiguration(projectKey, ciaConfig);
		return Response.ok().build();
	}

	@GET
	@Path("/cia-configuration")
	public Response getChangeImpactAnalysisConfiguration(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		return Response.ok(ConfigPersistenceManager.getChangeImpactAnalysisConfiguration(projectKey)).build();
	}
}
