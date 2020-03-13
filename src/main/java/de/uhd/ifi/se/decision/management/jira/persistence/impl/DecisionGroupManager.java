package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionGroupInDatabase;
import net.java.ao.Query;

/**
 * Class responsible for groups of decision from all Jira based documentation
 * locations. Groups are stored in the internal database of Jira.
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
	    if (dgData.getGroup() == group && dgData.getSourceId() == element.getId()) {
		isDeleted = DecisionGroupInDatabase.deleteGroup(dgData);
	    }
	}
	return isDeleted;
    }

    /**
     * Replaces all current existing assignments for that element with a list of new
     * groups
     * 
     * @param List of groups to add
     * @param The  element that the groups should be assigned to
     * @return If replacement was successful
     */
    public static boolean setGroupAssignment(List<String> groups, KnowledgeElement element) {
	List<String> oldGroups = getGroupsForElement(element);
	for (String oldGroup : oldGroups) {
	    deleteGroupAssignment(oldGroup, element);
	}
	for (String group : groups) {
	    insertGroup(group, element);
	}
	return false;

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
	    KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey).getDecisionKnowledgeElement(
		    databaseEntry.getSourceId(), databaseEntry.getSourceDocumentationLocation());
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
     * 
     * @see KnowledgeElement
     */
    public static List<String> getGroupsForElement(KnowledgeElement element) {
	if (element == null) {
	    return new ArrayList<String>();
	}
	return getGroupsForElement(element.getId(), element.getDocumentationLocation());
    }

    /**
     * Returns all groups for a given KnowledgeElement.
     * 
     * @param element       node id in the {@link KnowledgeGraph}.
     * @param documentation location of the KnowledgeElement
     * @return list of Strings of group assignments
     * 
     * @see KnowledgeElement
     */
    public static List<String> getGroupsForElement(long elementId, DocumentationLocation documentationLocation) {
	List<String> groups = new ArrayList<String>();
	if (elementId <= 0 || documentationLocation == null) {
	    return new ArrayList<String>();
	}
	String identifier = documentationLocation.getIdentifier();
	DecisionGroupInDatabase[] groupsInDatabase = ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class,
		Query.select().where("SOURCE_ID = ? AND SOURCE_DOCUMENTATION_LOCATION = ?", elementId, identifier));
	for (DecisionGroupInDatabase groupInDatabase : groupsInDatabase) {
	    String group = groupInDatabase.getGroup();
	    groups.add(group);
	}
	return groups;
    }

    /**
     * Inserts a new group assignment into database.
     *
     * @param String Name of the Group
     * @param The    KnowledgeElement that the group is assigned to
     * @return internal database id of inserted group assignment, -1 if insertion
     *         failed.
     */
    public static long insertGroup(String group, KnowledgeElement sourceElement) {
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
	return groupInDatabase.getId();
    }

    /**
     * Returns the group id if the assignment already exists in database, otherwise
     * -1.
     * 
     * @param String Name of the Group
     * @param The    KnowledgeElement that the group is assigned to
     * @return group id if the entry already exists in database, otherwise -1.
     */
    public static long isGroupAlreadyInDatabase(String group, KnowledgeElement sourceElement) {
	long groupId = -1;
	for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
	    if (groupInDatabase.getGroup() != null && groupInDatabase.getGroup() == group
		    && groupInDatabase.getSourceId() == sourceElement.getId()) {
		groupId = groupInDatabase.getId();
	    }
	}
	return groupId;
    }

    /**
     * Returns the GroupInDatabase object.
     * 
     * @param String Name of the Group
     * @param The    KnowledgeElement that the group is assigned to
     * @return GroupInDatabase object.
     */
    public static DecisionGroupInDatabase getGroupInDatabase(String group, KnowledgeElement sourceElement) {
	for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
	    if (groupInDatabase.getGroup() == group && groupInDatabase.getSourceId() == sourceElement.getId()) {
		return groupInDatabase;
	    }
	}
	return null;
    }

    public static List<String> getAllDecisionGroups() {
	List<String> groups = new ArrayList<String>();
	for (DecisionGroupInDatabase groupInDatabase : ACTIVE_OBJECTS.find(DecisionGroupInDatabase.class)) {
	    String elementGroup = groupInDatabase.getGroup();
	    if (!groups.contains(elementGroup) && !"".equals(elementGroup) && !" ".equals(elementGroup)) {
		groups.add(elementGroup);
	    }
	}
	return groups;
    }
}
