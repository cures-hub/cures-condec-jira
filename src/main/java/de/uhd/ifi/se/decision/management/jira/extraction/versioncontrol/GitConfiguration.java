package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

/**
 * Contains the configuration details for the git connection for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class GitConfiguration {

	private boolean isActivated;
	private List<GitRepositoryConfiguration> gitRepoConfigurations;

	public GitConfiguration() {
		this.setActivated(false);
		this.setGitRepoConfigurations(new ArrayList<>());
	}

	/**
	 * @return true if {@link ChangedFile}s and decision knowledge is extracted from
	 *         git. The decision knowledge is both extracted from commit messages
	 *         and code comments.
	 */
	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * @param isActivated
	 *            true if {@link ChangedFile}s and decision knowledge is extracted
	 *            from git. The decision knowledge is both extracted from commit
	 *            messages and code comments.
	 */
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
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
