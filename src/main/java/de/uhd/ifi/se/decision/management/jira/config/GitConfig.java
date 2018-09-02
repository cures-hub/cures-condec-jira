package de.uhd.ifi.se.decision.management.jira.config;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;

public class GitConfig {

	private String path;
	private String projectKey;

	public GitConfig(String projectKey) {
		this.projectKey = projectKey;
		this.path = ConfigPersistence.getGitAddress(projectKey);
	}

	public GitConfig(String projectKey, String path) {
		this.projectKey = projectKey;
		if (ConfigPersistence.getGitAddress(projectKey) != path) {
			ConfigPersistence.setGitAddress(projectKey, path);
		}
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
