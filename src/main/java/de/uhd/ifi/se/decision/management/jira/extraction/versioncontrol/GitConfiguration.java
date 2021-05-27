package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the git connection for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class GitConfiguration {

	private List<GitRepositoryConfiguration> gitRepoConfigurations;

	public GitConfiguration() {
		this.setGitRepoConfigurations(new ArrayList<>());

	}

	public List<GitRepositoryConfiguration> getGitRepoConfigurations() {
		return gitRepoConfigurations;
	}

	public void setGitRepoConfigurations(List<GitRepositoryConfiguration> gitRepoConfigurations) {
		this.gitRepoConfigurations = gitRepoConfigurations;
	}

	public void addGitRepoConfiguration(GitRepositoryConfiguration gitRepositoryConfiguration) {
		gitRepoConfigurations.add(gitRepositoryConfiguration);
	}

}
