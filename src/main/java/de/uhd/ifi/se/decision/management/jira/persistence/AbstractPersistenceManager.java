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

/**
 * Abstract class to create, edit, delete and retrieve decision knowledge
 * elements and their links. Concrete persistence strategies for first class
 * elements are either the JIRA issue strategy or the active object strategy.
 * Use the strategy provider to get the persistence strategy used in a project.
 * Other persistence methods are for example JIRA issue comments, description,
 * and commit messages.
 *
 * @see JiraIssuePersistenceManager
 * @see ActiveObjectPersistenceManager
 * @see JiraIssueCommentPersistenceManager
 * @see PersistenceProvider
 */
public abstract class AbstractPersistenceManager {

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
	public abstract boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user);

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
	public abstract boolean deleteLink(Link link, ApplicationUser user);

	/**
	 * Get a decision knowledge element in database by its id.
	 *
	 * @see DecisionKnowledgeElement
	 * @return decision knowledge element.
	 */
	public abstract DecisionKnowledgeElement getDecisionKnowledgeElement(long id);

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
		List<DecisionKnowledgeElement> elements = this.getDecisionKnowledgeElements();
		Iterator<DecisionKnowledgeElement> iterator = elements.iterator();
		while (iterator.hasNext()) {
			DecisionKnowledgeElement element = iterator.next();
			if (element.getType() != type) {
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
	 * Get all linked elements of the decision knowledge element for a project. It
	 * does not matter whether this decision knowledge element is the source or the
	 * destination element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param element
	 *            decision knowledge element with id in database.
	 * @return list of linked elements.
	 */
	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>();
		linkedElements.addAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElements.addAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElements;
	}

	/**
	 * Get all linked elements of the decision knowledge element for a project. It
	 * does not matter whether this decision knowledge element is the source or the
	 * destination element.
	 *
	 * @see DecisionKnowledgeElement
	 * @see DecisionKnowledgeProject
	 * @param id
	 *            id of a decision knowledge element in database. The id is
	 *            different to the key.
	 * @return list of linked elements.
	 */
	public List<DecisionKnowledgeElement> getLinkedElements(long id) {
		DecisionKnowledgeElement element = this.getDecisionKnowledgeElement(id);
		return this.getLinkedElements(element);
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

		List<DecisionKnowledgeElement> linkedElements = this.getLinkedElements(element);
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
	 * @param user
	 *            authenticated JIRA application user
	 * @return decision knowledge element that is now filled with internal database
	 *         id and key, null if insertion failed.
	 */
	public abstract DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user);

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
	 * @return internal database id, zero if insertion failed.
	 */
	public abstract long insertLink(Link link, ApplicationUser user);

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
		case JIRAISSUECOMMENT:
			return new JiraIssueCommentPersistenceManager(projectKey);
		case ACTIVEOBJECT:
			return new ActiveObjectPersistenceManager(projectKey);
		default:
			return getDefaultPersistenceStrategy(projectKey);
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
}
