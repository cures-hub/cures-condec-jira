package de.uhd.ifi.se.decision.documentation.jira.persistence;

/**
 * @description Provides the persistence strategy for a project
 */
public class StrategyProvider {

	public PersistenceStrategy getStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}

		boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new IssueStrategy();
		}
		return new ActiveObjectStrategy();
	}
}