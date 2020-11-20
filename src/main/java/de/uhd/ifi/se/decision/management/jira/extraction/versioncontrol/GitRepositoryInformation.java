package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class GitRepositoryInformation {
    private String repoUri;
	private String defaultBranchName;
	private String authMethod;
	private String username;
	private String token;

	public GitRepositoryInformation(String repoUri, String projectKey) {
		this.repoUri = repoUri;
		this.defaultBranchName = ConfigPersistenceManager.getDefaultBranches(projectKey).get(repoUri);
		this.authMethod = ConfigPersistenceManager.getAuthMethods(projectKey).get(repoUri);
		this.username = ConfigPersistenceManager.getUsernames(projectKey).get(repoUri);
		this.token = ConfigPersistenceManager.getTokens(projectKey).get(repoUri);
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

	public void setRepoUri(String repoUri) {
		this.repoUri = repoUri;
	}

	public void setDefaultBranchName(String defaultBranchName) {
		this.defaultBranchName = defaultBranchName;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
