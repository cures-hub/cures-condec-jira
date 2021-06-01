package de.uhd.ifi.se.decision.management.jira.git.config;

import de.uhd.ifi.se.decision.management.jira.git.GitClientForSingleRepository;

/**
 * Authentication method for a {@link GitClientForSingleRepository}. The
 * authentication method is set in the {@link GitRepositoryConfiguration}.
 */
public enum AuthMethod {
	NONE, HTTP, GITHUB, GITLAB;

	public static AuthMethod getAuthMethodByName(String name) {
		if (name == null || name.isBlank()) {
			return NONE;
		}
		for (AuthMethod authMethod : values()) {
			if (authMethod.name().equalsIgnoreCase(name)) {
				return authMethod;
			}
		}
		return NONE;
	}
}
