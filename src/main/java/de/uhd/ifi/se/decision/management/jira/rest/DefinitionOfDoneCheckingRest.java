package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDoneChecker;
import de.uhd.ifi.se.decision.management.jira.quality.QualityCriterionCheckResult;

/**
 * REST resource for definition of done (DoD) configuration and checking.
 * 
 * @see DefinitionOfDone
 * @see DefinitionOfDoneChecker
 */
@Path("/quality-checking")
public class DefinitionOfDoneCheckingRest {

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param definitionOfDone
	 *            {@link DefinitionOfDone} object that specifies criteria that the
	 *            knowledge documentation needs to fulfill.
	 * @return ok if the DoD was successfully saved.
	 */
	@Path("/configuration/{projectKey}/definition-of-done")
	@POST
	public Response setDefinitionOfDone(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
			DefinitionOfDone definitionOfDone) {
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            {@link FilterSettings} with a selected {@link KnowledgeElement}.
	 * @return list of {@link QualityCriterionCheckResult}s.
	 */
	@Path("/quality-check-results")
	@POST
	public Response getQualityCheckResults(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Quality check could not be performed due to a bad request."))
					.build();
		}

		KnowledgeElement knowledgeElement = filterSettings.getSelectedElement();
		if (knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Quality check could not be performed because the element could not be found.")).build();
		}
		return Response.ok().entity(DefinitionOfDoneChecker.getQualityCheckResults(knowledgeElement, filterSettings))
				.build();
	}
}