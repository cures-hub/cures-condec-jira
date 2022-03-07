package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import net.java.ao.Query;

/**
 * Responsible to persist levels ("high level", "medium level", "realization
 * level") and groups (e.g. "process", "UI") of decisions from all documentation
 * locations. Decision <b>group/level assignments are inherited to neighbor
 * {@link KnowledgeElement}s within a link distance of 3 in the
 * {@link KnowledgeGraph}</b>. Groups/levels are stored in the internal database
 * of Jira via object relational mapping (active objects framework).
 *
 * @see DecisionGroupInDatabase
 * @see KnowledgeElement#getDecisionGroups()
 */
public class DecisionGroupPersistenceManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static final List<String> LEVELS = List.of("realization_level", "medium_level", "high_level");

	/**
	 * @param groupName
	 *            name of the decision group (e.g. "process", "UI") to be deleted.
	 *            Decision levels ("high level", "medium level", "realization
	 *            level") cannot be deleted.
	 * @param projectKey
	 *            of a Jira project.
	 * @return true if the decision group was successfully deleted.
	 */
	public static boolean deleteGroup(String groupName, String projectKey) {
		if (groupName == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("GROUP = ? AND PROJECT_KEY = ?", groupName, projectKey))) {
			isDeleted = DecisionGroupInDatabase.deleteGroup(groupInDatabase);
		}
		return isDeleted;
	}

	/**
	 * @param groupNames
	 *            names of the decision level ("high level", "medium level",
	 *            "realization level") and groups (e.g. "process", "UI").
	 * @param element
	 *            {@link KnowledgeElement} that the decision group/level should be
	 *            assigned to.
	 * @return true if all current existing decision group/level assignments for
	 *         that element <b>and for neighbor elements in a link distance of 3</b>
	 *         were replaced with new decision groups and a level.
	 */
	public static boolean setGroupAssignment(Set<String> groupNames, KnowledgeElement element) {
		if (groupNames == null || element == null) {
			return false;
		}
		return inheritGroups(groupNames, element, 3);
	}

	private static boolean inheritGroups(Set<String> groupNames, KnowledgeElement element, int distance) {
		boolean success = resetGroups(groupNames, element);
		if (distance == 0) {
			return success;
		}
		for (KnowledgeElement neighborElement : element.getLinkedElements(1)) {
			if (shouldElementInheritGroupNames(groupNames, neighborElement)) {
				success &= inheritGroups(groupNames, neighborElement, distance - 1);
			}
		}
		return success;
	}

	private static boolean resetGroups(Set<String> groupNames, KnowledgeElement element) {
		deleteAllGroupAssignments(element);
		return insertGroups(groupNames, element);
	}

	private static boolean shouldElementInheritGroupNames(Set<String> groupNames, KnowledgeElement element) {
		return element.getType() != KnowledgeType.OTHER && !isEqual(element.getDecisionGroups(), groupNames);
	}

	public static boolean isEqual(Collection<String> groups1, Collection<String> groups2) {
		return groups1.containsAll(groups2) && groups2.containsAll(groups1);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} that currently is assigned to 1..*
	 *            decision groups/levels.
	 * @return true if all decision groups/levels were successfully unassigned from
	 *         the {@link KnowledgeElement}.
	 */
	public static boolean deleteAllGroupAssignments(KnowledgeElement element) {
		boolean isDeleted = true;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", element.getId(),
						element.getDocumentationLocation().getIdentifier()))) {
			isDeleted &= DecisionGroupInDatabase.deleteGroup(groupInDatabase);
		}
		return isDeleted;
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
	 *         first and groups are sorted alphabetically.
	 */
	public static List<String> sortGroupNames(List<String> groupNames) {
		Collections.sort(groupNames);
		for (String level : LEVELS) {
			if (groupNames.removeIf(groupName -> groupName.toLowerCase().equals(level))) {
				groupNames.add(0, WordUtils.capitalize(level, "_".toCharArray()));
			}
		}
		return groupNames;
	}

	/**
	 * Inserts new decision group/level assignments into database.
	 *
	 * @param groupNames
	 *            names of the decision level ("high level", "medium level",
	 *            "realization level") and groups (e.g. "process", "UI").
	 * @param sourceElement
	 *            {@link KnowledgeElement} that the decision group/level should be
	 *            assigned to.
	 * @return true if insertion of group into database was successful.
	 */
	public static boolean insertGroups(Collection<String> groupNames, KnowledgeElement sourceElement) {
		boolean isInserted = true;
		for (String group : groupNames) {
			isInserted &= insertGroup(group, sourceElement) > -1;
		}
		return isInserted;
	}

	/**
	 * Inserts a new decision group/level assignment into database.
	 *
	 * @param sourceElement
	 *            {@link KnowledgeElement} that the decision group/level should be
	 *            assigned to.
	 * @param groupName
	 *            name of the decision level ("high level", "medium level",
	 *            "realization level") or group (e.g. "process", "UI").
	 * @return internal database id of inserted decision group/level assignment, -1
	 *         if insertion failed.
	 */
	public static long insertGroup(String groupName, KnowledgeElement sourceElement) {
		if (groupName == null || sourceElement == null || groupName.isBlank()) {
			return -1;
		}
		long isGroupAlreadyInDatabase = isGroupAlreadyInDatabase(groupName, sourceElement);
		if (isGroupAlreadyInDatabase != -1) {
			return isGroupAlreadyInDatabase;
		}
		DecisionGroupInDatabase groupInDatabase = ACTIVE_OBJECTS.create(DecisionGroupInDatabase.class);
		setParameters(sourceElement, groupName, groupInDatabase);
		groupInDatabase.save();
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
	private static long isGroupAlreadyInDatabase(String groupName, KnowledgeElement element) {
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
	private static DecisionGroupInDatabase getDecisionGroupInDatabase(String groupName, KnowledgeElement element) {
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
