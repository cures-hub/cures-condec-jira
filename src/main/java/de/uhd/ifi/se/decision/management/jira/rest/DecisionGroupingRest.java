package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;

/**
 * REST resource for the management of levels ("high level", "medium level",
 * "realization level") and groups (e.g. "process", "UI") of decisions from all
 * documentation locations.
 * 
 * @see DecisionGroupPersistenceManager
 * @see DecisionGroupInDatabase
 */
@Path("/grouping")
public class DecisionGroupingRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionGroupingRest.class);

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param level
	 *            name of the decision level ("high level", "medium level", or
	 *            "realization level").
	 * @param existingGroups
	 *            names of groups already assigned to the element (e.g. "process",
	 *            "UI").
	 * @param groupsToAdd
	 *            names of groups to be newly assigned to the element (e.g.
	 *            "process", "UI").
	 * @param element
	 *            {@link KnowledgeElement} that the decision group/level should be
	 *            assigned to.
	 * @return ok if all current existing decision group/level assignments for that
	 *         element <b>and for neighbor elements in a link distance of 3</b> were
	 *         replaced with the provided decision groups and a level.
	 */
	@Path("/assign")
	@POST
	public Response assignDecisionGroup(@Context HttpServletRequest request, @QueryParam("level") String level,
			@QueryParam("existingGroups") String existingGroups, @QueryParam("addGroup") String groupsToAdd,
			KnowledgeElement element) {
		Set<String> groupsToAssign = new HashSet<String>();
		groupsToAssign.add(level);
		String[] groupSplitArray = parseGroupNamesString(existingGroups);
		for (String group : groupSplitArray) {
			groupsToAssign.add(group);
		}
		groupSplitArray = parseGroupNamesString(groupsToAdd);
		for (String group : groupSplitArray) {
			groupsToAssign.add(group);
		}
		groupsToAssign.removeIf(groupName -> groupName.isBlank());
		if (DecisionGroupPersistenceManager.setGroupAssignment(groupsToAssign, element)) {
			LOGGER.info("The groups " + groupsToAssign + " were assigned to " + element);
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision groups and level could not be assigned.")).build();
	}

	private static String[] parseGroupNamesString(String groupNamesString) {
		return groupNamesString.replace(" ", "").split(",");
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement}, e.g., decision, code file, or
	 *            requirement.
	 * @return all decision groups/levels for one {@link KnowledgeElement}.
	 */
	@Path("/groups-for-element")
	@POST
	public Response getDecisionGroupsForElement(KnowledgeElement element) {
		if (element == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		return Response.ok(element.getDecisionGroups()).build();
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @param oldGroupName
	 *            name of the decision group to be renamed, e.g. "UI" or "process".
	 *            Decision levels ("high level", "medium level", "realization
	 *            level") cannot be renamed.
	 * @param newGroupName
	 *            new name of the decision group.
	 * @return ok if renaming was successful.
	 */
	@Path("/{projectKey}/rename")
	@GET
	public Response renameDecisionGroup(@PathParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupPersistenceManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			LOGGER.info("The group " + oldGroupName + " was renamed to " + newGroupName + ".");
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to rename found."))
				.build();
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @param groupName
	 *            name of the decision group (e.g. "process", "UI") to be deleted.
	 *            Decision levels ("high level", "medium level", "realization
	 *            level") cannot be deleted.
	 * @return ok if the decision group was successfully deleted.
	 */
	@Path("/{projectKey}")
	@DELETE
	public Response deleteDecisionGroup(@PathParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		if (DecisionGroupPersistenceManager.deleteGroup(groupName, projectKey)) {
			LOGGER.info("The group " + groupName + " was deleted.");
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to delete found."))
				.build();
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @return all decision groups/levels for one project sorted so that levels
	 *         (high level, medium level, realization level) come first.
	 */
	@Path("/{projectKey}")
	@GET
	public Response getAllDecisionGroups(@PathParam("projectKey") String projectKey) {
		List<String> allGroupNames = DecisionGroupPersistenceManager.getAllDecisionGroups(projectKey);
		return Response.ok(allGroupNames).build();
	}

	/**
	 * @param filterSettings
	 *            object of {@link FilterSettings} e.g. specifying the
	 *            {@link KnowledgeType}s to include in the results.
	 * @return map with decision levels and decision groups as keys and the
	 *         respective {@link KnowledgeElement}s that are tagged with the group
	 *         as values.
	 */
	@Path("/groups-and-elements")
	@POST
	public Response getDecisionGroupsMap(FilterSettings filterSettings) {
		if (filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Filter settings are missing."))
					.build();
		}
		Map<String, Set<KnowledgeElement>> decisionGroupsMap = new LinkedHashMap<>();

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementsMatchingFilterSettings = filteringManager.getElementsMatchingFilterSettings();
		List<String> allLevelsAndGroups = DecisionGroupPersistenceManager
				.getAllDecisionGroups(filterSettings.getProjectKey());
		allLevelsAndGroups.add("NoGroup"); // for ungrouped elements

		// init group to elements map
		for (String group : allLevelsAndGroups) {
			decisionGroupsMap.put(group, new HashSet<>());
		}
		for (KnowledgeElement element : elementsMatchingFilterSettings) {
			List<String> groupsOfElement = element.getDecisionGroups();
			if (groupsOfElement.size() <= 1) {
				decisionGroupsMap.get("NoGroup").add(element);
			}
			for (String group : groupsOfElement) {
				Set<KnowledgeElement> elementsOfGroup = decisionGroupsMap.get(group);
				if (elementsOfGroup == null) {
					LOGGER.error("Invalid group: " + group);
					continue;
				}
				elementsOfGroup.add(element);
			}
		}
		return Response.ok(decisionGroupsMap).build();
	}

	/**
	 * @param filterSettings
	 *            object of {@link FilterSettings} e.g. specifying the
	 *            {@link KnowledgeType}s to include in the results.
	 * @return map with coverage (i.e. number of decision levels and decision groups
	 *         assigned) as keys and the respective {@link KnowledgeElement}s that
	 *         are tagged with the number as values.
	 */
	@Path("/coverage")
	@POST
	public Response getDecisionGroupCoverage(FilterSettings filterSettings) {
		if (filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Filter settings are missing."))
					.build();
		}
		Map<Integer, List<KnowledgeElement>> coverageMap = new LinkedHashMap<>();
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementsMatchingFilterSettings = filteringManager.getElementsMatchingFilterSettings();
		for (KnowledgeElement element : elementsMatchingFilterSettings) {
			List<String> groupsOfElement = element.getDecisionGroups();
			int numberOfGroupsOfElement = groupsOfElement.size();
			if (!coverageMap.containsKey(numberOfGroupsOfElement)) {
				coverageMap.put(numberOfGroupsOfElement, new ArrayList<>());
			}
			coverageMap.get(numberOfGroupsOfElement).add(element);
		}
		return Response.ok(coverageMap).build();
	}
}