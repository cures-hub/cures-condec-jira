package de.uhd.ifi.se.decision.documentation.jira.config;

/**
 * @description Model class for plug-in configuration
 */
public class Config {
	private String projectKey;
	private String projectName;
	private boolean isActivated;
	private boolean isIssueStrategy;

	public Config(String projectKey, String projectName, boolean isActivated, boolean isIssueStrategy) {
		this.projectKey = projectKey;
		this.projectName = projectName;
		this.isActivated = isActivated;
		this.isIssueStrategy = isIssueStrategy;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public boolean isIssueStrategy() {
		return isIssueStrategy;
	}

	public void setIssueStrategy(boolean isIssueStrategy) {
		this.isIssueStrategy = isIssueStrategy;
	}
}
