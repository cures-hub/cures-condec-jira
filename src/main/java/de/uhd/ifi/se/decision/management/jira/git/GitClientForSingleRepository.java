package de.uhd.ifi.se.decision.management.jira.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRef;
import de.uhd.ifi.se.decision.management.jira.git.parser.JiraIssueKeyFromCommitMessageParser;

/**
 * Retrieves commits and code changes (diffs) from one git repository.
 * 
 * @issue How can we assign more than one git repository to a Jira project?
 * @decision Implement class GitClientForSingleRepository with separate git
 *           attribute for each repository!
 * @alternative Use git.remoteAdd() command to add new repos!
 * @con The purpose of `git.remoteAdd()` is completely different: it simply
 *      executes `git remote add` within an existing repository and does not
 *      manage several repositories.
 */
public class GitClientForSingleRepository {

	private Git git;
	private String projectKey;
	private GitRepositoryConfiguration gitRepositoryConfiguration;
	private GitRepositoryFileSystemManager fileSystemManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitClientForSingleRepository.class);

	public GitClientForSingleRepository(String projectKey, GitRepositoryConfiguration gitRepositoryConfiguration) {
		this.projectKey = projectKey;
		this.gitRepositoryConfiguration = gitRepositoryConfiguration;
		fileSystemManager = new GitRepositoryFileSystemManager(projectKey, gitRepositoryConfiguration.getRepoUri());
		fetchOrClone();
	}

	/**
	 * @issue Should we checkout files in the working directory of the local git
	 *        repository?
	 * @decision Do not checkout branches (also not the master branch) into file
	 *           system. Git repositories are bare, i.e. do not have a working
	 *           directory! Use git fetch!
	 * @pro Supports resource and time efficiency (takes less space and time than
	 *      pulling).
	 * @alternative Checkout files into working directory, use git pull!
	 * @pro Git repository content would be human readable on the server.
	 * @con Pulling takes a lot of time and space resources.
	 * 
	 * @return true if fetching or cloning succeeded.
	 */
	public boolean fetchOrClone() {
		File workingDirectory = fileSystemManager.getPathToWorkingDirectory();
		File gitDirectory = new File(workingDirectory, ".git/");
		if (gitDirectory.exists()) {
			if (openRepository(gitDirectory)) {
				if (!fetch()) {
					LOGGER.error("Failed Git fetch " + workingDirectory);
					return false;
				}
			} else {
				LOGGER.error("Could not open repository: " + workingDirectory.getAbsolutePath());
				return false;
			}
		} else {
			if (!cloneRepository(workingDirectory)) {
				LOGGER.error("Could not clone repository " + gitRepositoryConfiguration.getRepoUri() + " to "
						+ workingDirectory.getAbsolutePath());
				return false;
			}
		}
		return true;
	}

	private boolean openRepository() {
		File directory = getGitDirectory();
		return openRepository(directory);
	}

	private boolean openRepository(File directory) {
		if (directory == null) {
			return false;
		}
		try {
			git = Git.open(directory);
		} catch (Exception e) {
			LOGGER.error(
					"Git repository could not be opened: " + directory.getAbsolutePath() + "\n\t" + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * @issue How can we get the new commits since the last fetch? To know the new
	 *        commits is important for trace link maintenance between Jira issues
	 *        and changed files (this includes updating/deleting changed files and
	 *        their links in database).
	 * @decision Store the position of the default branch (e.g. master) before
	 *           fetching and after fetching to determine changes (in particular new
	 *           commits) since last fetch!
	 * @alternative Use FetchResult and TrackingRefUpdate to determine changes (in
	 *              particular new commits) since last fetch!
	 * @con Might work in productive code but did not work in unit testing.
	 * 
	 * @return true if fetching was successful.
	 */
	public boolean fetch() {
		LOGGER.info("Fetching Repository: " + gitRepositoryConfiguration.getRepoUri());
		try {
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				ObjectId oldId = getDefaultBranchPosition();
				UsernamePasswordCredentialsProvider credentialsProvider = gitRepositoryConfiguration
						.getCredentialsProvider();
				FetchCommand fetchCommand = git.fetch().setRemote(remote.getName())
						.setRefSpecs(remote.getFetchRefSpecs()).setRemoveDeletedRefs(true);
				if (credentialsProvider != null) {
					fetchCommand.setCredentialsProvider(credentialsProvider);
				}
				fetchCommand.call();
				ObjectId newId = getDefaultBranchPosition();
				DiffForSingleRef diffSinceLastFetch = getDiffSinceLastFetch(oldId, newId);
				new CodeFileExtractorAndMaintainer(projectKey)
						.maintainChangedFilesInDatabase(new Diff(diffSinceLastFetch));
				LOGGER.info("Fetched branches in " + git.getRepository().getDirectory());
			}
		} catch (GitAPIException e) {
			LOGGER.error("Issue occurred while fetching from a remote." + "\n\t " + e.getMessage());
			return false;
		}
		LOGGER.info("Fetched from remote in " + git.getRepository().getDirectory());
		return true;
	}

	private ObjectId getDefaultBranchPosition() {
		ObjectId objectId = null;
		try {
			objectId = getRepository().resolve(getDefaultRef().getName());
		} catch (RevisionSyntaxException | IOException | NullPointerException e) {
		}
		return objectId;
	}

	public DiffForSingleRef getDiffSinceLastFetch(ObjectId oldObjectId, ObjectId newObjectId) {
		List<RevCommit> newCommits = getCommitsSinceLastFetch(oldObjectId, newObjectId);
		getDefaultBranchCommits().addAll(newCommits);
		if (newCommits.isEmpty()) {
			return new DiffForSingleRef();
		}
		DiffForSingleRef diffSinceLastFetch = getDiff(newCommits.get(0), newCommits.get(newCommits.size() - 1));
		return addCommitsToChangedFiles(diffSinceLastFetch, newCommits);
	}

	public DiffForSingleRef addCommitsToChangedFiles(DiffForSingleRef diff, List<RevCommit> commits) {
		for (RevCommit commit : commits) {
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

	/**
	 * @return commits between the two git objects as a list of {@link RevCommit}s.
	 */
	private List<RevCommit> getCommitsSinceLastFetch(ObjectId oldObjectId, ObjectId newObjectId) {
		if (oldObjectId == null || newObjectId == null) {
			return new ArrayList<>();
		}
		List<RevCommit> newCommits = new ArrayList<>();
		try {
			Iterable<RevCommit> newCommitsIterable = git.log().addRange(oldObjectId, newObjectId).call();
			newCommitsIterable.iterator().forEachRemaining(newCommits::add);
		} catch (Exception e) {
			LOGGER.error("Issue occurred while fetching from a remote." + "\n\t " + e.getMessage());
		}
		return newCommits;
	}

	private boolean cloneRepository(File directory) {
		if (!gitRepositoryConfiguration.isValid()) {
			return false;
		}
		try {
			CloneCommand cloneCommand = Git.cloneRepository().setURI(gitRepositoryConfiguration.getRepoUri())
					.setDirectory(directory).setCloneAllBranches(true).setNoCheckout(true);
			UsernamePasswordCredentialsProvider credentialsProvider = gitRepositoryConfiguration
					.getCredentialsProvider();
			if (credentialsProvider != null) {
				cloneCommand.setCredentialsProvider(credentialsProvider);
			}
			git = cloneCommand.call();
			setConfig();
		} catch (GitAPIException e) {
			LOGGER.error("Git repository could not be cloned: " + gitRepositoryConfiguration.getRepoUri() + " "
					+ directory.getAbsolutePath() + "\n\t" + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean setConfig() {
		Repository repository = getRepository();
		StoredConfig config = repository.getConfig();
		/**
		 * @issue The internal representation of a file might add system dependent new
		 *        line statements, for example CR LF in Windows. How to deal with
		 *        different line endings?
		 * @decision Disable system dependent new line statements!
		 * @pro It is a common approach
		 * @alternative Ignore the problem! The plug-in is hosted on one machine, which
		 *              most likely is not Windows.
		 * @con It is painful for developers to work with local Windows setups.
		 */
		config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, AutoCRLF.TRUE);
		try {
			config.save();
		} catch (IOException e) {
			LOGGER.error("Git configuration could not be set. Message: " + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * @param firstCommit
	 *            first commit on a branch as a RevCommit object.
	 * @param lastCommit
	 *            last commit on a branch as a RevCommit object.
	 * @return {@link DiffForSingleRef} object for a branch of commits indicated by
	 *         the first and last commit on the branch containing the
	 *         {@link ChangedFile}s. Each {@link ChangedFile} is created from a diff
	 *         entry and contains the respective edit list.
	 */
	public DiffForSingleRef getDiff(RevCommit firstCommit, RevCommit lastCommit) {
		DiffFormatter diffFormatter = getDiffFormater();
		List<DiffEntry> diffEntries = getDiffEntries(firstCommit, lastCommit, diffFormatter);
		ObjectId treeId = lastCommit.getTree().getId();
		DiffForSingleRef diff = getDiffWithChangedFiles(diffEntries, diffFormatter, treeId);
		diffFormatter.close();
		return diff;
	}

	/**
	 * @issue How can we get the Jira issues that the diff entries (i.e. changed
	 *        files) were committed to?
	 * @decision We iterate over all commits in a git repository, extract the diff
	 *           entries (i.e. changed files) and store all commits that changed a
	 *           file as an attribute of the ChangedFile class! We read the Jira
	 *           issue keys from the commit messages of all commits of a ChangedFile
	 *           object!
	 * @alternative We could use "git blame" to get the commits that changed a file.
	 * @con Git blame is hard to use since a treeWalk path is needed for it.
	 */
	public List<DiffEntry> getDiffEntries(RevCommit firstCommit, RevCommit lastCommit, DiffFormatter diffFormatter) {
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
		try {
			if (firstCommit.getParentCount() > 0) {
				RevCommit parentCommit = getParent(firstCommit);
				diffEntries = diffFormatter.scan(parentCommit.getTree(), lastCommit.getTree());
			}
		} catch (IOException e) {
			LOGGER.debug("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		return diffEntries;
	}

	public List<DiffEntry> getDiffEntries(RevCommit commit) {
		DiffFormatter diffFormatter = getDiffFormater();
		List<DiffEntry> diffEntries = getDiffEntries(commit, commit, diffFormatter);
		diffFormatter.close();
		return diffEntries;
	}

	private DiffForSingleRef getDiffWithChangedFiles(List<DiffEntry> diffEntries, DiffFormatter diffFormatter,
			ObjectId treeId) {
		DiffForSingleRef diff = new DiffForSingleRef();
		for (DiffEntry diffEntry : diffEntries) {
			try {
				EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
				ChangedFile changedFile = new ChangedFile(diffEntry, editList, treeId, getRepository());
				changedFile.setProject(projectKey);
				changedFile.setRepoUri(gitRepositoryConfiguration.getRepoUri());
				diff.addChangedFile(changedFile);
			} catch (IOException e) {
				LOGGER.error("Git diff for the file " + diffEntry.getNewPath() + " could not be retrieved. Message: "
						+ e.getMessage());
			}
		}
		return diff;
	}

	private DiffFormatter getDiffFormater() {
		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		Repository repository = getRepository();
		diffFormatter.setRepository(repository);
		diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
		diffFormatter.setDetectRenames(true);
		return diffFormatter;
	}

	/**
	 * @return jgit repository object.
	 */
	public Repository getRepository() {
		if (git == null) {
			// TODO Avoid returning null. Use Optional<> instead
			return null;
		}
		return git.getRepository();
	}

	/**
	 * @return path to the .git folder as a File object.
	 */
	public File getGitDirectory() {
		Repository repository = getRepository();
		if (repository == null) {
			return null;
		}
		return repository.getDirectory();
	}

	private RevCommit getParent(RevCommit revCommit) {
		if (revCommit.getParentCount() > 0) {
			return revCommit.getParent(0);
		}
		return null;
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return commits with the Jira issue key in their commit message as a list of
	 *         {@link RevCommits}.
	 */
	public List<RevCommit> getCommits(Issue jiraIssue, boolean isDefaultBranch) {
		if (git == null || jiraIssue == null || jiraIssue.getKey() == null) {
			return new ArrayList<RevCommit>();
		}
		String jiraIssueKey = jiraIssue.getKey();
		List<RevCommit> commitsForJiraIssue = new ArrayList<RevCommit>();
		/**
		 * @issue How to get the commits for branches that are not on the master branch?
		 * @alternative Assume that the branch name begins with the Jira issue key!
		 * @pro This simple rule will work just fine.
		 * @con The rule is too simple. Issues with low key numbers could collect
		 *      branches of much higher issues also. ex. search for "CONDEC-1" would
		 *      find branches beginning with CONDEC-1 BUT as well the ones for issues
		 *      with keys "CONDEC-10", "CONDEC-11" , "CONDEC-100" etc.
		 * @decision Assume the branch name begins with the Jira issue key and a dot
		 *           character follows directly afterwards!
		 * @pro issues with low key number (ex. CONDEC-1) and higher key numbers (ex.
		 *      CONDEC-1000) will not be confused.
		 */
		List<RevCommit> commits = new ArrayList<RevCommit>();
		if (isDefaultBranch) {
			commits = getDefaultBranchCommits();
		} else {
			Ref branch = getRef(jiraIssueKey);
			commits = getCommits(branch);
		}
		for (RevCommit commit : commits) {
			String jiraIssueKeyInCommitMessage = JiraIssueKeyFromCommitMessageParser
					.getFirstJiraIssueKey(commit.getFullMessage());
			if (jiraIssueKeyInCommitMessage.equalsIgnoreCase(jiraIssueKey)) {
				commitsForJiraIssue.add(commit);
				LOGGER.info("Commit message for key " + jiraIssueKey + ": " + commit.getShortMessage());
			}
		}

		return commitsForJiraIssue;
	}

	private Ref getRef(String branchName) {
		List<Ref> refs = getRefs();
		for (Ref ref : refs) {
			if (ref.getName().contains(branchName)) {
				return ref;
			}
		}
		return getDefaultRef();
	}

	public Ref getDefaultRef() {
		List<Ref> refs = getRefs();
		for (Ref ref : refs) {
			if (ref.getName().contains(gitRepositoryConfiguration.getDefaultBranch())) {
				return ref;
			}
		}
		return null;
	}

	/**
	 * @return remote {@link Ref} objects in the repository.
	 */
	public List<Ref> getRefs() {
		List<Ref> refs = new ArrayList<Ref>();
		openRepository();
		try {
			refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
		} catch (GitAPIException | NullPointerException e) {
			LOGGER.error("Git could not get references. Message: " + e.getMessage());
		}
		refs.removeIf(ref -> !ref.getName().contains("remote"));
		return refs;
	}

	public List<RevCommit> getDefaultBranchCommits() {
		Ref defaultBranch = getDefaultRef();
		return getCommits(defaultBranch);
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

	public List<RevCommit> getCommits(Ref branch) {
		if (branch == null || fileSystemManager == null) {
			return new ArrayList<RevCommit>();
		}

		List<RevCommit> commits = new ArrayList<>();

		try {
			openRepository();
			ObjectId commitId = getRepository().resolve(branch.getName());
			Iterable<RevCommit> iterable = git.log().add(commitId).call();
			commits = Lists.newArrayList(iterable.iterator());
		} catch (RevisionSyntaxException | IOException | GitAPIException | NullPointerException e) {

		}
		return commits;
	}

	/**
	 * @param branchName
	 * @return all changes on branches that contain the name.
	 */
	public Diff getDiff(String branchName) {
		Diff diff = new Diff();
		List<Ref> refsWithName = getRefs().stream()
				.filter(ref -> ref.getName().toUpperCase().contains(branchName.toUpperCase()))
				.collect(Collectors.toList());
		for (Ref ref : refsWithName) {
			DiffForSingleRef diffForSingleRef = getDiff(ref);
			diff.add(diffForSingleRef);
		}
		return diff;
	}

	public DiffForSingleRef getDiff(Ref ref) {
		DiffForSingleRef branch = new DiffForSingleRef();
		branch.setProjectKey(projectKey);
		branch.setRef(ref);
		List<RevCommit> commits = getFeatureBranchCommits(ref);
		branch.setCommits(commits);
		branch.setRepoUri(getRemoteUri());

		if (!commits.isEmpty()) {
			RevCommit baseCommit = commits.get(0);
			RevCommit lastFeatureBranchCommit = commits.get(commits.size() - 1);
			branch.add(getDiff(baseCommit, lastFeatureBranchCommit));
		}
		return branch;
	}

	/**
	 * @return git object.
	 */
	public Git getGit() {
		return git;
	}

	/**
	 * @return remote Uniform Resource Identifier (URI) of the git repository as a
	 *         String.
	 */
	public String getRemoteUri() {
		return gitRepositoryConfiguration.getRepoUri();
	}

	/**
	 * @return name of the default branch (e.g. master).
	 */
	public String getDefaultBranchName() {
		return gitRepositoryConfiguration.getDefaultBranch();
	}

	/**
	 * @return file system manager responsible to create and delete the directory
	 *         that the repository is cloned to.
	 */
	public GitRepositoryFileSystemManager getFileSystemManager() {
		return fileSystemManager;
	}
}
