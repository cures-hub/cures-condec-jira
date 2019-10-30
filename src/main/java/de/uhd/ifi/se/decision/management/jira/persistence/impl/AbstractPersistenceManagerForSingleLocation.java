package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Abstract class to create, edit, delete and retrieve decision knowledge
 * elements and their links. Concrete persistence strategies for first class
 * elements are either the JIRA issue strategy or the active object strategy.
 * For example, other methods are to persist knowledge in JIRA issue comments,
 * the description, and commit messages.
 *
 * @see JiraIssuePersistenceManager
 * @see ActiveObjectPersistenceManager
 * @see JiraIssueTextPersistenceManager
 */
public abstract class AbstractPersistenceManagerForSingleLocation {

	protected String projectKey;
	protected DocumentationLocation documentationLocation;

	/**
	 * Delete an existing decision knowledge element in database.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if deleting was successful.
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 */
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		return this.deleteDecisionKnowledgeElement(element.getId(), user);
	}

	/**
	 * Delete an existing decision knowledge element in database.
	 *
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if deleting was successful.
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 */
	public abstract boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user);

	/**
	 * Get a decision knowledge element in database by its id.
	 *
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @return decision knowledge element.
	 * @see DecisionKnowledgeElement
	 */
	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(long id);

	/**
	 * Get a decision knowledge element in database by its key.
	 *
	 * @return decision knowledge element.
	 * @see DecisionKnowledgeElement
	 */
	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(String key);

	/**
	 * Get all decision knowledge elements for a project of a certain documentation
	 * location, e.g. all elements in Jira issue descriptions and comments.
	 *
	 * @return list of all decision knowledge elements for a project of a certain
	 *         documentation location.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 */
	public abstract List<DecisionKnowledgeElement> getDecisionKnowledgeElements();

	/**
	 * Get all decision knowledge elements for a project of a certain documentation
	 * location, e.g. all elements in Jira issue descriptions and comments, and with
	 * a certain knowledge type.
	 *
	 * @return list of all decision knowledge elements for a project with a certain
	 *         knowledge type of a certain documentation location.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @see KnowledgeType
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
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         destination element.
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 */
	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all linked elements of the decision knowledge element for a project where
	 * this decision knowledge element is the source element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         source element.
	 * @see DecisionKnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 */
	public abstract List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all links where the decision knowledge element is the destination
	 * element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of links where the given decision knowledge element is the
	 *         destination element.
	 * @see Link
	 */
	public abstract List<Link> getInwardLinks(DecisionKnowledgeElement element);

	/**
	 * Get all adjacent elements of the decision knowledge element for a project. It
	 * does not matter whether this decision knowledge element is the source or the
	 * destination element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of adjacent elements.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
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
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @return list of adjacent elements.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 */
	public List<DecisionKnowledgeElement> getAdjacentElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getAdjacentElements(element);
	}

	/**
	 * Get all links where the decision knowledge element is either the source or
	 * the destination element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of links where the given decision knowledge element is either
	 *         the source or the destination element.
	 * @see Link
	 */
	public List<Link> getLinks(DecisionKnowledgeElement element) {
		List<Link> links = new ArrayList<Link>();
		links.addAll(this.getInwardLinks(element));
		links.addAll(this.getOutwardLinks(element));
		return links;
	}

	/**
	 * Get all links where the id of the node or decision knowledge element is
	 * either the source or the destination element.
	 *
	 * @param id
	 *            of the node or the DecisionKnowledgeElement
	 * @return list of links where the given decision knowledge element is either
	 *         the source or the destination element.
	 */
	public List<Link> getLinks(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getLinks(element);
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
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
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
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @return list of linked elements.
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 */
	public List<DecisionKnowledgeElement> getUnlinkedElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getUnlinkedElements(element);
	}

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param parentElement
	 *            (optional) decision knowledge element that is the parent of this
	 *            element. The parent element is necessary for decision knowledge
	 *            stored in JIRA issue description and comments.
	 * @param user
	 *            authenticated JIRA application user
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user, DecisionKnowledgeElement parentElement) {
		return insertDecisionKnowledgeElement(element, user);
	}

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param user
	 *            authenticated JIRA application user
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 */
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		return null;
	}

	/**
	 * Update an existing decision knowledge element in database.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated JIRA application user
	 * @return true if updating was successful.
	 * @see DecisionKnowledgeElement
	 * @see ApplicationUser
	 */
	public abstract boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

	/**
	 * Returns the creator of an element as an application user object.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return creator of an element as an {@link ApplicationUser} object.
	 */
	public abstract ApplicationUser getCreator(DecisionKnowledgeElement element);

	// TODO
	// @issue Is it necessary to distinguish between methods
	// updateDecisionKnowledgeElement and
	// updateDecisionKnowledgeElementWithoutStatusChange?
	public abstract boolean updateDecisionKnowledgeElementWithoutStatusChange(DecisionKnowledgeElement element,
			ApplicationUser user);

	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}

	// TODO Move to DecisionKnowledgeElement class

	/**
	 * Determines whether an element is linked to at least one other decision
	 * knowledge element.
	 *
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @param documentationLocation
	 *            of the element
	 * @return list of linked elements.
	 * @see DecisionKnowledgeElement
	 */
	public static boolean isElementLinked(long id, DocumentationLocation documentationLocation) {
		List<Link> links = GenericLinkManager.getLinksForElement(id, documentationLocation);
		return links != null && links.size() > 0;
	}

	public static boolean isElementLinked(DecisionKnowledgeElement element) {
		return isElementLinked(element.getId(), element.getDocumentationLocation());
	}
}