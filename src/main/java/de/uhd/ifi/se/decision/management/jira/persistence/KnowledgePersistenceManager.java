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
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.KnowledgePersistenceManagerImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.StatusPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

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
			throw new IllegalArgumentException("The project key cannot be null.");
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		KnowledgePersistenceManager persistenceInterface = new KnowledgePersistenceManagerImpl(projectKey);
		instances.put(projectKey, persistenceInterface);
		return instances.get(projectKey);
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
	 * Inserts a new link into database. The link can be between any kinds of nodes
	 * in the {@link KnowledgeGraph}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object.
	 * @param user
	 *            authenticated JIRA {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	long insertLink(Link link, ApplicationUser user);

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
	static long updateLink(DecisionKnowledgeElement element, KnowledgeType formerKnowledgeType, long idOfParentElement,
			String documentationLocationOfParentElement, ApplicationUser user) {

		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || idOfParentElement == 0) {
			return -1;
		}

		String projectKey = element.getProject().getProjectKey();

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl();
		parentElement.setId(idOfParentElement);
		parentElement.setDocumentationLocation(documentationLocationOfParentElement);
		parentElement.setProject(projectKey);

		Link formerLink = Link.instantiateDirectedLink(parentElement, element, formerLinkType);
		if (!deleteLink(formerLink, user)) {
			return 0;
		}
		KnowledgeGraph.getOrCreate(projectKey).removeEdge(formerLink);

		Link link = Link.instantiateDirectedLink(parentElement, element, linkType);
		KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
		return KnowledgePersistenceManager.getOrCreate(projectKey).insertLink(link, user);
	}

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
	static boolean deleteLink(Link link, ApplicationUser user) {
		String projectKey = link.getSource().getProject().getProjectKey();

		if (link.containsUnknownDocumentationLocation()) {
			link.setDefaultDocumentationLocation(projectKey);
		}

		KnowledgeGraph.getOrCreate(projectKey).removeEdge(link);

		boolean isDeleted = false;
		if (link.isIssueLink()) {
			isDeleted = JiraIssuePersistenceManager.deleteLink(link, user);
			if (!isDeleted) {
				isDeleted = JiraIssuePersistenceManager.deleteLink(link.flip(), user);
			}
			return isDeleted;
		}
		isDeleted = GenericLinkManager.deleteLink(link);
		if (!isDeleted) {
			isDeleted = GenericLinkManager.deleteLink(link.flip());
		}

		if (isDeleted && ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			DecisionKnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}

		return isDeleted;
	}

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
			if (linkId <= 0) {
				JiraIssuePersistenceManager.getLinkId(link.flip());
			}
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
	AbstractPersistenceManagerForSingleLocation getDefaultPersistenceManager();

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
	AbstractPersistenceManagerForSingleLocation getPersistenceManager(DocumentationLocation documentationLocation);

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
	AbstractPersistenceManagerForSingleLocation getPersistenceManager(String documentationLocationIdentifier);

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
	static AbstractPersistenceManagerForSingleLocation getPersistenceManager(DecisionKnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("The element cannot be null.");
		}
		String projectKey = element.getProject().getProjectKey();
		return getOrCreate(projectKey).getPersistenceManager(element.getDocumentationLocation());
	}

	/**
	 * Get a decision knowledge element in database by its id and its documentation
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
	static DecisionKnowledgeElement getDecisionKnowledgeElement(long id, DocumentationLocation documentationLocation) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = getOrCreate("")
				.getPersistenceManager(documentationLocation);
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(id);
		if (element == null) {
			return new DecisionKnowledgeElementImpl();
		}
		return element;
	}

	static void insertStatus(DecisionKnowledgeElement element) {
		if (element.getType().equals(KnowledgeType.DECISION)) {
			StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.DECIDED);
		}
		if (element.getType().equals(KnowledgeType.ALTERNATIVE)) {
			StatusPersistenceManager.setStatusForElement(element, KnowledgeStatus.IDEA);
		}
	}

	static void updateGraphNode(DecisionKnowledgeElement element) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		graph.updateNode(element);
	}

	static void removeGraphNode(DecisionKnowledgeElement element) {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		graph.removeVertex(element);
	}
}
