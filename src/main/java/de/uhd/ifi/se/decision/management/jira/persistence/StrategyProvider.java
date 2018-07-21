package de.uhd.ifi.se.decision.management.jira.persistence;

/**
 * Provides the persistence strategy for a project
 */
public class StrategyProvider {

	public AbstractPersistenceStrategy getPersistenceStrategy(String projectKey) {
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