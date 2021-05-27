package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

/**
 * Contains the configuration details for the git connection for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class GitConfiguration {

	private boolean isActivated;
	private List<GitRepositoryConfiguration> gitRepoConfigurations;
	private boolean isPostDefaultBranchCommitsActivated;
	private boolean isPostFeatureBranchCommitsActivated;

	public GitConfiguration() {
		this.setActivated(false);
		this.setGitRepoConfigurations(new ArrayList<>());
		setPostDefaultBranchCommitsActivated(false);
		setPostFeatureBranchCommitsActivated(false);
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

	/**
	 * @return true if git commit messages of default branch commits (e.g. squashed
	 *         commits) should be posted as Jira issue comments. This enabled to
	 *         integrate decision knowledge from commit messages into the
	 *         {@link KnowledgeGraph}.
	 */
	public boolean isPostDefaultBranchCommitsActivated() {
		return isPostDefaultBranchCommitsActivated;
	}

	/**
	 * @return true if git commit messages of feature branch commits should be
	 *         posted as Jira issue comments. This enabled to integrate decision
	 *         knowledge from commit messages into the {@link KnowledgeGraph}.
	 */
	public boolean isPostFeatureBranchCommitsActivated() {
		return isPostFeatureBranchCommitsActivated;
	}

	public void setPostDefaultBranchCommitsActivated(boolean isPostDefaultBranchCommitsActivated) {
		this.isPostDefaultBranchCommitsActivated = isPostDefaultBranchCommitsActivated;
	}

	public void setPostFeatureBranchCommitsActivated(boolean isPostFeatureBranchCommitsActivated) {
		this.isPostFeatureBranchCommitsActivated = isPostFeatureBranchCommitsActivated;
	}

}
