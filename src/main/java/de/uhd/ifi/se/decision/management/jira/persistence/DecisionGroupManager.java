package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.atlassian.activeobjects.external.ActiveObjects;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import net.java.ao.Query;

/**
 * Responsible for groups of decisions from all documentation locations. Groups
 * are stored in the internal database of Jira via object relational mapping
 * (active objects framework).
 *
 * @see KnowledgePersistenceManager
 * @see DecisionGroupInDatabase
 * @see JiraIssuePersistenceManager
 */
public class DecisionGroupManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static boolean deleteGroupAssignment(Long elementId) {
		if (elementId == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase dgData : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (dgData.getId() == elementId) {
				isDeleted = DecisionGroupInDatabase.deleteGroup(dgData);
			}
		}
		return isDeleted;
	}

	public static boolean deleteGroupAssignment(String group, KnowledgeElement element) {
		if (element == null || group == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase dgData : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (dgData.getGroup().equals(group) && dgData.getSourceId() == element.getId()
					&& dgData.getSourceDocumentationLocation().equals(element.getDocumentationLocation().getIdentifier())) {
				isDeleted = DecisionGroupInDatabase.deleteGroup(dgData);
			}
		}

		if (element.getDocumentationLocation().getIdentifier().equals("c")) {
			Set<KnowledgeElement> childElements = element.getLinkedElements(3);
			for (KnowledgeElement childElement : childElements) {
				if (childElement.getId() < 0 && childElement.getDescription().contains(element.getSummary())) {
					isDeleted = isDeleted && deleteGroupAssignment(group, childElement);
				}
			}
		}
		return isDeleted;
	}

	public static boolean deleteAllGroupAssignments(KnowledgeElement element) {
		if (element == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase dgData : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (dgData.getSourceId() == element.getId()
					&& dgData.getSourceDocumentationLocation().equals(element.getDocumentationLocation().getIdentifier())
					&& dgData.getProjectKey().equals(element.getProject().getProjectKey())) {
				isDeleted = DecisionGroupInDatabase.deleteGroup(dgData);
			}
		}
		return isDeleted;
	}

	public static boolean deleteGroup(String group, String projectKey) {
		if (group == null) {
			return false;
		}
		boolean isDeleted = false;
		for (DecisionGroupInDatabase dgData : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (dgData.getGroup().equals(group) && dgData.getProjectKey().equals(projectKey)) {
				isDeleted = DecisionGroupInDatabase.deleteGroup(dgData);
			}
		}
		return isDeleted;
	}

	/**
	 * Replaces all current existing assignments for that element with a list of new
	 * groups
	 *
	 * @param groups of groups to add
	 * @param element The element that the groups should be assigned to
	 * @return If replacement was successful
	 */
	public static boolean setGroupAssignment(List<String> groups, KnowledgeElement element) {
		if (groups == null || element == null) {
			return false;
		}
		boolean success;
		long id = 0;
		success = deleteAllGroupAssignments(element);
		for (String group : groups) {
			id = insertGroup(group, element);
		}

		if (element.getDocumentationLocation().getIdentifier().equals("c")) {
			Set<KnowledgeElement> childElements = element.getLinkedElements(3);
			for (KnowledgeElement childElement : childElements) {
				if (childElement.getId() < 0 && childElement.getDescription().contains(element.getSummary())) {
					success = success && setGroupAssignment(groups, childElement);
				}
			}
		}

		return (success && id != -1);

	}

	/**
	 * Deletes all groups that are not assigned to at least one existing
	 * KnowledgeElement.
	 *
	 * @return true if at least one group was deleted
	 */
	public static boolean deleteInvalidGroups() {
		boolean isGroupDeleted = false;
		DecisionGroupInDatabase[] groupsInDatabase = ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class);
		for (DecisionGroupInDatabase databaseEntry : groupsInDatabase) {
			String projectKey = databaseEntry.getProjectKey();
			KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getKnowledgeElement(databaseEntry.getSourceId(), databaseEntry.getSourceDocumentationLocation());
			if (element == null) {
				isGroupDeleted = true;
				DecisionGroupInDatabase.deleteGroup(databaseEntry);
			}
		}
		return isGroupDeleted;
	}

	/**
	 * Returns all groups for a given KnowledgeElement.
	 *
	 * @param element node in the {@link KnowledgeGraph}.
	 * @return list of Strings of group assignments
	 * @see KnowledgeElement
	 */
	public static List<String> getGroupsForElement(KnowledgeElement element) {
		if (element == null) {
			return null;
		}
		return getGroupsForElement(element.getId(), element.getDocumentationLocation());
	}

	/**
	 * Returns all groups for a given KnowledgeElement.
	 *
	 * @param elementId       node id in the {@link KnowledgeGraph}.
	 * @param documentationLocation location of the KnowledgeElement
	 * @return list of Strings of group assignments
	 * @see KnowledgeElement
	 */
	public static List<String> getGroupsForElement(long elementId, DocumentationLocation documentationLocation) {
		List<String> groups = new ArrayList<>();
		if (elementId == 0 || elementId == -1 || documentationLocation == null) {
			return null;
		}
		String identifier = documentationLocation.getIdentifier();
		DecisionGroupInDatabase[] groupsInDatabase = ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
				Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", elementId, identifier));
		for (DecisionGroupInDatabase groupInDatabase : groupsInDatabase) {
			String group = groupInDatabase.getGroup();
			groups.add(group);
		}
		for (String group : groups) {
			if (("High_Level").equals(group) || ("Medium_Level").equals(group) || ("Realization_Level").equals(group)) {
				Collections.swap(groups, groups.indexOf(group), 0);
			}
		}
		return groups;
	}

	/**
	 * Inserts a new group assignment into database.
	 *
	 * @param sourceElement Name of the Group
	 * @param group    KnowledgeElement that the group is assigned to
	 * @return internal database id of inserted group assignment, -1 if insertion
	 * failed.
	 */
	public static long insertGroup(String group, KnowledgeElement sourceElement) {
		if (group == null || sourceElement == null) {
			return -1;
		}
		long alreadyExistingId = isGroupAlreadyInDatabase(group, sourceElement);
		if (alreadyExistingId != -1) {
			return alreadyExistingId;
		}
		final DecisionGroupInDatabase groupInDatabase = ACTIVE_OBJECTS.create(DecisionGroupInDatabase.class);
		String documentationLocationOfSourceElement = sourceElement.getDocumentationLocation().getIdentifier();
		groupInDatabase.setSourceDocumentationLocation(documentationLocationOfSourceElement);
		groupInDatabase.setSourceId(sourceElement.getId());
		groupInDatabase.setProjectKey(sourceElement.getProject().getProjectKey());
		groupInDatabase.setGroup(group);
		groupInDatabase.save();

		List<Long> returnedIds = new ArrayList<Long>();

		if (sourceElement.getDocumentationLocation().getIdentifier().equals("c")) {
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

	/**
	 * Returns the group id if the assignment already exists in database, otherwise
	 * -1.
	 *
	 * @param group Name of the Group
	 * @param sourceElement    KnowledgeElement that the group is assigned to
	 * @return group id if the entry already exists in database, otherwise -1.
	 */
	public static long isGroupAlreadyInDatabase(String group, KnowledgeElement sourceElement) {
		long groupId = -1;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (groupInDatabase.getGroup() != null && groupInDatabase.getGroup().equals(group)
					&& groupInDatabase.getSourceId() == sourceElement.getId()
					&& groupInDatabase.getSourceDocumentationLocation().equals(sourceElement.getDocumentationLocation()
					.getIdentifier())) {
				groupId = groupInDatabase.getId();
			}
		}
		return groupId;
	}

	/**
	 * Returns the GroupInDatabase object.
	 *
	 * @param group Name of the Group
	 * @param sourceElement    KnowledgeElement that the group is assigned to
	 * @return GroupInDatabase object.
	 */
	public static DecisionGroupInDatabase getGroupInDatabase(String group, KnowledgeElement sourceElement) {
		if (group == null || sourceElement == null) {
			return null;
		}
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (groupInDatabase.getGroup().equals(group) && groupInDatabase.getSourceId() == sourceElement.getId()
					&& groupInDatabase.getSourceDocumentationLocation().equals(sourceElement.getDocumentationLocation()
				.getIdentifier())) {
				return groupInDatabase;
			}
		}
		return null;
	}

	public static List<String> getAllDecisionGroups(String projectKey) {
		List<String> groups = new ArrayList<>();
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			String elementGroup = groupInDatabase.getGroup();
			if (groupInDatabase.getProjectKey().equals(projectKey) && !groups.contains(elementGroup)
					&& !elementGroup.isBlank()) {
				groups.add(elementGroup);
			}
		}
		return groups;
	}

	public static List<String> getAllDecisionElementsWithCertainGroup(String group, String projectKey) {
		List<String> keys = new ArrayList<>();
		KnowledgePersistenceManager kpManager = new KnowledgePersistenceManager(projectKey);
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (groupInDatabase.getProjectKey().equals(projectKey) && groupInDatabase.getGroup().equals(group)
					&& !groupInDatabase.getSourceDocumentationLocation().equals("c")) {
				KnowledgeElement element = kpManager
						.getManagerForSingleLocation(groupInDatabase.getSourceDocumentationLocation())
						.getKnowledgeElement(groupInDatabase.getSourceId());
				if (element != null) {
					keys.add(element.getKey());
				}
			}
		}
		return keys;
	}

	public static List<String> getAllClassElementsWithCertainGroup(String group, String projectKey) {
		List<String> keys = new ArrayList<>();
		KnowledgePersistenceManager kpManager = new KnowledgePersistenceManager(projectKey);
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (groupInDatabase.getProjectKey().equals(projectKey) && groupInDatabase.getGroup().equals(group)
					&& groupInDatabase.getSourceDocumentationLocation().equals("c")) {
				KnowledgeElement element = kpManager
						.getManagerForSingleLocation(groupInDatabase.getSourceDocumentationLocation())
						.getKnowledgeElement(groupInDatabase.getSourceId());
				if (element != null) {
					keys.add(element.getKey());
				}
			}
		}
		return keys;
	}

	public static boolean updateGroupName(String oldGroup, String newGroup, String projectKey) {
		boolean success = false;
		for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
			if (groupInDatabase.getGroup().equals(oldGroup) && groupInDatabase.getProjectKey().equals(projectKey)) {
				groupInDatabase.setGroup(newGroup);
				groupInDatabase.save();
				success = true;
			}
		}
		return success;
	}
}
