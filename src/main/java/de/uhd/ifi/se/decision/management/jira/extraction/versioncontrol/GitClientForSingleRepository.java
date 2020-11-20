package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
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
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.CommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * FIXME Investigate alternative for the following decision problem:
 * 
 * @issue How can we assign more than one git repository to a Jira project?
 * @decision Implement class GitClientForSingleRepository with separate git
 *           attribute for each repository!
 * @alternative Use git.remoteAdd() command to add new repos!
 * @con The purpose of `git.remoteAdd()` is completely different – it simply
 *      executes `git remote add` within an existing repository and does not
 *      manage several repositories
 *
 *              Retrieves commits and code changes (diffs) from one git
 *              repository.
 */
public class GitClientForSingleRepository {

	private Git git;
	private List<RevCommit> defaultBranchCommits;
	private String projectKey;
	private GitRepositoryInformation gitInfo;
	private GitRepositoryFileSystemManager fileSystemManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitClientForSingleRepository.class);

	public GitClientForSingleRepository(String projectKey, GitRepositoryInformation gitInfo) {
		this.projectKey = projectKey;
		this.gitInfo = gitInfo;
		fileSystemManager = new GitRepositoryFileSystemManager(projectKey, gitInfo.getRepoUri());
		fetchOrClone();
		defaultBranchCommits = getDefaultBranchCommits();
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
				LOGGER.error("Could not clone repository " + this.gitInfo.getRepoUri() + " to " + workingDirectory.getAbsolutePath());
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
		try {
			git = Git.open(directory);
		} catch (IOException e) {
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
		LOGGER.info("Fetching Repository: " + this.gitInfo.getRepoUri());
		try {
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				ObjectId oldId = getDefaultBranchPosition();
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
						.setRemoveDeletedRefs(true).call();
				ObjectId newId = getDefaultBranchPosition();
				Diff diffSinceLastFetch = getDiffSinceLastFetch(oldId, newId);
				CodeClassPersistenceManager persistenceManager = new CodeClassPersistenceManager(projectKey);
				persistenceManager.maintainChangedFilesInDatabase(diffSinceLastFetch);
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
			objectId = getRepository().resolve(getDefaultBranch().getName());
		} catch (RevisionSyntaxException | IOException | NullPointerException e) {
		}
		return objectId;
	}

	public Diff getDiffSinceLastFetch(ObjectId oldObjectId, ObjectId newObjectId) {
		List<RevCommit> newCommits = getCommitsSinceLastFetch(oldObjectId, newObjectId);
		if (newCommits.isEmpty()) {
			return new Diff();
		}
		Diff diffSinceLastFetch = getDiff(newCommits.get(0), newCommits.get(newCommits.size() - 1));
		return addCommitsToChangedFiles(diffSinceLastFetch, newCommits);
	}

	public Diff addCommitsToChangedFiles(Diff diff, List<RevCommit> commits) {
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
		if (this.gitInfo.getRepoUri() == null || this.gitInfo.getRepoUri().isEmpty()) {
			return false;
		}
		try {
			CloneCommand cloneCommand = Git.cloneRepository().setURI(this.gitInfo.getRepoUri()).setDirectory(directory)
					.setCloneAllBranches(true).setNoCheckout(true);
			UsernamePasswordCredentialsProvider credentialsProvider = getCredentialsProvider();
			if (credentialsProvider != null) {
				cloneCommand.setCredentialsProvider(credentialsProvider);
			}
			git = cloneCommand.call();
			setConfig();
			new CodeClassPersistenceManager(projectKey).extractAllChangedFiles();
		} catch (GitAPIException e) {
			LOGGER.error("Git repository could not be cloned: " + this.gitInfo.getRepoUri() + " " + directory.getAbsolutePath() + "\n\t"
					+ e.getMessage());
			return false;
		}
		return true;
	}

	private UsernamePasswordCredentialsProvider getCredentialsProvider() {
		switch (this.gitInfo.getAuthMethod()) {
		case "HTTP":
			return new UsernamePasswordCredentialsProvider(this.gitInfo.getUsername(), this.gitInfo.getToken());
		case "GITHUB":
			return new UsernamePasswordCredentialsProvider(this.gitInfo.getToken(), "");
		case "GITLAB":
			return new UsernamePasswordCredentialsProvider(this.gitInfo.getUsername(), this.gitInfo.getToken());
		default:
			return null;
		}
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
	 * @return {@link Diff} object for a branch of commits indicated by the first
	 *         and last commit on the branch containing the {@link ChangedFile}s.
	 *         Each {@link ChangedFile} is created from a diff entry and contains
	 *         the respective edit list.
	 */
	public Diff getDiff(RevCommit firstCommit, RevCommit lastCommit) {
		DiffFormatter diffFormatter = getDiffFormater();
		List<DiffEntry> diffEntries = getDiffEntries(firstCommit, lastCommit, diffFormatter);
		ObjectId treeId = null;
		try {
			treeId = getRepository().resolve(lastCommit.getName() + "^{tree}");
		} catch (RevisionSyntaxException | IOException e) {
			LOGGER.error("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		Diff diff = getDiffWithChangedFiles(diffEntries, diffFormatter, treeId);
		diffFormatter.close();
		return diff;
	}

	/**
	 * @issue How can we get the Jira issues that the diff entries were committed
	 *        to?
	 */
	public List<DiffEntry> getDiffEntries(RevCommit firstCommit, RevCommit lastCommit, DiffFormatter diffFormatter) {
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
		try {
			RevCommit parentCommit = getParent(firstCommit);
			if (parentCommit != null) {
				diffEntries = diffFormatter.scan(parentCommit.getTree(), lastCommit.getTree());
			}
		} catch (IOException e) {
			LOGGER.error("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		return diffEntries;
	}

	public List<DiffEntry> getDiffEntries(RevCommit commit) {
		DiffFormatter diffFormatter = getDiffFormater();
		List<DiffEntry> diffEntries = getDiffEntries(commit, commit, diffFormatter);
		diffFormatter.close();
		return diffEntries;
	}

	private Diff getDiffWithChangedFiles(List<DiffEntry> diffEntries, DiffFormatter diffFormatter, ObjectId treeId) {
		Diff diff = new Diff();
		for (DiffEntry diffEntry : diffEntries) {
			try {
				EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
				ChangedFile changedFile = new ChangedFile(diffEntry, editList, treeId, getRepository());
				changedFile.setProject(projectKey);
				changedFile.setRepoUri(this.gitInfo.getRepoUri());
				diff.addChangedFile(changedFile);
			} catch (IOException e) {
				LOGGER.error("Git diff for the file " + diffEntry.getNewPath() + " could not be retrieved. Message: "
						+ e.getMessage());
			}
		}
		return diff;
	}

	/**
	 * @return {@link Diff} object containing the {@link ChangedFile}s. Each
	 *         {@link ChangedFile} is created from a diff entry and contains the
	 *         respective edit list.
	 */
	public Diff getDiff(ObjectId oldHead, ObjectId newHead) {
		if (oldHead.equals(newHead)) {
			return new Diff();
		}
		ObjectReader reader = this.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
		String gitPath = "";
		List<DiffEntry> diffEntries = new ArrayList<>();
		try {
			oldTreeIter.reset(reader, oldHead);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, newHead);
			gitPath = getGitDirectory().getAbsolutePath();
			gitPath = gitPath.substring(0, gitPath.length() - 5);
			diffEntries = getGit().diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
		} catch (IOException | GitAPIException e) {
			LOGGER.error("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		DiffFormatter diffFormatter = getDiffFormater();
		ObjectId treeId = null;
		try {
			treeId = getRepository().resolve(newHead.getName() + "^{tree}");
		} catch (RevisionSyntaxException | IOException e) {
			LOGGER.error("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		return getDiffWithChangedFiles(diffEntries, diffFormatter, treeId);
	}

	private DiffFormatter getDiffFormater() {
		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		Repository repository = this.getRepository();
		if (repository == null) {
			return diffFormatter;
		}
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
		RevCommit parentCommit = null;
		try {
			Repository repository = getRepository();
			RevWalk revWalk = new RevWalk(repository);
			parentCommit = revWalk.parseCommit(revCommit.getParent(0).getId());
			revWalk.close();
		} catch (Exception e) {
			LOGGER.error("Could not get the parent commit. Message: " + e.getMessage());
		}
		return parentCommit;
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
			Ref branch = getBranch(jiraIssueKey);
			commits = getCommits(branch);
		}
		for (RevCommit commit : commits) {
			String jiraIssueKeyInCommitMessage = CommitMessageParser.getFirstJiraIssueKey(commit.getFullMessage());
			if (jiraIssueKeyInCommitMessage.equalsIgnoreCase(jiraIssueKey)) {
				commitsForJiraIssue.add(commit);
				LOGGER.info("Commit message for key " + jiraIssueKey + ": " + commit.getShortMessage());
			}
		}

		return commitsForJiraIssue;
	}

	/*
	 * TODO: This method and getCommits(Issue jiraIssue) need refactoring and deeper
	 * discussions!
	 */
	private Ref getBranch(String branchName) {
		List<Ref> refs = getBranches();
		for (Ref ref : refs) {
			if (ref.getName().contains(branchName)) {
				return ref;
			}
		}
		return getDefaultBranch();
	}

	public Ref getDefaultBranch() {
		List<Ref> refs = getBranches();
		for (Ref ref : refs) {
			if (ref.getName().contains(this.gitInfo.getDefaultBranchName())) {
				return ref;
			}
		}
		return null;
	}

	/**
	 * @return remote branches in repository as a list of {@link Ref} objects.
	 */
	public List<Ref> getBranches() {
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
		if (defaultBranchCommits == null || defaultBranchCommits.isEmpty()) {
			Ref defaultBranch = getDefaultBranch();
			defaultBranchCommits = getCommits(defaultBranch);
		}
		return defaultBranchCommits;
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
		return this.gitInfo.getRepoUri();
	}

	/**
	 * @return name of the default branch (e.g. master).
	 */
	public String getDefaultBranchName() {
		return this.gitInfo.getDefaultBranchName();
	}

	/**
	 * @return file system manager responsible to create and delete the directory
	 *         that the repository is cloned to.
	 */
	public GitRepositoryFileSystemManager getFileSystemManager() {
		return fileSystemManager;
	}
}
