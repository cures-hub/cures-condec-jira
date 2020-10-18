package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitClientForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Retrieves commits and code changes (diffs) from one or more git repositories.
 *
 * Multiple instances of this class are "thread-safe" in the limited way that
 * the checked-out branch files are stored in dedicated branch folders and can
 * be read. Modifying files is not supported.
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

	/**
	 * @issue What is the best place to clone the git repo to?
	 * @decision Clone git repo to JiraHome/data/condec-plugin/git!
	 * @pro The Git integration for Jira plug-in clones its repos to a similar
	 *      folder: JiraHome/data/git-plugin.
	 */
	public static String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "git" + File.separator;

	public static final long REPO_OUTDATED_AFTER = 10 * 60 * 1000; // ex. 10 minutes = 10 minutes * 60 seconds * 1000
	// miliseconds
	private List<GitClientForSingleRepository> gitClientsForSingleRepos;

	private String projectKey;
	private static final Logger LOGGER = LoggerFactory.getLogger(GitClient.class);

	/**
	 * Instances of {@link GitClient}s that are identified by the project key (uses
	 * the multiton pattern).
	 */
	private static Map<String, GitClient> instances = new HashMap<String, GitClient>();

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
	public static GitClient getOrCreate(String projectKey) {
		if (projectKey == null || projectKey.isBlank()) {
			return null;
		}
		GitClient gitClient;

		if (instances.containsKey(projectKey)) {
			instances.remove(projectKey);
		}
		
		gitClient = new GitClient(projectKey);
		instances.put(projectKey, gitClient);

		gitClient.pullOrCloneRepositories();
		return gitClient;
	}

	private GitClient(String projectKey) {
		this(ConfigPersistenceManager.getGitUris(projectKey), ConfigPersistenceManager.getDefaultBranches(projectKey),
		ConfigPersistenceManager.getAuthMethods(projectKey), ConfigPersistenceManager.getUsernames(projectKey),
		ConfigPersistenceManager.getTokens(projectKey), projectKey);
	}

	private GitClient(List<String> uris, String projectKey) {
		this(uris, ConfigPersistenceManager.getDefaultBranches(projectKey),
		ConfigPersistenceManager.getAuthMethods(projectKey), ConfigPersistenceManager.getUsernames(projectKey),
		ConfigPersistenceManager.getTokens(projectKey), projectKey);
	}

	private GitClient(List<String> uris, Map<String, String> defaultBranches, Map<String, String> authMethods,
			Map<String, String> usernames, Map<String, String> tokens, String projectKey) {
		this();
		this.projectKey = projectKey;
		uris.forEach(uri -> gitClientsForSingleRepos
				.add(new GitClientForSingleRepository(uri, defaultBranches.get(uri),
				projectKey, authMethods.get(uri), usernames.get(uri), tokens.get(uri))));
	}

	public GitClient() {
		gitClientsForSingleRepos = new ArrayList<GitClientForSingleRepository>();
	}

	public boolean pullOrCloneRepositories() {
		boolean isEverythingUpToDate = true;
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			isEverythingUpToDate = isEverythingUpToDate && gitClientForSingleRepo.pullOrClone();
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
		if (commits == null || commits.size() == 0) {
			return new Diff();
		}
		Diff diff = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diff.getChangedFiles().addAll(gitClientForSingleRepo.getDiff(commits).getChangedFiles());
		}
		return diff;
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
		Diff diff = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diff.getChangedFiles().addAll(gitClientForSingleRepo.getDiff(jiraIssue).getChangedFiles());
		}
		return diff;
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
		return getDiff(revCommit, revCommit);
	}

	/**
	 * @param featureBranch
	 * @return String of Remote Repository Uri containing the given Branch or null
	 *         if branch not contained in any Repo.
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
	 * Temporally switches git client's directory to feature branch directory to
	 * fetch commits, afterwards returns to default branch directory after.
	 *
	 * @param featureBranch
	 *            ref of the feature branch, Uri of Git Repository
	 * @return list of unique commits of a <b>feature</b> branch, which do not exist
	 *         in the <b>default</b> branch. Commits are sorted by age, beginning
	 *         with the oldest.
	 */
	public List<RevCommit> getFeatureBranchCommits(Ref featureBranch) {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getFeatureBranchCommits(featureBranch));
		}
		return commits;
	}

	/**
	 * @param featureBranchName
	 *            name of the feature branch.
	 * @return list of unique commits of a <b>feature</b> branch, which do not exist
	 *         in the <b>default</b> branch. Commits are sorted by age, beginning
	 *         with the oldest.
	 */
	public List<RevCommit> getFeatureBranchCommits(String featureBranchName) {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getFeatureBranchCommits(featureBranchName));
		}
		return commits;
	}

	/**
	 * Closes all repositories.
	 */
	public void closeAll() {
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			gitClientForSingleRepo.close();
		}
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return commits with the Jira issue key in their commit message as a list of
	 *         {@link RevCommits}.
	 */
	public List<RevCommit> getCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new LinkedList<RevCommit>();
		}
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommits(jiraIssue));
		}
		return commits;
	}

	/**
	 * Retrieves the Jira issue key from a commit message.
	 *
	 * @param commitMessage
	 *            a commit message that should contain a Jira issue key.
	 * @return extracted Jira issue key or empty String if no Jira issue key could
	 *         be found.
	 *
	 * @issue How to identify the Jira issue key(s) in a commit message?
	 * @alternative This is a very simple method to detect the Jira issue key as the
	 *              first word in the message and should be improved!
	 */
	public static String getJiraIssueKey(String commitMessage) {
		if (commitMessage.isEmpty()) {
			return "";
		}
		String[] split = commitMessage.split("[\\s,:]+");
		return split[0].toUpperCase(Locale.ENGLISH);
	}

	public Set<String> getJiraIssueKeys(String message) {
		Set<String> keys = new LinkedHashSet<String>();
		if (projectKey == null) {
			return keys;
		}
		String baseKey = projectKey.toUpperCase(Locale.ENGLISH);
		String pattern = "(" + baseKey + "-)\\d+";

		String[] words = message.split("[\\s,:]+");
		for (String word : words) {
			word = word.toUpperCase(Locale.ENGLISH);
			if (word.matches(pattern)) {
				keys.add(word);
			}
		}
		return keys;
	}

	/**
	 * @return all commits of the git repository as a list of {@link RevCommits}.
	 */
	public List<RevCommit> getCommits() {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommits());
		}
		return commits;
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return number of commits with the Jira issue key in their commit message.
	 */
	public int getNumberOfCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return 0;
		}
		List<RevCommit> commits = getCommits(jiraIssue);
		return commits.size();
	}

	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param repoUri
	 * @return default branch commits.
	 */
	public List<RevCommit> getDefaultBranchCommits() {
		List<RevCommit> commits = new ArrayList<>();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			commits.addAll(gitClientForSingleRepo.getCommitsFromDefaultBranch());
		}
		return commits;
	}

	/**
	 * Closes all repositories and deletes all local files.
	 */
	public boolean deleteRepositories() {
		boolean isDeleted = true;
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			isDeleted = isDeleted && gitClientForSingleRepo.deleteRepository();
		}
		return isDeleted;
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
			if (gitClientForSingleRepo.getRemoteUri().equals(uri)) {
				return gitClientForSingleRepo;
			}
		}
		return null;
	}

	public List<Ref> getAllRemoteBranches() {
		List<Ref> allRemoteBranches = new ArrayList<>();
		getGitClientsForSingleRepos().forEach(
				gitClientForSingleRepo -> allRemoteBranches.addAll(gitClientForSingleRepo.getRemoteBranches()));
		return allRemoteBranches;
	}

}