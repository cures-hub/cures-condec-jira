package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDoneChecker;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.QualityProblemType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.RationaleCoverageCalculator;

/**
 * REST resource for definition of done (DoD) configuration and checking.
 */
@Path("/dodChecking")
public class DefinitionOfDoneCheckingRest {

	@Path("/setDefinitionOfDone")
	@POST
	public Response setDefinitionOfDone(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, DefinitionOfDone definitionOfDone) {
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
	 * Get a list of the {@link QualityProblemType} of the {@link KnowledgeElement}
	 * selected in the {@link FilterSettings}.
	 *
	 * @param request
	 * @param filterSettings
	 * @return List<QualityProblem> A list containing the
	 *         {@link QualityProblemType}.
	 */
	@Path("/getQualityProblems")
	@POST
	public Response getQualityProblems(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey().isEmpty()) {
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
