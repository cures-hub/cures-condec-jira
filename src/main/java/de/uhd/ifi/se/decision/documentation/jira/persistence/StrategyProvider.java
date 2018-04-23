package de.uhd.ifi.se.decision.documentation.jira.persistence;

import de.uhd.ifi.se.decision.documentation.jira.config.Config;

/**
 * @description Provides the persistence strategy for a project
 */
public class StrategyProvider {

	public PersistenceStrategy getStrategy(String projectKey) {
		if (projectKey == null) {
			throw new IllegalArgumentException("The project key cannot be null.");
		}
		boolean isIssueStrategy = Config.isIssueStrategy(projectKey);
		if (isIssueStrategy) {
			return new IssueStrategy();
		}
		return new ActiveObjectStrategy();
	}
}