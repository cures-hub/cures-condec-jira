package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.PersistenceManagerImpl;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Interface that integates all available persistence managers for single
 * documentation locations for a given project.
 * 
 * @see AbstractPersistenceManagerForSingleLocation
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see ActiveObjectPersistenceManager
 */
public interface PersistenceManager {

	/**
	 * Map of persistence manager instances that are identified by the project key.
	 * Use the {@link PersistenceManager#getOrCreate()} method to either create or
	 * retrieve an existing object
	 * 
	 * @issue How can we reuse existing objects instead of recreating them all the
	 *        time?
	 * @decision Use a map of project keys and respective objects to reuse existing
	 *           objects instead of recreating them all the time! Use the
	 *           getOrCreate() method to either create or retrieve an existing
	 *           object!
	 */
	static Map<String, PersistenceManager> instances = new HashMap<String, PersistenceManager>();

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
	static PersistenceManager getOrCreate(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		PersistenceManager persistenceInterface = new PersistenceManagerImpl(projectKey);
		instances.put(projectKey, persistenceInterface);
		return instances.get(projectKey);
	}

	/**
	 * Returns the key of the project that this persistence manager is responsible
	 * for.
	 * 
	 * @return key of the Jira project as a string.
	 */
	String getProjectKey();

	/**
	 * Update link in database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see KnowledgeType
	 * @see DocumentationLocation
	 * @see Link
	 * @see ApplicationUser
	 * @param element
	 *            child element of the link with the new knowledge type.
	 * @param formerKnowledgeType
	 *            former knowledge type of the child element before it was updated.
	 * @param idOfParentElement
	 *            id of the parent element.
	 * @param documentationLocationOfParentElement
	 *            documentation location of the parent element.
	 * @param user
	 *            authenticated JIRA application user
	 * @return internal database id of updated link, zero if updating failed.
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
		return insertLink(link, user);
	}

	/**
	 * Insert a new link into database.
	 *
	 * @param link
	 *            link between a source and a destination decision knowledge
	 *            element.
	 * @param user
	 *            authenticated JIRA application user
	 * @return internal database id of inserted link, zero if insertion failed.
	 * @see Link
	 * @see ApplicationUser
	 */
	static long insertLink(Link link, ApplicationUser user) {
		String projectKey = link.getSourceElement().getProject().getProjectKey();

		if (link.containsUnknownDocumentationLocation()) {
			link.setDefaultDocumentationLocation(projectKey);
		}

		long databaseId = 0;

		if (link.isIssueLink()) {
			databaseId = JiraIssuePersistenceManager.insertLink(link, user);
			if (databaseId > 0) {
				KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
			}
			return databaseId;
		}

		if (ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			DecisionKnowledgeElement sourceElement = link.getSourceElement();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}
		databaseId = GenericLinkManager.insertLink(link, user);
		if (databaseId > 0) {
			KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
		}
		return databaseId;
	}

	/**
	 * Delete an existing link in database.
	 *
	 * @param link
	 *            link between a source and a destination decision knowledge
	 *            element.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if insertion was successful.
	 * @see Link
	 * @see ApplicationUser
	 */
	static boolean deleteLink(Link link, ApplicationUser user) {
		String projectKey = link.getSourceElement().getProject().getProjectKey();

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
			DecisionKnowledgeElement sourceElement = link.getSourceElement();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}
		return isDeleted;
	}

	/**
	 * Returns the default persistence manager for autarkical decision knowledge
	 * elements used in the project. These elements are directly stored in Jira and
	 * independent from other Jira issues. These elements are real "first-class"
	 * elements.
	 *
	 * @param projectKey
	 *            of the Jira project.
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
	 * Returns the persistence manager for a given project and documentation
	 * location.
	 *
	 * @param projectKey
	 *            of a JIRA project.
	 * @param documentationLocation
	 *            of knowledge.
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	AbstractPersistenceManagerForSingleLocation getPersistenceManager(DocumentationLocation documentationLocation);

	JiraIssueTextPersistenceManager getJiraIssueTextPersistenceManager();

	JiraIssuePersistenceManager getJiraIssuePersistenceManager();

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
	 * Get the persistence manager for a given project and documentation location.
	 *
	 * @param projectKey
	 *            of a JIRA project.
	 * @param documentationLocationIdentifier
	 *            String identifier indicating the documentation location of
	 *            knowledge (e.g., i for JIRA issue).
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	static AbstractPersistenceManagerForSingleLocation getPersistenceManager(String projectKey,
			String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getOrCreate(projectKey).getPersistenceManager(documentationLocation);
	}

	static void insertStatus(DecisionKnowledgeElement element) {
		if (element.getType().equals(KnowledgeType.DECISION)) {
			DecisionStatusManager.setStatusForElement(element, KnowledgeStatus.DECIDED);
		}
		if (element.getType().equals(KnowledgeType.ALTERNATIVE)) {
			DecisionStatusManager.setStatusForElement(element, KnowledgeStatus.IDEA);
		}
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

	ActiveObjectPersistenceManager getActiveObjectPersistenceManager();

	List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type);
}
