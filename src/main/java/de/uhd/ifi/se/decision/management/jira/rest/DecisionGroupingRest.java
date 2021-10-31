package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(DecisionGroupingRest.class);

	@Path("/assignDecisionGroup")
	@POST
	public Response assignDecisionGroup(@Context HttpServletRequest request, @QueryParam("level") String level,
			@QueryParam("existingGroups") String existingGroups, @QueryParam("addGroup") String addGroup,
			KnowledgeElement element) {
		Set<String> groupsToAssign = new HashSet<String>();
		groupsToAssign.add(level);
		String[] groupSplitArray = existingGroups.replace(" ", "").split(",");
		for (String group : groupSplitArray) {
			groupsToAssign.add(group);
		}
		groupSplitArray = addGroup.replace(" ", "").split(",");
		for (String group : groupSplitArray) {
			groupsToAssign.add(group);
		}
		groupsToAssign.removeIf(groupName -> groupName.isBlank());
		if (DecisionGroupPersistenceManager.setGroupAssignment(groupsToAssign, element)) {
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Decision groups and level could not be assigned.")).build();
	}

	/**
	 * @issue How can we keep the sorting of the list when passing it through the
	 *        REST API?
	 * @decision Cast the list to a TreeSet to keep sorting when passing it through
	 *           the REST API!
	 * @pro No other java.util data structure seems to keep the sorting than
	 *      TreeSet.
	 * 
	 * @param element
	 *            {@link KnowledgeElement}, e.g., decision, code file, or
	 *            requirement.
	 * @return all decision groups/levels for one {@link KnowledgeElement}.
	 */
	@Path("/getDecisionGroupsForElement")
	@POST
	public Response getDecisionGroupsForElement(KnowledgeElement element) {
		if (element == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		return Response.ok(new TreeSet<>(element.getDecisionGroups())).build();
	}

	@Path("/renameDecisionGroup")
	@GET
	public Response renameDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupPersistenceManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to rename found."))
				.build();
	}

	@Path("/deleteDecisionGroup")
	@GET
	public Response deleteDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		if (DecisionGroupPersistenceManager.deleteGroup(groupName, projectKey)) {
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to delete found."))
				.build();
	}

	/**
	 * @issue How can we keep the sorting of the list when passing it through the
	 *        REST API?
	 * @decision Cast the list to a TreeSet to keep sorting when passing it through
	 *           the REST API!
	 * @pro No other java.util data structure seems to keep the sorting than
	 *      TreeSet.
	 * 
	 * @param projectKey
	 *            of a Jira project.
	 * @return all decision groups/levels for one project sorted so that levels
	 *         (high level, medium level, realization level) come first.
	 */
	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		List<String> allGroupNames = DecisionGroupPersistenceManager.getAllDecisionGroups(projectKey);
		return Response.ok(new TreeSet<>(allGroupNames)).build();
	}
}