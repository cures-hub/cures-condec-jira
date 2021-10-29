package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
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

	// TODO Refactor: too many ifs
	@Path("/getDecisionGroups")
	@GET
	public Response getDecisionGroups(@QueryParam("elementId") long id, @QueryParam("location") String location,
			@QueryParam("projectKey") String projectKey) {
		if (id == -1 || location == null || projectKey == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		KnowledgeElement element = KnowledgePersistenceManager.getInstance(projectKey).getKnowledgeElement(id,
				location);
		if (element != null) {
			List<String> groups = element.getDecisionGroups();
			if (groups != null) {
				for (String group : groups) {
					if (("High_Level").equals(group) || ("Medium_Level").equals(group)
							|| ("Realization_Level").equals(group)) {
						int index = groups.indexOf(group);
						if (index != 0) {
							Collections.swap(groups, 0, index);
						}
					}
				}
				return Response.ok(groups).build();
			}
		}
		return Response.ok(Collections.emptyList()).build();
	}

	@Path("/getAllDecisionElementsWithCertainGroup")
	@GET
	public Response getAllDecisionElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupPersistenceManager.getAllKnowledgeElementsWithCertainGroup(group, projectKey);
		return Response.ok(keys).build();
	}

	@Path("/getAllClassElementsWithCertainGroup")
	@GET
	public Response getAllClassElementsWithCertainGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("group") String group) {
		List<String> keys = DecisionGroupPersistenceManager.getAllClassElementsWithCertainGroup(group, projectKey);
		return Response.ok(keys).build();
	}

	@Path("/renameDecisionGroup")
	@GET
	public Response renameDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("oldName") String oldGroupName, @QueryParam("newName") String newGroupName) {
		if (DecisionGroupPersistenceManager.updateGroupName(oldGroupName, newGroupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to rename found")).build();
	}

	@Path("/deleteDecisionGroup")
	@GET
	public Response deleteDecisionGroup(@QueryParam("projectKey") String projectKey,
			@QueryParam("groupName") String groupName) {
		if (DecisionGroupPersistenceManager.deleteGroup(groupName, projectKey)) {
			return Response.ok(true).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "No group to delete found")).build();
	}

	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		Set<String> allGroups = DecisionGroupPersistenceManager.getAllDecisionGroups(projectKey);
		return Response.ok(allGroups).build();
	}
}