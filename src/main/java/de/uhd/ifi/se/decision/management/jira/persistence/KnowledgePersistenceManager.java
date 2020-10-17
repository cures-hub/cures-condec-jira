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
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.IssueCompletenessCheck;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Integrates all available persistence managers for single documentation
 * locations for a given project.
 *
 * @issue How can we integrate knowledge from different documentation locations?
 * @alternative Use the decorator design pattern to integrate persistence
 *              managers for different documentation locations!
 * @alternative Manually implement methods such as getKnowledgeElements!
 * 
 * @see AbstractPersistenceManagerForSingleLocation
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see CodeClassPersistenceManager
 */
public class KnowledgePersistenceManager {

	private final String projectKey;
	private final JiraIssuePersistenceManager jiraIssuePersistenceManager;
	private final JiraIssueTextPersistenceManager jiraIssueTextPersistenceManager;
	private final CodeClassPersistenceManager codeClassPersistenceManager;
	private final List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSingleLocations;

	/**
	 * Map of persistence manager instances that are identified by the project key.
	 * Use the {@link #getOrCreate(String) getOrCreate} method to either create or
	 * retrieve an existing object
	 *
	 * @issue How can we reuse existing objects instead of recreating them all the
	 *        time?
	 * @decision Use the multiton design pattern, i.e. a map of project keys and
	 *           respective objects to reuse existing objects instead of recreating
	 *           them all the time! Use the getOrCreate() method to either create or
	 *           retrieve an existing object!
	 */
	public static Map<String, KnowledgePersistenceManager> instances = new HashMap<>();

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
		this.codeClassPersistenceManager = new CodeClassPersistenceManager(projectKey);
		this.activePersistenceManagersForSingleLocations = initActivePersistenceManagersForSingleLocations();
	}

	private List<AbstractPersistenceManagerForSingleLocation> initActivePersistenceManagersForSingleLocations() {
		List<AbstractPersistenceManagerForSingleLocation> activePersistenceManagersForSinleLocations = new ArrayList<>();
		activePersistenceManagersForSinleLocations.add(jiraIssueTextPersistenceManager);
		activePersistenceManagersForSinleLocations.add(jiraIssuePersistenceManager);
		activePersistenceManagersForSinleLocations.add(codeClassPersistenceManager);
		return activePersistenceManagersForSinleLocations;
	}

	/**
	 * @return list of all {@link KnowledgeElement}s for a Jira project.
	 */
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<>();
		activePersistenceManagersForSingleLocations.forEach(manager -> elements.addAll(manager.getKnowledgeElements()));

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
			return codeClassPersistenceManager;
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

		long databaseId;

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
			new WebhookConnector(projectKey).sendElement(sourceElement, "changed");
		}
		databaseId = GenericLinkManager.insertLink(link, user);
		if (databaseId > 0) {
			link.setId(databaseId);
			KnowledgeGraph.getOrCreate(projectKey).addEdge(link);
		}
		updateIssueStatus(link, user);
		return databaseId;
	}

	/**
	 * If the source or target of the link is a decision problem (=issue), its
	 * status is updated either to resolved or unresolved.
	 * 
	 * @param link
	 *            link (=edge) between a source and a destination
	 *            {@link KnowledgeElement} as a {@link Link} object.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if the status of at least one side of the link was updated.
	 */
	public boolean updateIssueStatus(Link link, ApplicationUser user) {
		return updateIssueStatus(link.getSource(), user) || updateIssueStatus(link.getTarget(), user);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} object. If it is a decision problem
	 *            (=issue), its status is updated either to resolved or unresolved.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if the status of the decision problem (=issue) was updated.
	 */
	public boolean updateIssueStatus(KnowledgeElement element, ApplicationUser user) {
		boolean isIssueResolved = false;
		boolean isUpdateNeeded = false;
		if (element.getType().getSuperType() == KnowledgeType.PROBLEM) {
			if (IssueCompletenessCheck.isDecisionProblemResolved(element)) {
				isIssueResolved = true;
			}

			if (isIssueResolved && element.getStatus() == KnowledgeStatus.UNRESOLVED) {
				element.setStatus(KnowledgeStatus.RESOLVED);
				return updateKnowledgeElement(element, user);
			}

			if (!isIssueResolved && element.getStatus() == KnowledgeStatus.RESOLVED) {
				element.setStatus(KnowledgeStatus.UNRESOLVED);
				return updateKnowledgeElement(element, user);
			}
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
		return insertLink(link, user);
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

		boolean isDeleted;
		if (link.isIssueLink()) {
			isDeleted = JiraIssuePersistenceManager.deleteLink(link, user);
			if (!isDeleted) {
				isDeleted = JiraIssuePersistenceManager.deleteLink(link.flip(), user);
			}
			updateIssueStatus(link, user);
			return isDeleted;
		}
		isDeleted = GenericLinkManager.deleteLink(link);
		if (!isDeleted) {
			isDeleted = GenericLinkManager.deleteLink(link.flip());
		}

		if (isDeleted && ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			KnowledgeElement sourceElement = link.getSource();
			new WebhookConnector(projectKey).sendElement(sourceElement, "changed");
		}
		updateIssueStatus(link, user);
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
	 * @param parentElement
	 *            parent element of the updated element.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of updated link, zero if updating failed.
	 * @see KnowledgeElement
	 * @see KnowledgeType
	 * @see DocumentationLocation
	 * @see Link
	 */
	public long updateLink(KnowledgeElement element, KnowledgeType formerKnowledgeType, KnowledgeElement parentElement,
			ApplicationUser user) {
		updateIssueStatus(element, user);
		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || parentElement == null) {
			return -1;
		}
		updateIssueStatus(parentElement, user);

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		Link formerLink = Link.instantiateDirectedLink(parentElement, element, formerLinkType);
		if (!deleteLink(formerLink, user)) {
			return 0;
		}
		KnowledgeGraph.getOrCreate(projectKey).removeEdge(formerLink);

		Link link = Link.instantiateDirectedLink(parentElement, element, linkType);

		return insertLink(link, user);
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
	public boolean deleteKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		KnowledgeGraph.getOrCreate(projectKey).removeVertex(element);
		return persistenceManager.deleteKnowledgeElement(element, user);
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
	public boolean updateKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		boolean isUpdated = persistenceManager.updateKnowledgeElement(element, user);
		if (isUpdated) {
			KnowledgeElement updatedElement = persistenceManager.getKnowledgeElement(element.getId());
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
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user,
			KnowledgeElement parentElement) {
		if (element.getStatus() == KnowledgeStatus.UNDEFINED) {
			element.setStatus(KnowledgeStatus.getDefaultStatus(element.getType()));
		}
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getManagerForSingleLocation(element);
		if (persistenceManager == null) {
			return null;
		}
		KnowledgeElement elementWithId = persistenceManager.insertKnowledgeElement(element, user, parentElement);
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
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return insertKnowledgeElement(element, user, null);
	}

	/**
	 * @param id
	 *            of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            of the element, see {@link DocumentationLocation}.
	 * @return {@link KnowledgeElement} or null if it is not found.
	 */
	public KnowledgeElement getKnowledgeElement(long id, DocumentationLocation documentationLocation) {
		AbstractPersistenceManagerForSingleLocation persistenceManager = getManagerForSingleLocation(
				documentationLocation);
		if (persistenceManager == null) {
			return null;
		}
		return persistenceManager.getKnowledgeElement(id);
	}

	/**
	 * @param id
	 *            id of the {@link KnowledgeElement} in database.
	 * @param documentationLocationIdentifier
	 *            identifier of the {@link DocumentationLocation} of the element,
	 *            e.g., "i" for Jira issue.
	 * @return {@link KnowledgeElement} or null if it is not found.
	 */
	public KnowledgeElement getKnowledgeElement(long id, String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getKnowledgeElement(id, documentationLocation);
	}

	/**
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @return list of {@link Link}s where the given knowledge element is either the
	 *         source or the destination element.
	 */
	public List<Link> getLinks(KnowledgeElement element) {
		List<Link> links = new ArrayList<>();
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
		long linkId;
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
