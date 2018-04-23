package de.uhd.ifi.se.decision.documentation.jira.model;

/**
 * @description Interface for a project and its configuration
 */
public interface IProject {
	public String getProjectKey();

	public void setProjectKey(String projectKey);

	public String getProjectName();

	public void setProjectName(String projectName);

	public boolean isActivated();

	public void setActivated(boolean isActivated);

	public boolean isIssueStrategy();

	public void setIssueStrategy(boolean isIssueStrategy);
}