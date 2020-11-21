package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.List;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Contains the configuration details for one git repository connected to a Jira
 * project, i.e., the {@link GitClientForSingleRepository}. A Jira project can
 * be connected to more than one git repository.
 */
public class GitRepositoryConfiguration {

	private String repoUri;
	private String defaultBranch;
	private AuthMethod authMethod;
	private String username;
	private String token;

	public GitRepositoryConfiguration(String repoUri, String defaultBranch, String authMethod, String username,
			String token) {
		this.repoUri = repoUri;
		this.defaultBranch = defaultBranch;
		this.authMethod = AuthMethod.getAuthMethodByName(authMethod);
		this.username = username;
		this.token = token;

		if (this.defaultBranch == null || this.defaultBranch.isBlank()) {
			this.defaultBranch = "master";
		}
	}

	public String getRepoUri() {
		return repoUri;
	}

	public String getDefaultBranch() {
		return defaultBranch;
	}

	public String getAuthMethod() {
		return authMethod.name();
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public boolean isValid() {
		return repoUri != null && !repoUri.isBlank();
	}

	public UsernamePasswordCredentialsProvider getCredentialsProvider() {
		switch (authMethod) {
		case HTTP:
			return new UsernamePasswordCredentialsProvider(getUsername(), getToken());
		case GITHUB:
			return new UsernamePasswordCredentialsProvider(getToken(), "");
		case GITLAB:
			return new UsernamePasswordCredentialsProvider(getUsername(), getToken());
		default:
			return null;
		}
	}

	public static boolean areAllGitRepositoryConfigurationsValid(
			List<GitRepositoryConfiguration> gitRepositoryConfigurations) {
		for (GitRepositoryConfiguration configuration : gitRepositoryConfigurations) {
			if (!configuration.isValid()) {
				return false;
			}
		}
		return true;
	}
}
