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
import de.uhd.ifi.se.decision.management.jira.metric.RationaleCoverageCalculator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDoneChecker;
import de.uhd.ifi.se.decision.management.jira.quality.QualityProblem;

/**
 * REST resource for definition of done (DoD) configuration and checking.
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
	 * @return list of {@link QualityProblem}s.
	 */
	@Path("/quality-problems")
	@POST
	public Response getQualityProblems(@Context HttpServletRequest request, FilterSettings filterSettings) {
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
		return Response.ok().entity(DefinitionOfDoneChecker.getQualityProblems(knowledgeElement, filterSettings))
				.build();
	}

	/**
	 * Get the coverage with decisions of the {@link KnowledgeElement} selected in
	 * the {@link FilterSettings}.
	 *
	 * @param request
	 * @param filterSettings
	 * @return coverage How many decisions are linked to the selected
	 *         {@link KnowledgeElement}.
	 */
	@Path("/getCoverageOfJiraIssue")
	@POST
	public Response getCoverageOfJiraIssue(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey().isEmpty()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Coverage check could not be performed due to a bad request."))
					.build();
		}

		KnowledgeElement knowledgeElement = filterSettings.getSelectedElement();
		if (knowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Coverage check could not be performed because the element could not be found.")).build();
		}

		return Response.ok()
				.entity(RationaleCoverageCalculator
						.getReachableElementsOfType(knowledgeElement, KnowledgeType.DECISION, filterSettings).size())
				.build();
	}
}
