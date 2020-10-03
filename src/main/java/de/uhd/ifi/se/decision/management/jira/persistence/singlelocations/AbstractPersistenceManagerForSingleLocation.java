package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Abstract class to create, edit, delete, and retrieve
 * {@link KnowledgeElement}s and their {@link Link}s. Decision knowledge can be
 * persisted in entire Jira issues, Jira issue comments, their description, and
 * commit messages.
 *
 * @see JiraIssuePersistenceManager
 * @see JiraIssueTextPersistenceManager
 * @see CodeClassPersistenceManager
 */
public abstract class AbstractPersistenceManagerForSingleLocation {

	protected String projectKey;
	protected DocumentationLocation documentationLocation;

	/**
	 * Deletes an existing {@link KnowledgeElement} in database.
	 *
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @param user
	 *            authenticated Jira application user
	 * @return true if deleting was successful.
	 * @see ApplicationUser
	 */
	public boolean deleteKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		return deleteKnowledgeElement(element.getId(), user);
	}

	/**
	 * Deletes an existing {@link KnowledgeElement} in database.
	 *
	 * @param id
	 *            id of the {@link KnowledgeElement} in database.
	 * @param user
	 *            authenticated Jira application user
	 * @return true if deleting was successful.
	 * @see ApplicationUser
	 */
	public abstract boolean deleteKnowledgeElement(long id, ApplicationUser user);

	/**
	 * @param id
	 *            id of the {@link KnowledgeElement} in database.
	 * @return {@link KnowledgeElement}.
	 */
	public abstract KnowledgeElement getKnowledgeElement(long id);

	/**
	 * @param key
	 *            of the {@link KnowledgeElement}.
	 * @return {@link KnowledgeElement}.
	 */
	public abstract KnowledgeElement getKnowledgeElement(String key);

	/**
	 * @return list of all {@link KnowledgeElement} for a project of a certain
	 *         documentation location.
	 */
	public abstract List<KnowledgeElement> getKnowledgeElements();

	/**
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @return list of {@link Link}s where the given {@link KnowledgeElement} is the
	 *         destination/target element.
	 */
	public abstract List<Link> getInwardLinks(KnowledgeElement element);

	/**
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @return list of {@link Link}s where the given {@link KnowledgeElement} is the
	 *         source element.
	 */
	public abstract List<Link> getOutwardLinks(KnowledgeElement element);

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
		return insertKnowledgeElement(element, user);
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
		return null;
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
	public abstract boolean updateKnowledgeElement(KnowledgeElement element, ApplicationUser user);

	/**
	 * @param element
	 *            {@link KnowledgeElement} with id in database.
	 * @return creator of an element as an {@link ApplicationUser} object.
	 */
	public abstract ApplicationUser getCreator(KnowledgeElement element);

	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}
}