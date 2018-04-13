package de.uhd.ifi.se.decision.documentation.jira.config;

/**
 * @description model class for plugin configuration
 */
public class Config {
	private String projectKey;
	private String projectName;
	private String isActivated;
	private String isIssueStrategy;

	public Config(String projectKey, String projectName, String isActivated, String isIssueStrategy) {
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

	public String getIsActivated() {
		return isActivated;
	}

	public void setIsActivated(String isActivated) {
		this.isActivated = isActivated;
	}

	public String getIsIssueStrategy() {
		return isIssueStrategy;
	}

	public void setIsIssueStrategy(String isIssueStrategy) {
		this.isIssueStrategy = isIssueStrategy;
	}
}
