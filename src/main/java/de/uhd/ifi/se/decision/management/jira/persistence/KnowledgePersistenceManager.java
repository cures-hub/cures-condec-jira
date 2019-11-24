package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.KnowledgePersistenceManagerImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.StatusPersistenceManager;

/**
 * Interface that integrates all available persistence managers for single
 * documentation locations for a given project. Responsible to create, edit,
 * delete and retrieve decision knowledge elements and their links.
 * 
 * @see AbstractPersistenceManagerForSingleLocation
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see ActiveObjectPersistenceManager
 */
public interface KnowledgePersistenceManager {

	/**
	 * Map of persistence manager instances that are identified by the project key.
	 * Use the {@link KnowledgePersistenceManager#getOrCreate()} method to either
	 * create or retrieve an existing object
	 * 
	 * @issue How can we reuse existing objects instead of recreating them all the
	 *        time?
	 * @decision Use a map of project keys and respective objects to reuse existing
	 *           objects instead of recreating them all the time! Use the
	 *           getOrCreate() method to either create or retrieve an existing
	 *           object!
	 */
	static Map<String, KnowledgePersistenceManager> instances = new HashMap<String, KnowledgePersistenceManager>();

	/**
	 * Retrieves an existing PersistenceManager instance or creates a new instance
	 * if there is no instance for the given project key.
	 * 
	 * @param projectKey
	 *            of the Jira project that this persistence manager is responsible
	 *            for.
	 * @return either a new or already existing PersistenceManager instance.
	 * 
	 * @see DecisionKnowledgeProject
	 */
	static KnowledgePersistenceManager getOrCreate(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key must not be null.");
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		KnowledgePersistenceManager persistenceInterface = new KnowledgePersistenceManagerImpl(projectKey);
		instances.put(projectKey, persistenceInterface);
		return instances.get(projectKey);
	}

	static KnowledgePersistenceManager getOrCreate(DecisionKnowledgeProject project) {
		if (project == null) {
			throw new IllegalArgumentException("The project must not be null.");
		}
		return getOrCreate(project.getProjectKey());
	}

	/**
	 * Returns the key of the project that this persistence manager is responsible
	 * for.
	 * 
	 * @return key of the Jira project as a string.
	 * @see DecisionKnowledgeProject
	 */
	String getProjectKey();

	/**
	 * Returns all decision knowledge elements for a project.
	 *
	 * @return list of all decision knowledge elements for a project.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 */
	List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	/**
	 * Returns all decision knowledge elements for a project with a certain
	 * knowledge type.
	 *
	 * @return list of all decision knowledge elements for a project with a certain
	 *         knowledge type.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @see KnowledgeType
	 */
	List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type);

	/**
	 * Deletes an existing decision knowledge element in database.
	 *
	 * @param element
	 *            decision knowledge element with id in database and the
	 *            {@link DocumentationLocation} set.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if deleting was successful.
	 * @see DecisionKnowledgeElement
	 */
	boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	/**
	 * Update an existing decision knowledge element in database.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if updating was successful.
	 * @see DecisionKnowledgeElement
	 */
	boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	/**
	 * Inserts a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see DecisionKnowledgeElement
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user);

	/**
	 * Inserts a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param parentElement
	 *            (optional) decision knowledge element that is the parent of this
	 *            element. The parent element is necessary for decision knowledge
	 *            stored in JIRA issue description and comments.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see DecisionKnowledgeElement
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement);

	/**
	 * Inserts a new link into database. The link can be between any kinds of nodes
	 * in the {@link KnowledgeGraph}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	long insertLink(Link link, ApplicationUser user);

	/**
	 * Inserts a new link into database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see LinkType
	 * @param childElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param parentElement
	 *            a decision knowledge element that is on one end of the link.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	long insertLink(DecisionKnowledgeElement parentElement, DecisionKnowledgeElement childElement,
			ApplicationUser user);

	/**
	 * Updates an existing link in database. The link can be between any kinds of
	 * nodes in the {@link KnowledgeGraph}.
	 *
	 * @param element
	 *            child element of the link with the new knowledge type.
	 * @param formerKnowledgeType
	 *            former knowledge type of the child element before it was updated.
	 * @param idOfParentElement
	 *            id of the parent element.
	 * @param documentationLocationOfParentElement
	 *            {@link DocumentationLocation} of the parent element.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of updated link, zero if updating failed.
	 * 
	 * @see DecisionKnowledgeElement
	 * @see KnowledgeType
	 * @see DocumentationLocation
	 * @see Link
	 */
	long updateLink(DecisionKnowledgeElement element, KnowledgeType formerKnowledgeType, long idOfParentElement,
			String documentationLocationOfParentElement, ApplicationUser user);

	/**
	 * Deletes an existing link in database. The link can be between any kinds of
	 * nodes in the {@link KnowledgeGraph}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if deletion was successful.
	 */
	boolean deleteLink(Link link, ApplicationUser user);

	/**
	 * Returns the database id of a link object (either a Jira issue link or a
	 * generic link). Returns a value <= 0 if the link is not existing in one of
	 * these databases.
	 * 
	 * @param link
	 *            {@link Link} object.
	 * @return database id of a link object (either a Jira issue link or a generic
	 *         link). Returns a value <= 0 if the link is not existing in one of
	 *         these databases.
	 * @see GenericLinkManager
	 * @see IssueLink
	 */
	static long getLinkId(Link link) {
		long linkId = -1;
		if (link.isIssueLink()) {
			linkId = JiraIssuePersistenceManager.getLinkId(link);
			return linkId;
		}
		linkId = GenericLinkManager.isLinkAlreadyInDatabase(link);
		if (linkId <= 0) {
			GenericLinkManager.isLinkAlreadyInDatabase(link.flip());
		}
		return linkId;
	}

	/**
	 * Returns the persistence manager for a single documentation location that uses
	 * Jira issue comments or the description to store decision knowledge.
	 * 
	 * @return persistence manager that uses Jira issue comments or the description
	 *         to store decision knowledge.
	 * 
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	JiraIssueTextPersistenceManager getJiraIssueTextManager();

	/**
	 * Returns the persistence manager for a single documentation location that uses
	 * entire Jira issues with specific types to store decision knowledge.
	 * 
	 * @return persistence manager that uses entire Jira issues with specific types
	 *         to store decision knowledge.
	 * 
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	JiraIssuePersistenceManager getJiraIssueManager();

	/**
	 * Returns the persistence manager for a single documentation location that uses
	 * object relational mapping with the help of the active object framework to
	 * store decision knowledge.
	 * 
	 * @return persistence manager that uses entire Jira issues with specific types
	 *         to store decision knowledge.
	 * 
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	ActiveObjectPersistenceManager getActiveObjectManager();

	/**
	 * Returns the default persistence manager for autarkical decision knowledge
	 * elements used in the project. These elements are directly stored in Jira and
	 * independent from other Jira issues. These elements are real "first-class"
	 * elements.
	 *
	 * @return persistence manager for real "first-class" decision knowledge
	 *         elements used in the given project, either Jira issue manager or
	 *         active object manager. The active object manager is the default
	 *         manager if the user did not decide differently.
	 * @see AbstractPersistenceManagerForSingleLocation
	 * @see JiraIssuePersistenceManager
	 * @see ActiveObjectPersistenceManager
	 */
	AbstractPersistenceManagerForSingleLocation getDefaultManagerForSingleLocation();

	/**
	 * Returns the persistence manager for a single documentation location.
	 *
	 * @param documentationLocation
	 *            of knowledge.
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(DocumentationLocation documentationLocation);

	/**
	 * Gets the persistence manager for a single documentation location by the
	 * identifier of the location.
	 *
	 * @param documentationLocationIdentifier
	 *            String identifier indicating the documentation location of
	 *            knowledge (e.g., i for Jira issue).
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(String documentationLocationIdentifier);

	/**
	 * Get the persistence manager of a given decision knowledge elements.
	 *
	 * @param element
	 *            decision knowledge element with project and documentation
	 *            location.
	 * @return persistence manager of a given decision knowledge elements. Returns
	 *         the default persistence manager in case the documentation location of
	 *         the element cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	static AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			DecisionKnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("The element cannot be null.");
		}
		String projectKey = element.getProject().getProjectKey();
		return getOrCreate(projectKey).getManagerForSingleLocation(element.getDocumentationLocation());
	}

	/**
	 * Gets a decision knowledge element in database by its id and its documentation
	 * location.
	 *
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @param documentationLocation
	 *            of the element.
	 * @return decision knowledge element.
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 */
	DecisionKnowledgeElement getDecisionKnowledgeElement(long id, DocumentationLocation documentationLocation);

	static void insertStatus(DecisionKnowledgeElement element) {
		if (element.getType() == KnowledgeType.DECISION) {
			StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.DECIDED);
		}
		if (element.getType() == KnowledgeType.ALTERNATIVE) {
			StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.IDEA);
		}
	}
}
