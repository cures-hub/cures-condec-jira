package de.uhd.ifi.se.decision.management.jira.persistence;

/**
 * Provides the persistence strategy for a project
 */
public class StrategyProvider {

	/**
	 * Get the persistence strategy for decision knowledge used in a project.
	 *
	 * @see AbstractPersistenceStrategy
	 * @see IssueStrategy
	 * @see ActiveObjectStrategy
	 * @param projectKey
	 *            of the JIRA project.
	 * @return persistence strategy for decision knowledge used in the given
	 *         project, either issue strategy or active object strategy. The active
	 *         object strategy is the default strategy.
	 */
	public static AbstractPersistenceStrategy getPersistenceStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new IssueStrategy(projectKey);
		}
		return new ActiveObjectStrategy(projectKey);
	}
}