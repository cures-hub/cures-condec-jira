package de.uhd.ifi.se.decision.management.jira.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.DecisionKnowledgeElementInCodeComment;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRef;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Retrieves commits and code changes ({@link DiffForSingleRef}s) from one or
 * more git repositories. Modifying files is not supported.
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
	public static Map<String, GitClient> instances = new HashMap<>();

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
			LOGGER.error("GitClient could not clone or fetch repo(s) for project: " + projectKey);
			return null;
		}
		if (extractAllCodeKnowledge) {
			Diff diff = gitClient.getDiffOfEntireDefaultBranch();
			new CodeFileExtractorAndMaintainer(projectKey).maintainChangedFilesInDatabase(diff);
		}
		return gitClient;
	}

	private GitClient(String projectKey) {
		this.projectKey = projectKey;
		gitClientsForSingleRepos = new ArrayList<GitClientForSingleRepository>();
		for (GitRepositoryConfiguration gitRepositoryConfiguration : ConfigPersistenceManager
				.getGitConfiguration(projectKey).getGitRepoConfigurations()) {
			if (gitRepositoryConfiguration.isValid()) {
				gitClientsForSingleRepos.add(new GitClientForSingleRepository(projectKey, gitRepositoryConfiguration));
			}
		}
	}

	private boolean fetchOrCloneRepositories() {
		boolean isEverythingUpToDate = true;
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			isEverythingUpToDate = isEverythingUpToDate && gitClientForSingleRepo.fetchOrClone();
		}
		return isEverythingUpToDate;
	}

	/**
	 * @return {@link Diff} object for all commits on the default branch(es)
	 *         containing the {@link ChangedFile}s. Each {@link ChangedFile} is
	 *         created from a diff entry and contains the respective edit list and a
	 *         reference to all commits that changed the file.
	 */
	public Diff getDiffOfEntireDefaultBranch() {
		Diff diff = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diff.add(gitClientForSingleRepo.getDiffOfEntireDefaultBranch());
		}
		return diff;
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
	 *            e.g. Jira issue key or Jira project key.
	 * @return all changes on branches that contain the name that are NOT on the
	 *         default branch.
	 */
	public Diff getDiffForFeatureBranchWithName(String branchName) {
		if (branchName == null) {
			return new Diff();
		}
		Diff diff = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diff.addAll(gitClientForSingleRepo.getDiffForFeatureBranchWithName(branchName));
		}
		return diff;
	}

	/**
	 * @param jiraIssue
	 *            a Jira issue such as work item/development task/requirement. The
	 *            feature branch name or commit message on default branch needs to
	 *            have the key in it.
	 * @return {@link Diff} object for a Jira issue containing the
	 *         {@link ChangedFile}s from both the default branch(es) and the feature
	 *         branches that have the Jira issue key in the branch name. Each
	 *         {@link ChangedFile} is created from a diff entry and contains the
	 *         respective edit list.
	 */
	public Diff getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new Diff();
		}

		Diff diffForJiraIssue = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diffForJiraIssue
					.addAll(gitClientForSingleRepo.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(jiraIssue));
		}
		return diffForJiraIssue;
	}

	/**
	 * @param jiraIssue
	 *            a Jira issue such as work item/development task/requirement.
	 * @return {@link Diff} object for a Jira issue containing the
	 *         {@link ChangedFile}s from the default branch(es). The commit messages
	 *         on the default branch(es) need to contain the Jira issue key. Each
	 *         {@link ChangedFile} is created from a diff entry and contains the
	 *         respective edit list.
	 */
	public Diff getDiffForJiraIssueOnDefaultBranch(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new Diff();
		}

		Diff diffForJiraIssueOnDefaultBranches = new Diff();
		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			diffForJiraIssueOnDefaultBranches.add(gitClientForSingleRepo.getDiffOnDefaultBranch(jiraIssue));
		}
		return diffForJiraIssueOnDefaultBranches;
	}

	public Diff getDiffOfEntireDefaultBranchFromKnowledgeGraph() {
		Diff diffForDefaultBranches = new Diff();
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		filterSettings.setOnlyDecisionKnowledgeShown(true);
		filterSettings.setDocumentationLocations(List.of("Code"));
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> codeElements = filteringManager.getElementsMatchingFilterSettings();

		for (GitClientForSingleRepository gitClientForSingleRepo : getGitClientsForSingleRepos()) {
			String repoUri = gitClientForSingleRepo.getRemoteUri();
			List<DecisionKnowledgeElementInCodeComment> elementsFromRepo = new ArrayList<>();
			for (KnowledgeElement element : codeElements) {
				if (!(element instanceof DecisionKnowledgeElementInCodeComment)) {
					continue;
				}
				if (((DecisionKnowledgeElementInCodeComment) element).getRepoUri().equals(repoUri)) {
					elementsFromRepo.add((DecisionKnowledgeElementInCodeComment) element);
				}
			}
			Collections.sort(elementsFromRepo);
			DiffForSingleRef branch = new DiffForSingleRef(gitClientForSingleRepo.getDefaultRef(), elementsFromRepo,
					new ArrayList<>());
			branch.setRepoUri(gitClientForSingleRepo.getRemoteUri());
			diffForDefaultBranches.add(branch);
		}
		return diffForDefaultBranches;
	}

}