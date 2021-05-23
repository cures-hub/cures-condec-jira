package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitClientForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryFileSystemManager;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Retrieves commits and code changes ({@link Diff}s) from one or more git
 * repositories. Modifying files is not supported.
 *
 * @issue How to access commits related to a Jira issue?
 * @decision Use the jGit library to access the git repositories for a Jira
 *           project!
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for Jira plugin
 *              were used to access git repositories!
 * @con An application link and oAuth is needed to call REST API on Java side in
 *      order to access the git repository with the git integration for Jira
 *      plugin.
 */
public class GitClient {

	private String projectKey;
	private List<GitClientForSingleRepository> gitClientsForSingleRepos;
	private static final Logger LOGGER = LoggerFactory.getLogger(GitClient.class);

	/**
	 * Instances of {@link GitClient}s that are identified by the project key (uses
	 * the multiton pattern).
	 */
	public static Map<String, GitClient> instances = new HashMap<String, GitClient>();

	/**
	 * Retrieves an existing {@link GitClient} instance or creates a new instance if
	 * there is no instance for the given project key.
	 * 
	 * @issue How to access knowledge extracted from commits?
	 * @decision Hold cached commits and their knowledge in a globally accessible
	 *           class!
	 * @pro consistency: git repositories are another knowledge source like
	 *      activeObjects or issueService
	 * @con resources: consumes more memory, risk of resource starving with poor
	 *      implementation
	 * @alternative Instantiate classes for commit knowledge extraction only when
	 *              needed!
	 * @con performance: IO access is costly and requires more CPU time
	 * @pro resources: consumes less memory, CPU time costs are low
	 *
	 * @param projectKey
	 *            of the Jira project.
	 * @return either a new or already existing {@link GitClient} instance.
	 */
	public static GitClient getInstance(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			return null;
		}
		GitClient gitClient;
		boolean extractAllCodeKnowledge = false;
		if (instances.containsKey(projectKey)) {
			gitClient = instances.get(projectKey);
		} else {
			gitClient = new GitClient(projectKey);
			instances.put(projectKey, gitClient);
			extractAllCodeKnowledge = true;
		}
		if (!gitClient.fetchOrCloneRepositories()) {
			return null;
		}
		if (extractAllCodeKnowledge) {
			new Thread(() -> new CodeFileExtractorAndMaintainer(projectKey).extractAllChangedFiles(gitClient)).start();
		}
		return gitClient;
	}

	private GitClient(String projectKey) {
		this();
		this.projectKey = projectKey;
		for (GitRepositoryConfiguration gitRepositoryConfiguration : ConfigPersistenceManager
				.getGitRepositoryConfigurations(projectKey)) {
			if (gitRepositoryConfiguration.isValid()) {
				gitClientsForSingleRepos.add(new GitClientForSingleRepository(projectKey, gitRepositoryConfiguration));
			}
		}
	}

	public GitClient() {
		gitClientsForSingleRepos = new ArrayList<GitClientForSingleRepository>();
	}

	private boolean fetchOrCloneRepositories() {
		boolean isEverythingUpToDate = true;
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			isEverythingUpToDate = isEverythingUpToDate && gitClientForSingleRepo.fetchOrClone();
		}
		return isEverythingUpToDate;
	}

	/**
	 * @param commits
	 *            commits as a list of RevCommit objects.
	 * @return {@link Diff} object for a list of commits containing the
	 *         {@link ChangedFile}s. Each {@link ChangedFile} is created from a diff
	 *         entry and contains the respective edit list.
	 */
	public Diff getDiff(List<RevCommit> commits) {
		if (commits == null || commits.isEmpty()) {
			return new Diff();
		}
		RevCommit firstCommit = commits.stream().min(Comparator.comparing(RevCommit::getCommitTime))
				.orElse(commits.get(0));
		RevCommit lastCommit = commits.stream().max(Comparator.comparing(RevCommit::getCommitTime))
				.orElse(commits.get(commits.size() - 1));
		return getDiff(firstCommit, lastCommit);
	}

	/**
	 * @return {@link Diff} object for all commits on the default branch(es)
	 *         containing the {@link ChangedFile}s. Each {@link ChangedFile} is
	 *         created from a diff entry and contains the respective edit list.
	 */
	public Diff getDiffOfEntireDefaultBranch() {
		List<RevCommit> allCommits = getDefaultBranchCommits();
		if (allCommits.isEmpty()) {
			return new Diff();
		}

		Diff diff = new Diff();
		// because first commit does not have a parent commit
		allCommits.remove(0);
		diff = getDiff(allCommits);

		for (RevCommit commit : allCommits) {
			List<DiffEntry> diffEntriesInCommit = getDiffEntries(commit);
			for (DiffEntry diffEntry : diffEntriesInCommit) {
				for (ChangedFile file : diff.getChangedFiles()) {
					if (diffEntry.getNewPath().contains(file.getName())) {
						file.addCommit(commit);
					}
				}
			}
		}
		return diff;
	}

	public List<DiffEntry> getDiffEntries(RevCommit commit) {
		List<DiffEntry> diffEntries = new ArrayList<>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diffEntries.addAll(gitClientForSingleRepo.getDiffEntries(commit));
		}
		return diffEntries;
	}

	/**
	 * @param jiraIssue
	 *            a Jira issue object.
	 * @return {@link Diff} object for a Jira issue containing the
	 *         {@link ChangedFile}s. Each {@link ChangedFile} is created from a diff
	 *         entry and contains the respective edit list.
	 */
	public Diff getDiff(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new Diff();
		}
		List<RevCommit> defaultBranchCommits = getDefaultBranchCommits(jiraIssue);
		List<RevCommit> featureBranchCommits = getFeatureBranchCommits(jiraIssue);
		List<RevCommit> allCommits = defaultBranchCommits;
		for (RevCommit featureBranchCommit : featureBranchCommits) {
			if (!allCommits.contains(featureBranchCommit)) {
				allCommits.add(featureBranchCommit);
			}
		}
		allCommits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		return getDiff(allCommits);
	}

	/**
	 * @param firstCommit
	 *            first commit on a branch as a RevCommit object.
	 * @param lastCommit
	 *            last commit on a branch as a RevCommit object.
	 * @return {@link Diff} object for a branch of commits indicated by the first
	 *         and last commit on the branch containing the {@link ChangedFile}s.
	 *         Each {@link ChangedFile} is created from a diff entry and contains
	 *         the respective edit list.
	 */
	public Diff getDiff(RevCommit firstCommit, RevCommit lastCommit) {
		if (firstCommit == null || lastCommit == null) {
			return new Diff();
		}
		Diff diff = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diff.getChangedFiles().addAll(gitClientForSingleRepo.getDiff(firstCommit, lastCommit).getChangedFiles());
		}
		return diff;
	}

	/**
	 * @param revCommit
	 *            commit as a {@link RevCommit} object.
	 * @return {@link Diff} object containing the {@link ChangedFile}s. Each
	 *         {@link ChangedFile} is created from a diff entry and contains the
	 *         respective edit list.
	 */
	public Diff getDiff(RevCommit revCommit) {
		Diff diffForCommit = getDiff(revCommit, revCommit);
		diffForCommit.getChangedFiles().forEach(file -> file.addCommit(revCommit));
		return diffForCommit;
	}

	/**
	 * @param featureBranch
	 *            as a {@link Ref} object.
	 * @return String of remote repository URI containing the given branch. Returns
	 *         null if branch is not contained in any repo.
	 */
	public String getRepoUriFromBranch(Ref featureBranch) {
		if (featureBranch == null) {
			return "";
		}
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			Git git = gitClientForSingleRepo.getGit();
			try {
				if (git != null && git.getRepository() != null
						&& git.getRepository().exactRef(featureBranch.getName()) != null) {
					return gitClientForSingleRepo.getRemoteUri();
				}
			} catch (IOException e) {
				LOGGER.error("URI could not be retrieved from branch name. " + e.getMessage());
			}
		}
		return "";
	}

	/**
	 * @param jiraIssue
	 *            such as work item/development task/requirements that key was
	 *            mentioned in the commit messages.
	 * @return list of unique commits of a feature branch, which do not exist in the
	 *         default branch. Commits are not sorted.
	 */
	public List<RevCommit> getFeatureBranchCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new ArrayList<RevCommit>();
		}
		List<RevCommit> commits = new ArrayList<RevCommit>();
		List<Ref> branches = getBranches(jiraIssue.getKey());
		for (Ref featureBranch : branches) {
			commits.addAll(getFeatureBranchCommits(featureBranch));
		}
		return commits;
	}

	/**
	 * @param featureBranch
	 *            as a {@link Ref} object.
	 * @return list of unique commits of a feature branch, which do not exist in the
	 *         default branch. Commits are not sorted.
	 */
	public List<RevCommit> getFeatureBranchCommits(Ref featureBranch) {
		List<RevCommit> branchCommits = getCommits(featureBranch);
		List<RevCommit> defaultBranchCommits = getDefaultBranchCommits();
		List<RevCommit> branchUniqueCommits = new ArrayList<RevCommit>();

		for (RevCommit commit : branchCommits) {
			if (!defaultBranchCommits.contains(commit)) {
				branchUniqueCommits.add(commit);
			}
		}
		branchUniqueCommits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		return branchUniqueCommits;
	}

	/**
	 * @param branch
	 *            as a {@link Ref} object.
	 * @return list of commits of a branch, which might also exist in the default
	 *         branch.
	 */
	public List<RevCommit> getCommits(Ref branch) {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommits(branch));
		}
		commits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		return commits;
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return commits with the Jira issue key in their commit message as a list of
	 *         {@link RevCommits}.
	 * 
	 * @issue What is the return value of methods that would normally return a
	 *        collection (e.g. list) with an invalid input parameter?
	 * @decision Methods with an invalid input parameter return an empty list!
	 * @pro Would prevent a null pointer exception.
	 * @con Is misleading since it is not clear whether the list is empty but has a
	 *      valid input parameter or because of an invalid parameter.
	 * @alternative Methods with an invalid input parameter return null!
	 * @con null values might cause a null pointer exception.
	 */
	public List<RevCommit> getCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new LinkedList<RevCommit>();
		}
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommits(jiraIssue, false));
		}
		return commits;
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return number of commits with the Jira issue key in their commit message on
	 *         the default branch.
	 */
	public int getNumberOfCommitsOnDefaultBranches(Issue jiraIssue) {
		if (jiraIssue == null) {
			return 0;
		}
		List<RevCommit> commits = getDefaultBranchCommits(jiraIssue, false);
		return commits.size();
	}

	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @return all commits on the default branch(es) as a list of
	 *         {@link RevCommit}s.
	 */
	public List<RevCommit> getDefaultBranchCommits() {
		List<RevCommit> commits = new ArrayList<>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getDefaultBranchCommits());
		}
		commits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		return commits;
	}

	/**
	 * @param jiraIssue
	 *            such as work item/development task/requirements that key was
	 *            mentioned in the commit messages.
	 * @return all commits on the branch(es) as a list of {@link RevCommit}s. The
	 *         list is sorted by committing time: oldest commits come first.
	 */
	public List<RevCommit> getDefaultBranchCommits(Issue jiraIssue) {
		return getDefaultBranchCommits(jiraIssue, true);
	}

	/**
	 * @param jiraIssue
	 *            such as work item/development task/requirements that key was
	 *            mentioned in the commit messages.
	 * @param areCommitsSortedByTime
	 *            true if commits should be sorted by time (oldest commits come
	 *            first!)
	 * @return all commits on the branch(es) as a list of {@link RevCommit}s. The
	 *         list is sorted by committing time: oldest commits come first.
	 */
	public List<RevCommit> getDefaultBranchCommits(Issue jiraIssue, boolean areCommitsSortedByTime) {
		List<RevCommit> commits = new ArrayList<>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommits(jiraIssue, true));
		}
		if (areCommitsSortedByTime) {
			commits.sort(Comparator.comparingInt(RevCommit::getCommitTime));
		}
		return commits;
	}

	/**
	 * Closes all repositories and deletes all local files.
	 */
	public boolean deleteRepositories() {
		boolean isDeleted = true;
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			isDeleted = isDeleted && gitClientForSingleRepo.getFileSystemManager().deleteWorkingDirectory();
		}
		return isDeleted && GitRepositoryFileSystemManager.deleteProjectDirectory(projectKey);
	}

	/**
	 * @return all {@link GitClientForSingleRepository} for a project.
	 */
	public List<GitClientForSingleRepository> getGitClientsForSingleRepos() {
		return gitClientsForSingleRepos;
	}

	/**
	 * @param uri
	 *            Uniform Resource Identifier (URI) of the remote git repository.
	 * @return {@link GitClientForSingleRepository} for the given URI.
	 */
	public GitClientForSingleRepository getGitClientsForSingleRepo(String uri) {
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			if (gitClientForSingleRepo.getRemoteUri().equalsIgnoreCase(uri)) {
				return gitClientForSingleRepo;
			}
		}
		return null;
	}

	/**
	 * @param branchName
	 *            e.g. "master" or Jira issue key
	 * @return all branches matching the name as a list of {@link Ref} objects.
	 */
	public List<Ref> getBranches(String branchName) {
		if (branchName == null || branchName.isBlank()) {
			return new ArrayList<>();
		}
		List<Ref> remoteBranches = getBranches();
		List<Ref> branchCandidates = remoteBranches.stream()
				.filter(ref -> ref.getName().toUpperCase().contains(branchName.toUpperCase()))
				.collect(Collectors.toList());
		return branchCandidates;
	}

	/**
	 * @return all branches as a list of {@link Ref} objects.
	 */
	public List<Ref> getBranches() {
		List<Ref> allRemoteBranches = new ArrayList<>();
		getGitClientsForSingleRepos()
				.forEach(gitClientForSingleRepo -> allRemoteBranches.addAll(gitClientForSingleRepo.getBranches()));
		return allRemoteBranches;
	}
}