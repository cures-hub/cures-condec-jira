package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * Abstract class to create, edit, delete and retrieve decision knowledge
 * elements and their links. Concrete persistence strategies for first class
 * elements are either the JIRA issue strategy or the active object strategy.
 * For example, other methods are to persist knowledge in JIRA issue
 * comments, the description, and commit messages.
 *
 * @see JiraIssuePersistenceManager
 * @see ActiveObjectPersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see PersistenceProvider
 */
public abstract class AbstractPersistenceManager {

	protected String projectKey;
	protected DocumentationLocation documentationLocation;

	/**
	 * Delete an existing link in database.
	 *
	 * @see Link
	 * @see ApplicationUser
	 * @param link
	 *            link between a source and a destination decision knowledge
	 *            element.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if insertion was successful.
	 */
	public static boolean deleteLink(Link link, ApplicationUser user) {
		String projectKey = link.getSourceElement().getProject().getProjectKey();
		if (link.containsUnknownDocumentationLocation()) {
			setDefaultDocumentationLocation(link, projectKey);
		}

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

	private static void setDefaultDocumentationLocation(Link link, String projectKey) {
		AbstractPersistenceManager defaultPersistenceManager = getDefaultPersistenceStrategy(projectKey);
		String defaultDocumentationLocation = DocumentationLocation
				.getIdentifier(defaultPersistenceManager.getDocumentationLocation());
		if (link.getDestinationElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			link.setDocumentationLocationOfDestinationElement(defaultDocumentationLocation);
		}
		if (link.getSourceElement().getDocumentationLocation() == DocumentationLocation.UNKNOWN) {
			link.setDocumentationLocationOfSourceElement(defaultDocumentationLocation);
		}
	}

	/**
	 * Get the persistence strategy for autarkical decision knowledge elements used
	 * in a project. This elements are directly stored in JIRA and independent from
	 * other JIRA issues. These elements are "first class" elements.
	 *
	 * @see AbstractPersistenceManager
	 * @see JiraIssuePersistenceManager
	 * @see ActiveObjectPersistenceManager
	 * @param projectKey
	 *            of the JIRA project.
	 * @return persistence strategy for "first class" decision knowledge elements
	 *         used in the given project, either issue strategy or active object
	 *         strategy. The active object strategy is the default strategy.
	 */
	public static AbstractPersistenceManager getDefaultPersistenceStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistenceManager.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new JiraIssuePersistenceManager(projectKey);
		}
		return new ActiveObjectPersistenceManager(projectKey);
	}

	/**
	 * Get the persistence manager of a given decision knowledge elements.
	 *
	 * @see AbstractPersistenceManager
	 * @param element
	 *            decision knowledge element with project and documentation
	 *            location.
	 * @return persistence manager of a given decision knowledge elements. Returns
	 *         the default persistence manager in case the documentation location of
	 *         the element cannot be found.
	 */
	public static AbstractPersistenceManager getPersistenceManager(DecisionKnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("The element cannot be null.");
		}
		String projectKey = element.getProject().getProjectKey();
		return getPersistenceManager(projectKey, element.getDocumentationLocation());
	}

	/**
	 * Get the persistence manager for a given project and documentation location.
	 *
	 * @see AbstractPersistenceManager
	 * @param projectKey
	 *            of a JIRA project.
	 * @param documentationLocation
	 *            of knowledge.
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 */
	public static AbstractPersistenceManager getPersistenceManager(String projectKey,
			DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return getDefaultPersistenceStrategy(projectKey);
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return new JiraIssuePersistenceManager(projectKey);
		case JIRAISSUETEXT:
			return new JiraIssueTextPersistenceManager(projectKey);
		case ACTIVEOBJECT:
			return new ActiveObjectPersistenceManager(projectKey);
		default:
			return getDefaultPersistenceStrategy(projectKey);
		}
	}

	/**
	 * Get the persistence manager for a given project and documentation location.
	 *
	 * @see AbstractPersistenceManager
	 * @param projectKey
	 *            of a JIRA project.
	 * @param documentationLocationIdentifier
	 *            String identifier indicating the documentation location of
	 *            knowledge (e.g., i for JIRA issue).
	 * @return persistence manager associated to a documentation location. Returns
	 *         the default persistence manager in case the documentation location
	 *         cannot be found.
	 */
	public static AbstractPersistenceManager getPersistenceManager(String projectKey,
			String documentationLocationIdentifier) {
		DocumentationLocation documentationLocation = DocumentationLocation
				.getDocumentationLocationFromIdentifier(documentationLocationIdentifier);
		return getPersistenceManager(projectKey, documentationLocation);
	}

	/**
	 * Insert a new link into database.
	 *
	 * @see Link
	 * @see ApplicationUser
	 * @param link
	 *            link between a source and a destination decision knowledge
	 *            element.
	 * @param user
	 *            authenticated JIRA application user
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	public static long insertLink(Link link, ApplicationUser user) {
		String projectKey = link.getSourceElement().getProject().getProjectKey();
		if (link.containsUnknownDocumentationLocation()) {
			setDefaultDocumentationLocation(link, projectKey);
		}

		if (link.isIssueLink()) {
			return JiraIssuePersistenceManager.insertLink(link, user);
		}
		if (ConfigPersistenceManager.isWebhookEnabled(projectKey)) {
			DecisionKnowledgeElement sourceElement = link.getSourceElement();
			new WebhookConnector(projectKey).sendElementChanges(sourceElement);
		}
		return GenericLinkManager.insertLink(link, user);
	}

	/**
	 * Update new link in database.
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
	public static long updateLink(DecisionKnowledgeElement element, KnowledgeType formerKnowledgeType,
			long idOfParentElement, String documentationLocationOfParentElement, ApplicationUser user) {

		if (LinkType.linkTypesAreEqual(formerKnowledgeType, element.getType()) || idOfParentElement == 0) {
			return -1;
		}

		LinkType formerLinkType = LinkType.getLinkTypeForKnowledgeType(formerKnowledgeType);
		LinkType linkType = LinkType.getLinkTypeForKnowledgeType(element.getType());

		DecisionKnowledgeElement parentElement = new DecisionKnowledgeElementImpl();
		parentElement.setId(idOfParentElement);
		parentElement.setDocumentationLocation(documentationLocationOfParentElement);
		parentElement.setProject(element.getProject().getProjectKey());

		Link formerLink = Link.instantiateDirectedLink(parentElement, element, formerLinkType);
		if (!deleteLink(formerLink, user)) {
			return 0;
		}

		Link link = Link.instantiateDirectedLink(parentElement, element, linkType);
		return insertLink(link, user);
	}

	/**
	 * Delete an existing decision knowledge element in database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if deleting was successful.
	 */
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		return this.deleteDecisionKnowledgeElement(element.getId(), user);
	}

	/**
	 * Delete an existing decision knowledge element in database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if deleting was successful.
	 */
	public abstract boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user);

	/**
	 * Get a decision knowledge element in database by its id.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @return decision knowledge element.
	 */
	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(long id);

	/**
	 * Get a decision knowledge element in database by its id and its documentation
	 * location.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DocumentationLocation
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @param documentationLocation
	 *            of the element.
	 * @return decision knowledge element.
	 */
	public static DecisionKnowledgeElement getDecisionKnowledgeElement(long id,
			DocumentationLocation documentationLocation) {
		AbstractPersistenceManager persistenceManager = AbstractPersistenceManager.getPersistenceManager("",
				documentationLocation);
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(id);
		if (element == null) {
			return new DecisionKnowledgeElementImpl();
		}
		return element;
	}

	/**
	 * Get a decision knowledge element in database by its key.
	 *
	 * @see DecisionKnowledgeElement
	 * @return decision knowledge element.
	 */
	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(String key);

	/**
	 * Get all decision knowledge elements for a project.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @return list of all decision knowledge elements for a project.
	 */
	public abstract List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	/**
	 * Get all decision knowledge elements for a project with a certain knowledge
	 * type.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @see KnowledgeType
	 * @return list of all decision knowledge elements for a project with a certain
	 *         knowledge type.
	 */
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(KnowledgeType type) {
		KnowledgeType simpleType = type.replaceProAndConWithArgument();
		List<DecisionKnowledgeElement> elements = this.getDecisionKnowledgeElements();
		Iterator<DecisionKnowledgeElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			DecisionKnowledgeElement element = iterator.next();
			if (element.getType().replaceProAndConWithArgument() != simpleType) {
				iterator.remove();
			}
		}
		return elements;
	}

	/**
	 * Get all linked elements of the decision knowledge element for a project where
	 * this decision knowledge element is the destination element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         destination element.
	 */
	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all linked elements of the decision knowledge element for a project where
	 * this decision knowledge element is the source element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         source element.
	 */
	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all links where the decision knowledge element is the destination
	 * element.
	 *
	 * @see Link
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of links where the given decision knowledge element is the
	 *         destination element.
	 */
	public abstract List<Link> getInwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all adjacent elements of the decision knowledge element for a project. It
	 * does not matter whether this decision knowledge element is the source or the
	 * destination element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of adjacent elements.
	 */
	public List<DecisionKnowledgeElement> getAdjacentElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElements;
	}

	/**
	 * Get all adjacent elements of the decision knowledge element for a project. It
	 * does not matter whether this decision knowledge element is the source or the
	 * destination element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @return list of adjacent elements.
	 */
	public List<DecisionKnowledgeElement> getAdjacentElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getAdjacentElements(element);
	}

	/**
	 * Get all links where the decision knowledge element is either the source or
	 * the destination element.
	 *
	 * @see Link
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of links where the given decision knowledge element is either
	 *         the source or the destination element.
	 */
	public List<Link> getLinks(DecisionKnowledgeElement element) {
		List<Link> links = new ArrayList<Link>();
		links.addAll(this.getInwardLinks(element));
		links.addAll(this.getOutwardLinks(element));
		return links;
	}

	/**
	 * Get all links where the decision knowledge element is the source element.
	 *
	 * @see Link
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of links where the given decision knowledge element is the
	 *         source element.
	 */
	public abstract List<Link> getOutwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all unlinked elements of the decision knowledge element for a project.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements.
	 */
	public List<DecisionKnowledgeElement> getUnlinkedElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> elements = this.getDecisionKnowledgeElements();
		if (element == null) {
			return elements;
		}
		elements.remove(element);

		List<DecisionKnowledgeElement> linkedElements = this.getAdjacentElements(element);
		elements.removeAll(linkedElements);

		return elements;
	}

	/**
	 * Get all unlinked elements of the decision knowledge element for a project.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @return list of linked elements.
	 */
	public List<DecisionKnowledgeElement> getUnlinkedElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getUnlinkedElements(element);
	}

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param parentElement
	 *            (optional) decision knowledge element that is the parent of this
	 *            element. The parent element is necessary for decision knowledge
	 *            stored in JIRA issue description and comments.
	 * @param user
	 *            authenticated JIRA application user
	 * @return decision knowledge element that is now filled with internal database
	 *         id and key, null if insertion failed.
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		return insertDecisionKnowledgeElement(element, user);
	}

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param user
	 *            authenticated JIRA application user
	 * @return decision knowledge element that is now filled with internal database
	 *         id and key, null if insertion failed.
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		return null;
	}

	/**
	 * Update an existing decision knowledge element in database.
	 *
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if updating was successful.
	 */
	public abstract boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}

	public void setDocumentationLocation(DocumentationLocation documentationLocation) {
		this.documentationLocation = documentationLocation;
	}

	// TODO Move to DecisionKnowledgeElement class
	/**
	 * Determines whether an element is linked to at least one other decision
	 * knowledge element.
	 *
	 * @see DecisionKnowledgeElement
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @param documentation
	 *            location of the element
	 * @return list of linked elements.
	 */
	public static boolean isElementLinked(long id, DocumentationLocation documentationLocation) {
		List<Link> links = GenericLinkManager.getLinksForElement(id, documentationLocation);
		return links != null && links.size() > 0;
	}

	public static boolean isElementLinked(DecisionKnowledgeElement element) {
		return isElementLinked(element.getId(), element.getDocumentationLocation());
	}
}
