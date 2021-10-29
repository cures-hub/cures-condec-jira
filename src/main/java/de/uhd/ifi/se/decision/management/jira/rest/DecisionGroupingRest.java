package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
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
		List<String> groupsToAssign = new ArrayList<String>();
		groupsToAssign.add(level);
		if (!"".equals(existingGroups)) {
			String[] groupSplitArray = existingGroups.replace(" ", "").split(",");
			for (String group : groupSplitArray) {
				if (!groupsToAssign.contains(group)) {
					groupsToAssign.add(group);
				}
			}
		}
		if (!"".equals(addGroup)) {
			String[] groupSplitArray = addGroup.replace(" ", "").split(",");
			for (String group : groupSplitArray) {
				if (!groupsToAssign.contains(group)) {
					groupsToAssign.add(group);
				}
			}
		}
		DecisionGroupPersistenceManager.setGroupAssignment(groupsToAssign, element);
		inheritGroupAssignment(groupsToAssign, element);

		return Response.ok().build();
	}

	// TODO Simplify, this method is way too long and complex!
	private void inheritGroupAssignment(List<String> groupsToAssign, KnowledgeElement element) {
		if (element.getDocumentationLocation() != DocumentationLocation.CODE) {
			List<KnowledgeElement> linkedElements = new ArrayList<KnowledgeElement>();
			for (Link link : element.getLinks()) {
				KnowledgeElement linkedElement = link.getOppositeElement(element);
				if (linkedElement != null && linkedElement.getDocumentationLocation() == DocumentationLocation.CODE) {
					if (!linkedElement.getDecisionGroups().contains("Realization_Level")) {
						DecisionGroupPersistenceManager.insertGroup("Realization_Level", linkedElement);
					}
					for (String group : groupsToAssign) {
						if (!("High_Level").equals(group) && !("Medium_Level").equals(group)
								&& !("Realization_Level").equals(group)) {
							DecisionGroupPersistenceManager.insertGroup(group, linkedElement);
						}

					}
				} else if (linkedElement != null) {
					linkedElements.add(linkedElement);
					if ((linkedElement.getTypeAsString().equals("Decision")
							|| linkedElement.getTypeAsString().equals("Alternative")
							|| linkedElement.getTypeAsString().equals("Issue")) && linkedElement.getLinks() != null) {
						Set<Link> deeperLinks = linkedElement.getLinks();
						for (Link deeperLink : deeperLinks) {
							if (deeperLink != null && deeperLink.getTarget() != null
									&& deeperLink.getSource() != null) {
								KnowledgeElement deeperElement = deeperLink.getOppositeElement(linkedElement);
								if (deeperElement != null && (deeperElement.getTypeAsString().equals("Pro")
										|| deeperElement.getTypeAsString().equals("Con")
										|| deeperElement.getTypeAsString().equals("Decision")
										|| deeperElement.getTypeAsString().equals("Alternative"))) {
									linkedElements.add(deeperElement);
								}
							}
						}
					}
				}
			}
			for (KnowledgeElement ele : linkedElements) {
				DecisionGroupPersistenceManager.setGroupAssignment(groupsToAssign, ele);
			}
		}
	}

	@Path("/getDecisionGroups")
	@POST
	public Response getDecisionGroups(KnowledgeElement element) {
		if (element == null) {
			return Response.ok(Collections.emptyList()).build();
		}
		return Response.ok(element.getDecisionGroups()).build();
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

	@Path("/getAllDecisionGroups")
	@GET
	public Response getAllDecisionGroups(@QueryParam("projectKey") String projectKey) {
		Set<String> allGroups = DecisionGroupPersistenceManager.getAllDecisionGroups(projectKey);
		return Response.ok(allGroups).build();
	}
}