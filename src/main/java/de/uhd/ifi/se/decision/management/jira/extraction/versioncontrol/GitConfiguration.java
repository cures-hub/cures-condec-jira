package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CommentStyleType;
import de.uhd.ifi.se.decision.management.jira.model.git.FileType;

/**
 * Contains the configuration details for the git connection for one Jira
 * project (see {@link DecisionKnowledgeProject}).
 */
public class GitConfiguration {

	private boolean isActivated;
	private List<GitRepositoryConfiguration> gitRepoConfigurations;
	private boolean isPostDefaultBranchCommitsActivated;
	private boolean isPostFeatureBranchCommitsActivated;
	private List<FileType> fileTypesToExtract;

	/**
	 * Constructs an object with default values.
	 */
	public GitConfiguration() {
		this.setActivated(false);
		this.setGitRepoConfigurations(new ArrayList<>());
		setPostDefaultBranchCommitsActivated(false);
		setPostFeatureBranchCommitsActivated(false);
		this.fileTypesToExtract = new ArrayList<>();
		fileTypesToExtract.add(new FileType("java", CommentStyleType.JAVA_C));
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

	/**
	 * @return list of configuration details for the git repositories connected to
	 *         the Jira project, i.e., for every
	 *         {@link GitClientForSingleRepository}.
	 */
	public List<GitRepositoryConfiguration> getGitRepoConfigurations() {
		return gitRepoConfigurations;
	}

	/**
	 * @param gitRepoConfigurations
	 *            list of configuration details for the git repositories connected
	 *            to the Jira project, i.e., for every
	 *            {@link GitClientForSingleRepository}.
	 */
	public void setGitRepoConfigurations(List<GitRepositoryConfiguration> gitRepoConfigurations) {
		this.gitRepoConfigurations = gitRepoConfigurations;
	}

	/**
	 * @param gitRepositoryConfiguration
	 *            configuration details for one git repository connected to a Jira
	 *            project, i.e., the {@link GitClientForSingleRepository}.
	 */
	public void addGitRepoConfiguration(GitRepositoryConfiguration gitRepositoryConfiguration) {
		gitRepoConfigurations.add(gitRepositoryConfiguration);
	}

	/**
	 * @return true if git commit messages of default branch commits (e.g. squashed
	 *         commits) should be posted as Jira issue comments. This enables to
	 *         integrate decision knowledge from commit messages into the
	 *         {@link KnowledgeGraph}.
	 */
	public boolean isPostDefaultBranchCommitsActivated() {
		return isPostDefaultBranchCommitsActivated;
	}

	/**
	 * @return true if git commit messages of feature branch commits should be
	 *         posted as Jira issue comments. This enables to integrate decision
	 *         knowledge from commit messages into the {@link KnowledgeGraph}.
	 */
	public boolean isPostFeatureBranchCommitsActivated() {
		return isPostFeatureBranchCommitsActivated;
	}

	/**
	 * @param isPostDefaultBranchCommitsActivated
	 *            true if git commit messages of default branch commits (e.g.
	 *            squashed commits) should be posted as Jira issue comments. This
	 *            enables to integrate decision knowledge from commit messages into
	 *            the {@link KnowledgeGraph}.
	 */
	public void setPostDefaultBranchCommitsActivated(boolean isPostDefaultBranchCommitsActivated) {
		this.isPostDefaultBranchCommitsActivated = isPostDefaultBranchCommitsActivated;
	}

	/**
	 * @param isPostFeatureBranchCommitsActivated
	 *            true if git commit messages of feature branch commits should be
	 *            posted as Jira issue comments. This enables to integrate decision
	 *            knowledge from commit messages into the {@link KnowledgeGraph}.
	 */
	public void setPostFeatureBranchCommitsActivated(boolean isPostFeatureBranchCommitsActivated) {
		this.isPostFeatureBranchCommitsActivated = isPostFeatureBranchCommitsActivated;
	}

	public void setCodeFileEndings(Map<String, String> codeFileEndingMap) {
		fileTypesToExtract = new ArrayList<>();
		for (String commentStyleTypeString : codeFileEndingMap.keySet()) {
			CommentStyleType commentStyleType = CommentStyleType.getFromString(commentStyleTypeString);
			String[] fileEndings = codeFileEndingMap.get(commentStyleTypeString).replaceAll("[^A-Za-z0-9+\\-$#!]+", " ")
					.split(" ");
			for (String fileEnding : fileEndings) {
				fileTypesToExtract.add(new FileType(fileEnding.toLowerCase(), commentStyleType));
			}
		}
	}

	public String getCodeFileEndings(String commentStyleTypeString) {
		CommentStyleType commentStyleType = CommentStyleType.getFromString(commentStyleTypeString);
		String codeFileEndings = "";
		for (FileType codeFileEnding : fileTypesToExtract) {
			if (codeFileEnding.getCommentStyleType() == commentStyleType) {
				codeFileEndings += codeFileEnding.getFileEnding() + ", ";
			}
		}
		if (!codeFileEndings.isEmpty()) {
			return codeFileEndings.substring(0, codeFileEndings.length() - 2); // remove last ", "
		}
		return codeFileEndings;
	}

	/**
	 * @return which code files are extracted from git and decision knowledge from
	 *         their code comments.
	 */
	public List<FileType> getFileTypesToExtract() {
		return fileTypesToExtract;
	}

	/**
	 * @param fileTypesToExtract
	 *            determines which code files are extracted from git and decision
	 *            knowledge from their code comments.
	 */
	public void setFileTypesToExtract(List<FileType> fileTypesToExtract) {
		this.fileTypesToExtract = fileTypesToExtract;
	}

	public boolean shouldFileTypeBeExtracted(FileType fileType) {
		for (FileType fileTypeToExtract : fileTypesToExtract) {
			if (fileTypeToExtract.equals(fileType)) {
				return true;
			}
		}
		return false;
	}

	public FileType getFileTypeForEnding(String fileEnding) {
		for (FileType fileType : fileTypesToExtract) {
			if (fileType.getFileEnding().equalsIgnoreCase(fileEnding)) {
				return fileType;
			}
		}
		return null;
	}
}