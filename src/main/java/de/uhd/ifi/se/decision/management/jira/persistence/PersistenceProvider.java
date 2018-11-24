package de.uhd.ifi.se.decision.management.jira.persistence;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

/**
 * Provides the persistence method for a project
 */
public class PersistenceProvider {

	/**
	 * Get the persistence strategy for autarkical decision knowledge elements used
	 * in a project. This elements are directly stored in JIRA and independent from
	 * other JIRA issues. These elements are "first class" elements.
	 *
	 * @see AbstractPersistenceManager
	 * @see JiraIssuePersistence
	 * @see ActiveObjectPersistence
	 * @param projectKey
	 *            of the JIRA project.
	 * @return persistence strategy for "first class" decision knowledge elements
	 *         used in the given project, either issue strategy or active object
	 *         strategy. The active object strategy is the default strategy.
	 */
	public static AbstractPersistenceManager getPersistenceStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new JiraIssuePersistence(projectKey);
		}
		return new ActiveObjectPersistence(projectKey);
	}

	/**
	 * Get the persistence manager of a given decision knowledge elements.
	 *
	 * @see AbstractPersistenceManager
	 * @param element
	 *            decision knowledge element with project and documentation
	 *            location.
	 * @return persistence manager of a given decision knowledge elements. Returns
	 *         null in case the persistence manager cannot be found.
	 */
	public static AbstractPersistenceManager getPersistence(DecisionKnowledgeElement element) {
		if (element == null) {
			throw new IllegalArgumentException("The element cannot be null.");
		}

		String projectKey = element.getProject().getProjectKey();

		switch (element.getDocumentationLocation()) {
		case JIRAISSUE:
			return new JiraIssuePersistence(projectKey);
		case JIRAISSUECOMMENT:
			return new JiraIssueCommentPersistence(projectKey);
		case ACTIVEOBJECT:
			return new ActiveObjectPersistence(projectKey);
		default:
			return null;
		}
	}
}