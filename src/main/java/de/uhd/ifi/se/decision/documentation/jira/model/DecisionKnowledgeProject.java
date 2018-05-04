package de.uhd.ifi.se.decision.documentation.jira.model;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;

/**
 * @description Interface for a project and its configuration
 */
public interface DecisionKnowledgeProject {
	public String getProjectKey();

	public void setProjectKey(String projectKey);

	public String getProjectName();

	public void setProjectName(String projectName);

	public boolean isActivated();

	public void setActivated(boolean isActivated);

	public boolean isIssueStrategy();

	public void setIssueStrategy(boolean isIssueStrategy);

	public PersistenceStrategy getPersistenceStrategy();
}