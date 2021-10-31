package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import net.java.ao.Query;

/**
 * Responsible to persist levels ("high level", "medium level", "realization
 * level") and groups (e.g. "process", "UI") of decisions from all documentation
 * locations. Groups/levels are stored in the internal database of Jira via
 * object relational mapping (active objects framework).
 *
 * @see DecisionGroupInDatabase
 * @see KnowledgeElement#getDecisionGroups()
 */
public class DecisionGroupPersistenceManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static final List<String> LEVELS = List.of("high_level", "medium_level", "realization_level");

	/**
	 * @param groupId
	 *            id of a decision group/level in the database.
	 * @return true if decision group/level was deleted in database.
	 */
	public static boolean deleteGroup(long groupId) {
		if (groupId < 0) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("ID = ?", groupId))) {
			isDeleted = DecisionGroupInDatabase.deleteGroup(groupInDatabase);
		}
		return isDeleted;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} that currently is assigned to 1..*
	 *            decision groups/levels.
	 * @return true if all decision groups/levels were successfully unassigned from
	 *         the {@link KnowledgeElement}.
	 */
	public static boolean deleteAllGroupAssignments(KnowledgeElement element) {
		if (element == null) {
			return false;
		}
		boolean isDeleted = true;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(),
						element.getDocumentationLocation().getIdentifier()))) {
			isDeleted &= DecisionGroupInDatabase.deleteGroup(groupInDatabase);
		}
		return isDeleted;
	}

	/**
	 * @param group
	 *            name of the decision level ("high level", "medium level",
	 *            "realization level") or group (e.g. "process", "UI") to be
	 *            unassigned.
	 * @param projectKey
	 *            of a Jira project.
	 * @return true if the decision group/level was successfully deleted.
	 */
	public static boolean deleteGroup(String group, String projectKey) {
		if (group == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("GROUP = ? AND PROJECT_KEY = ?", group, projectKey))) {
			isDeleted = DecisionGroupInDatabase.deleteGroup(groupInDatabase);
		}
		return isDeleted;
	}

	/**
	 * Replaces all current existing decision group/level assignments for that
	 * element with a list of new decision groups/levels.
	 *
	 * @param groups
	 *            of groups to add
	 * @param element
	 *            The element that the groups should be assigned to
	 * @return If replacement was successful
	 */
	public static boolean setGroupAssignment(Set<String> groups, KnowledgeElement element) {
		if (groups == null || element == null) {
			return false;
		}
		boolean success;
		long id = 0;
		success = deleteAllGroupAssignments(element);
		for (String group : groups) {
			id = insertGroup(group, element);
		}

		if (element.getDocumentationLocation() == DocumentationLocation.CODE) {
			Set<KnowledgeElement> childElements = element.getLinkedElements(3);
			for (KnowledgeElement childElement : childElements) {
				if (childElement.getId() < 0 && childElement.getDescription().contains(element.getSummary())) {
					success = success && setGroupAssignment(groups, childElement);
				}
			}
		}

		inheritGroupAssignment(groups, element);

		return success & id != -1;
	}

	// TODO Simplify, this method is way too long and complex!
	public static void inheritGroupAssignment(Set<String> groupsToAssign, KnowledgeElement element) {
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
			// for (KnowledgeElement ele : linkedElements) {
			// DecisionGroupPersistenceManager.setGroupAssignment(groupsToAssign, ele);
			// }
		}
	}

	/**
	 * Deletes all groups that are not assigned to at least one existing
	 * {@link KnowledgeElement} or that have an empty name.
	 *
	 * @return true if at least one group was deleted.
	 */
	public static boolean deleteInvalidGroups() {
		boolean isGroupDeleted = false;
		DecisionGroupInDatabase[] groupsInDatabase = ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class);
		for (DecisionGroupInDatabase databaseEntry : groupsInDatabase) {
			String projectKey = databaseEntry.getProjectKey();
			KnowledgeElement element = KnowledgePersistenceManager.getInstance(projectKey)
					.getKnowledgeElement(databaseEntry.getSourceId(), databaseEntry.getSourceDocumentationLocation());
			if (element == null || databaseEntry.getGroup().isBlank()) {
				isGroupDeleted = true;
				DecisionGroupInDatabase.deleteGroup(databaseEntry);
			}
		}
		return isGroupDeleted;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement}.
	 * @return group/level names for the given {@link KnowledgeElement}, sorted so
	 *         that the level comes first.
	 */
	public static List<String> getGroupsForElement(KnowledgeElement element) {
		if (element == null || element.getId() == 0 || element.getDocumentationLocation() == null) {
			return new ArrayList<>();
		}
		List<String> groups = new LinkedList<>();
		DecisionGroupInDatabase[] groupsInDatabase = ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(),
						element.getDocumentationLocation().getIdentifier()));
		for (DecisionGroupInDatabase groupInDatabase : groupsInDatabase) {
			groups.add(groupInDatabase.getGroup());
		}
		return sortGroupNames(groups);
	}

	/**
	 * @param groupNames
	 *            names of decision groups and levels as a List of Strings.
	 * @return sorted List of decision groups and levels so that the levels come
	 *         first.
	 */
	public static List<String> sortGroupNames(List<String> groupNames) {
		for (String group : groupNames) {
			if (LEVELS.contains(group.toLowerCase())) {
				int indexOfLevel = LEVELS.indexOf(group.toLowerCase());
				if (groupNames.size() <= indexOfLevel) {
					indexOfLevel = 0;
				}
				Collections.swap(groupNames, groupNames.indexOf(group), indexOfLevel);
			}
		}
		return groupNames;
	}

	/**
	 * Inserts a new decision group/level assignment into database.
	 *
	 * @param sourceElement
	 *            {@link KnowledgeElement} that the decision group/level is assigned
	 *            to.
	 * @param group
	 *            name of the decision level ("high level", "medium level",
	 *            "realization level") or group (e.g. "process", "UI").
	 * @return internal database id of inserted decision group/level assignment, -1
	 *         if insertion failed.
	 */
	public static long insertGroup(String group, KnowledgeElement sourceElement) {
		if (group == null || sourceElement == null || group.isBlank()) {
			return -1;
		}
		long alreadyExistingId = isGroupAlreadyInDatabase(group, sourceElement);
		if (alreadyExistingId != -1) {
			return alreadyExistingId;
		}
		final DecisionGroupInDatabase groupInDatabase = ACTIVE_OBJECTS.create(DecisionGroupInDatabase.class);
		setParameters(sourceElement, group, groupInDatabase);
		groupInDatabase.save();

		List<Long> returnedIds = new ArrayList<Long>();

		if (sourceElement.getDocumentationLocation() == DocumentationLocation.CODE) {
			Set<KnowledgeElement> childElements = sourceElement.getLinkedElements(3);
			for (KnowledgeElement childElement : childElements) {
				if (childElement.getId() < 0 && childElement.getDescription().contains(sourceElement.getSummary())) {
					returnedIds.add(insertGroup(group, childElement));
				}
			}
		}
		if (returnedIds.contains(-1L)) {
			return -1;
		}
		return groupInDatabase.getId();
	}

	private static void setParameters(KnowledgeElement element, String groupName,
			DecisionGroupInDatabase databaseEntry) {
		String documentationLocationOfSourceElement = element.getDocumentationLocation().getIdentifier();
		databaseEntry.setSourceDocumentationLocation(documentationLocationOfSourceElement);
		databaseEntry.setSourceId(element.getId());
		databaseEntry.setProjectKey(element.getProject().getProjectKey());
		databaseEntry.setGroup(groupName);
	}

	/**
	 * @param groupName
	 *            name of the group/level, e.g. "high level" or "UI".
	 * @param element
	 *            {@link KnowledgeElement} that the decision group/level is assigned
	 *            to.
	 * @return group id if the entry already exists in database, otherwise -1.
	 */
	public static long isGroupAlreadyInDatabase(String groupName, KnowledgeElement element) {
		DecisionGroupInDatabase groupInDatabase = getDecisionGroupInDatabase(groupName, element);
		return groupInDatabase != null ? groupInDatabase.getId() : -1;
	}

	/**
	 * @param groupName
	 *            name of the group/level, e.g. "high level" or "UI".
	 * @param element
	 *            {@link KnowledgeElement} that the decision group/level is assigned
	 *            to.
	 * @return {@link DecisionGroupInDatabase} object.
	 */
	public static DecisionGroupInDatabase getDecisionGroupInDatabase(String groupName, KnowledgeElement element) {
		if (groupName == null || element == null) {
			return null;
		}
		DecisionGroupInDatabase groupInDatabase = null;
		for (DecisionGroupInDatabase databaseEntry : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("GROUP = ? AND SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", groupName,
						element.getId(), element.getDocumentationLocation().getIdentifier()))) {
			groupInDatabase = databaseEntry;
		}
		return groupInDatabase;
	}

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @return all decision group/level names for the project.
	 */
	public static List<String> getAllDecisionGroups(String projectKey) {
		Set<String> groupNames = new HashSet<>();
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			groupNames.add(groupInDatabase.getGroup());
		}
		return sortGroupNames(new ArrayList<>(groupNames));
	}

	/**
	 * @param oldGroupName
	 *            name of the decision group to be renamed, e.g. "UI" or "process".
	 *            Decision levels ("high level", "medium level", "realization
	 *            level") cannot be renamed.
	 * @param newGroupName
	 *            new name of the decision group.
	 * @param projectKey
	 *            of a Jira project.
	 * @return true if renaming was successful.
	 */
	public static boolean updateGroupName(String oldGroupName, String newGroupName, String projectKey) {
		boolean isRenamed = false;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND GROUP = ?", projectKey, oldGroupName))) {
			groupInDatabase.setGroup(newGroupName);
			groupInDatabase.save();
			isRenamed = true;
		}
		return isRenamed;
	}
}
