package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Abstract class to create, edit, delete and retrieve decision knowledge
 * elements and their links. Decision knowledge can be persisted in entire Jira
 * issues, Jira issue comments, their description, and commit messages.
 *
 * @see JiraIssuePersistenceManager
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
	 *            authenticated Jira application user
	 * @return true if deleting was successful.
	 * @see KnowledgeElement
	 * @see ApplicationUser
	 */
	public boolean deleteDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return this.deleteDecisionKnowledgeElement(element.getId(), user);
	}

	/**
	 * Delete an existing decision knowledge element in database.
	 *
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @param user
	 *            authenticated Jira application user
	 * @return true if deleting was successful.
	 * @see KnowledgeElement
	 * @see ApplicationUser
	 */
	public abstract boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user);

	/**
	 * Get a decision knowledge element in database by its id.
	 *
	 * @param id
	 *            id of the decision knowledge element in database.
	 * @return decision knowledge element.
	 * @see KnowledgeElement
	 */
	public abstract KnowledgeElement getDecisionKnowledgeElement(long id);

	/**
	 * Get a decision knowledge element in database by its key.
	 *
	 * @return decision knowledge element.
	 * @see KnowledgeElement
	 */
	public abstract KnowledgeElement getDecisionKnowledgeElement(String key);

	/**
	 * Get all decision knowledge elements for a project of a certain documentation
	 * location, e.g. all elements in Jira issue descriptions and comments.
	 *
	 * @return list of all decision knowledge elements for a project of a certain
	 *         documentation location.
	 * @see KnowledgeElement
	 * @see DecisionKnowledgeProject
	 */
	public abstract List<KnowledgeElement> getDecisionKnowledgeElements();

	/**
	 * Get all linked elements of the decision knowledge element for a project where
	 * this decision knowledge element is the destination element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         destination element.
	 * @see KnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 */
	public List<KnowledgeElement> getElementsLinkedWithInwardLinks(KnowledgeElement element) {
		List<Link> inwardLinks = getInwardLinks(element);
		List<KnowledgeElement> sourceElements = new ArrayList<KnowledgeElement>();
		for (Link link : inwardLinks) {
			sourceElements.add(link.getSource());
		}
		return sourceElements;
	}

	/**
	 * Get all linked elements of the decision knowledge element for a project where
	 * this decision knowledge element is the source element.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements where this decision knowledge element is the
	 *         source element.
	 * @see KnowledgeElement
	 * @see Link
	 * @see DecisionKnowledgeProject
	 */
	public List<KnowledgeElement> getElementsLinkedWithOutwardLinks(KnowledgeElement element) {
		List<Link> outwardLinks = getOutwardLinks(element);
		List<KnowledgeElement> destinationElements = new ArrayList<KnowledgeElement>();
		for (Link link : outwardLinks) {
			destinationElements.add(link.getTarget());
		}
		return destinationElements;
	}

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
	public abstract List<Link> getInwardLinks(KnowledgeElement element);

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
	public List<Link> getLinks(KnowledgeElement element) {
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
		KnowledgeElement element = this.getDecisionKnowledgeElement(id);
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
	public abstract List<Link> getOutwardLinks(KnowledgeElement element);

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param parentElement
	 *            (optional) decision knowledge element that is the parent of this
	 *            element. The parent element is necessary for decision knowledge
	 *            stored in Jira issue description and comments.
	 * @param user
	 *            authenticated Jira application user
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see KnowledgeElement
	 * @see ApplicationUser
	 */
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user,
			KnowledgeElement parentElement) {
		return insertDecisionKnowledgeElement(element, user);
	}

	/**
	 * Insert a new decision knowledge element into database.
	 *
	 * @param element
	 *            decision knowledge element with attributes such as a summary, the
	 *            knowledge type, and an optional description.
	 * @param user
	 *            authenticated Jira application user
	 * @return decision knowledge element that is now filled with an internal
	 *         database id and key. Returns null if insertion failed.
	 * @see KnowledgeElement
	 * @see ApplicationUser
	 */
	public KnowledgeElement insertDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return null;
	}

	/**
	 * Update an existing decision knowledge element in database.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @param user
	 *            authenticated Jira application user
	 * @return true if updating was successful.
	 * @see KnowledgeElement
	 * @see ApplicationUser
	 */
	public abstract boolean updateDecisionKnowledgeElement(KnowledgeElement element, ApplicationUser user);

	/**
	 * Returns the creator of an element as an application user object.
	 *
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return creator of an element as an {@link ApplicationUser} object.
	 */
	public abstract ApplicationUser getCreator(KnowledgeElement element);

	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}
}