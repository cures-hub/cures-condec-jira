package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Integates all available persistence managers for single documentation
 * locations for a given project.
 *
 * @issue How can we integrate knowledge from different documentation locations?
 * @alternative Use the decorator design pattern to integrate persistence
 *              managers for different documentation locations!
 * @alternative Manually implement methods such as getDecisionKnowledgeElements!
 * @see AbstractPersistenceManagerForSingleLocation
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 */
public class KnowledgePersistenceManager {

	private String projectKey;
	private JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;
	private CodeClassKnowledgeElementPersistenceManager codeClassKnowledgeElementPersistenceManager;
	private List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSingleLocations;

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
	public static Map<String, KnowledgePersistenceManager> instances = new HashMap<String, KnowledgePersistenceManager>();

	/**
	 * Retrieves an existing PersistenceManager instance or creates a new instance
	 * if there is no instance for the given project key.
	 *
	 * @param projectKey
	 *            of the Jira project that this persistence manager is responsible
	 *            for.
	 * @return either a new or already existing PersistenceManager instance.
	 * @see DecisionKnowledgeProject
	 */
	public static KnowledgePersistenceManager getOrCreate(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key must not be null.");
		}
		if (instances.containsKey(projectKey)) {
			return instances.get(projectKey);
		}
		KnowledgePersistenceManager persistenceInterface = new KnowledgePersistenceManager(projectKey);
		instances.put(projectKey, persistenceInterface);
		return instances.get(projectKey);
	}

	public static KnowledgePersistenceManager getOrCreate(DecisionKnowledgeProject project) {
		if (project == null) {
			throw new IllegalArgumentException("The project must not be null.");
		}
		return getOrCreate(project.getProjectKey());
	}

	public KnowledgePersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.jiraIssuePersistenceManager = new JiraIssuePersistenceManager(projectKey);
		this.jiraIssueTextPersistenceManager = new JiraIssueTextPersistenceManager(projectKey);
		this.codeClassKnowledgeElementPersistenceManager = new CodeClassKnowledgeElementPersistenceManager(projectKey);
		this.activePersistenceManagersForSingleLocations = initActivePersistenceManagersForSinleLocations();
	}

	private List<AbstractPersistenceManagerForSingleLocation> initActivePersistenceManagersForSinleLocations() {
		List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSinleLocations = new ArrayList<AbstractPersistenceManagerForSingleLocation>();
		activePersistenceManagersForSinleLocations.add(jiraIssueTextPersistenceManager);
		activePersistenceManagersForSinleLocations.add(jiraIssuePersistenceManager);
		return activePersistenceManagersForSinleLocations;
	}

	/**
	 * @return list of all {@link KnowledgeElement}s for a Jira project.
	 */
	public List<KnowledgeElement> getDecisionKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<KnowledgeElement>();
		activePersistenceManagersForSingleLocations
				.forEach(manager -> elements.addAll(manager.getDecisionKnowledgeElements()));

		// remove irrelevant sentences from graph
		elements.removeIf(
				element -> (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) element).isRelevant()));

		return elements;
	}

	/**
	 * @return key of the Jira project that this persistence manager is responsible
	 *         for as a string.
	 * @see DecisionKnowledgeProject
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @return persistence manager for a single documentation location that uses
	 *         Jira issue comments or the description to store decision knowledge.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	public JiraIssueTextPersistenceManager getJiraIssueTextManager() {
		return jiraIssueTextPersistenceManager;
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} with project and documentation location
	 *            set.
	 * @return persistence manager of a given {@link KnowledgeElement}. Returns the
	 *         default persistence manager in case the documentation location of the
	 *         element cannot be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	public static AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(KnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("The element cannot be null.");
		}
		String projectKey = element.getProject().getProjectKey();
		return getOrCreate(projectKey).getManagerForSingleLocation(element.getDocumentationLocation());
	}

	/**
	 * @return persistence manager for a single documentation location that uses
	 *         entire Jira issues with specific types to store decision knowledge.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	public JiraIssuePersistenceManager getJiraIssueManager() {
		return jiraIssuePersistenceManager;
	}

	/**
	 * @param documentationLocationIdentifier
	 *            String identifier indicating the documentation location of
	 *            knowledge (e.g., "i" for Jira issue).
	 * @return persistence manager for a single documentation location. Returns the
	 *         default persistence manager in case the documentation location cannot
	 *         be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			String documentationLocationIdentifier) {
		if (documentationLocationIdentifier == null) {
			return null;
		}
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getManagerForSingleLocation(documentationLocation);
	}

	/**
	 * @param documentationLocation
	 *            of knowledge.
	 * @return persistence manager for a single documentation location. Returns the
	 *         default persistence manager in case the documentation location cannot
	 *         be found.
	 * @see AbstractPersistenceManagerForSingleLocation
	 */
	public AbstractPersistenceManagerForSingleLocation getManagerForSingleLocation(
			DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return null;
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return jiraIssuePersistenceManager;
		case JIRAISSUETEXT:
			return jiraIssueTextPersistenceManager;
		case COMMIT:
			return codeClassKnowledgeElementPersistenceManager;
		default:
			return null;
		}
	}

	/**
	 * Inserts a new link into database. The link can be between any kinds of nodes
	 * in the {@link KnowledgeGraph}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination
	 *            {@link KnowledgeElement} as a {@link Link} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	public long insertLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			return 0;
		}

		long databaseId = 0;

		if (link.isIssueLink()) {
			databaseId = JiraIssuePersistenceManager.insertLink(link, user);
			if (databaseId > 0) {
				link.setId(databaseId);
				KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
			}
			return databaseId;
		}

		if (ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			KnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}
		databaseId = GenericLinkManager.insertLink(link, user);
		if (databaseId > 0) {
			link.setId(databaseId);
			KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
		}
		return databaseId;
	}

	public boolean updateIssueStatus(KnowledgeElement parentElement, KnowledgeElement childElement,
			ApplicationUser user) {
		if (KnowledgeStatus.isIssueResolved(parentElement, childElement)) {
			parentElement.setStatus(KnowledgeStatus.RESOLVED);
			updateDecisionKnowledgeElement(parentElement, user);
			return true;
		}
		return false;
	}

	/**
	 * Inserts a new link into database.
	 *
	 * @param childElement
	 *            a {@link KnowledgeElement} that is on one end of the link.
	 * @param parentElement
	 *            a {@link KnowledgeElement} that is on one end of the link.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 * @see LinkType
	 */
	public long insertLink(KnowledgeElement parentElement, KnowledgeElement childElement, ApplicationUser user) {
		if (parentElement == null || childElement == null) {
			return 0;
		}
		Link link = Link.instantiateDirectedLink(parentElement, childElement);
		long linkId = insertLink(link, user);
		return linkId;
	}

	/**
	 * Deletes an existing link in database. The link can be between any kinds of
	 * nodes in the {@link KnowledgeGraph}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination
	 *            {@link KnowledgeElement} as a {@link Link} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if deletion was successful.
	 */
	public boolean deleteLink(Link link, ApplicationUser user) {
		if (link.containsUnknownDocumentationLocation()) {
			return false;
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
			KnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}

		return isDeleted;
	}

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
	 * @see KnowledgeElement
	 * @see KnowledgeType
	 * @see DocumentationLocation
	 * @see Link
	 */
	public long updateLink(KnowledgeElement element, KnowledgeType formerKnowledgeType, long idOfParentElement,
			String documentationLocationOfParentElement, ApplicationUser user) {

		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || idOfParentElement == 0) {
			return -1;
		}

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		KnowledgeElement parentElement = new KnowledgeElement();
		parentElement.setId(idOfParentElement);
		parentElement.setDocumentationLocation(documentationLocationOfParentElement);
		parentElement.setProject(projectKey);

		Link formerLink = Link.instantiateDirectedLink(parentElement, element, formerLinkType);
		if (!this.deleteLink(formerLink, user)) {
			return 0;
		}
		KnowledgeGraph.getOrCreate(projectKey).removeEdge(formerLink);

		Link link = Link.instantiateDirectedLink(parentElement, element, linkType);
		KnowledgeGraph.getOrCreate(projectKey).addEdge(link);

		return this.insertLink(link, user);
	}

	/**
	 * Deletes an existing {@link KnowledgeElement} in database.
	 *
	 * @param element
	 *            {@link KnowledgeElement} with id in database and the
	 *            {@link DocumentationLocation} set.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if deleting was successful.
	 */
	public boolean deleteDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeGraph.getOrCreate(projectKey).removeVertex(element);
		return persistenceManager.deleteDecisionKnowledgeElement(element, user);
	}

	/**
	 * Updates an existing {@link KnowledgeElement} in database.
	 *
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if updating was successful.
	 */
	public boolean updateDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		boolean isUpdated = persistenceManager.updateDecisionKnowledgeElement(element, user);
		if (isUpdated) {
			KnowledgeElement updatedElement = persistenceManager.getDecisionKnowledgeElement(element.getId());
			KnowledgeGraph.getOrCreate(projectKey).updateElement(updatedElement);
		}
		return isUpdated;
	}

	/**
	 * Inserts a new {@link KnowledgeElement} into database.
	 *
	 * @param element
	 *            {@link KnowledgeElement} with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param parentElement
	 *            (optional) {@link KnowledgeElement} that is the parent of this
	 *            element. The parent element is necessary for decision knowledge
	 *            stored in Jira issue description and comments.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return {@link KnowledgeElement} that is now filled with an internal database
	 *         id and key. Returns null if insertion failed.
	 */
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user,
			KnowledgeElement parentElement) {
		if (element.getStatus() == KnowledgeStatus.UNDEFINED) {
			element.setStatus(KnowledgeStatus.getDefaultStatus(element.getType()));
		}
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeElement elementWithId = persistenceManager.insertDecisionKnowledgeElement(element, user,
				parentElement);
		KnowledgeGraph.getOrCreate(projectKey).addVertex(elementWithId);
		return elementWithId;
	}

	/**
	 * Inserts a new {@link KnowledgeElement} into database.
	 *
	 * @param element
	 *            {@link KnowledgeElement} with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return {@link KnowledgeElement} that is now filled with an internal database
	 *         id and key. Returns null if insertion failed.
	 */
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return insertDecisionKnowledgeElement(element, user, null);
	}

	/**
	 * @param id
	 *            of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            of the element, see {@link DocumentationLocation}.
	 * @return {@link KnowledgeElement} or null if it is not found.
	 */
	public KnowledgeElement getDecisionKnowledgeElement(long id, DocumentationLocation documentationLocation) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = getManagerForSingleLocation(
				documentationLocation);
		if (persistenceManager == null) {
			return null;
		}
		return persistenceManager.getDecisionKnowledgeElement(id);
	}

	/**
	 * @param id
	 *            id of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            identifier of the {@link DocumentationLocation} of the element,
	 *            e.g., "i" for Jira issue.
	 * @return {@link KnowledgeElement} or null if it is not found.
	 */
	public KnowledgeElement getDecisionKnowledgeElement(long id, String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getDecisionKnowledgeElement(id, documentationLocation);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @return list of {@link Link}s where the given knowledge element is either the
	 *         source or the destination element.
	 */
	public List<Link> getLinks(KnowledgeElement element) {
		List<Link> links = new ArrayList<Link>();
		activePersistenceManagersForSingleLocations.forEach(manager -> links.addAll(manager.getInwardLinks(element)));
		activePersistenceManagersForSingleLocations.forEach(manager -> links.addAll(manager.getOutwardLinks(element)));
		return links;
	}

	/**
	 * @param link
	 *            {@link Link} object.
	 * @return database id of a link object (either a Jira {@link IssueLink} or a
	 *         generic link). Returns a value <= 0 if the link is not existing in
	 *         one of these databases.
	 * @see GenericLinkManager
	 */
	public static long getLinkId(Link link) {
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

}