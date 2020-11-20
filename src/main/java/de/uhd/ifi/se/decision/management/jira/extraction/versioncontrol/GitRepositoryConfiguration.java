package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class GitRepositoryConfiguration {
    private String repoUri;
	private String defaultBranchName;
	private String authMethod;
	private String username;
	private String token;

	public GitRepositoryConfiguration(String repoUri, String projectKey) {
		this.repoUri = repoUri;
		this.defaultBranchName = ConfigPersistenceManager.getDefaultBranch(projectKey, repoUri);
		this.authMethod = ConfigPersistenceManager.getAuthMethod(projectKey, repoUri);
		this.username = ConfigPersistenceManager.getUsername(projectKey, repoUri);
		this.token = ConfigPersistenceManager.getToken(projectKey, repoUri);
	}

	public String getRepoUri() {
		return this.repoUri;
	}

	public String getDefaultBranchName() {
		return this.defaultBranchName;
	}

	public String getAuthMethod() {
		return this.authMethod;
	}

	public String getUsername() {
		return this.username;
	}

	public String getToken() {
		return this.token;
	}
	
	// public void setRepoUri(String repoUri) {
	// 	this.repoUri = repoUri;
	// }

	// public void setDefaultBranchName(String defaultBranchName) {
	// 	this.defaultBranchName = defaultBranchName;
	// }

	// public void setAuthMethod(String authMethod) {
	// 	this.authMethod = authMethod;
	// }

	// public void setUsername(String username) {
	// 	this.username = username;
	// }

	// public void setToken(String token) {
	// 	this.token = token;
	// }
}
