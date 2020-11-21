package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

public class GitRepositoryConfiguration {
    private String repoUri;
	private String defaultBranch;
	private String authMethod;
	private String username;
	private String token;

	public GitRepositoryConfiguration(String repoUri, String defaultBranch, String authMethod, String username, String token) {
		if (defaultBranch == "") {
			defaultBranch = "master";
		}
		if (!authMethod.equals("HTTP") && !authMethod.equals("GITHUB") && !authMethod.equals("GITLAB")) {
			authMethod = "NONE";
		}

		this.repoUri = repoUri;
		this.defaultBranch = defaultBranch;
		this.authMethod = authMethod;
		this.username = username;
		this.token = token;
	}

	public String getRepoUri() {
		return this.repoUri;
	}

	public String getDefaultBranch() {
		return this.defaultBranch;
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
