package de.uhd.ifi.se.decision.documentation.jira.config;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ConfigPersistence;

/**
 * @description Class for a JIRA project and its configuration
 */
public class DecisionKnowledgeProjectImpl implements DecisionKnowledgeProject {

	private String projectKey;
	private String projectName;

	public DecisionKnowledgeProjectImpl(String projectKey, String projectName) {
		this.projectKey = projectKey;
		this.projectName = projectName;
		this.setActivated(this.isActivated());
		this.setIssueStrategy(this.isIssueStrategy());
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

	public boolean isIssueStrategy() {
		return ConfigPersistence.isIssueStrategy(this.getProjectKey());
	}

	public void setIssueStrategy(boolean isIssueStrategy) {
		ConfigPersistence.setIssueStrategy(this.getProjectKey(), isIssueStrategy);
	}

	public boolean isActivated() {
		return ConfigPersistence.isActivated(this.getProjectKey());
	}

	public void setActivated(boolean isActivated) {
		ConfigPersistence.setActivated(this.getProjectKey(), isActivated);
	}
}