package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
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

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * Retrieves commits and code changes (diffs) from one git repository.
 */
public class GitClientForSingleRepository {

	private String repoUri;
	private Git git;
	private String defaultBranchName;
	private List<RevCommit> defaultBranchCommits;
	private String projectKey;
	private String authMethod;
	private String username;
	private String token;
	private GitRepositoryFSManager fsManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitClientForSingleRepository.class);

	public GitClientForSingleRepository(String uri, String defaultBranchName, String projectKey, String authMethod,
			String username, String token) {
		this.projectKey = projectKey;
		this.repoUri = uri;
		this.defaultBranchName = defaultBranchName;
		this.authMethod = authMethod;
		this.username = username;
		this.token = token;
		fsManager = new GitRepositoryFSManager(GitClient.DEFAULT_DIR, projectKey, uri, defaultBranchName);
		pullOrClone();
		defaultBranchCommits = getCommitsFromDefaultBranch();
	}

	public boolean pullOrClone() {
		File directory = new File(fsManager.getDefaultBranchPath());
		File gitDirectory = new File(directory, ".git/");
		if (isGitDirectory(gitDirectory)) {
			if (openRepository(gitDirectory)) {
				if (!pull()) {
					LOGGER.error("Failed Git pull " + directory);
					return false;
				}
			} else {
				LOGGER.error("Could not open repository: " + directory.getAbsolutePath());
				return false;
			}
		} else {
			if (!cloneRepository(directory)) {
				LOGGER.error("Could not clone repository " + repoUri + " to " + directory.getAbsolutePath());
				return false;
			}
		}
		return true;
	}

	private boolean isGitDirectory(File directory) {
		return directory.exists();
	}

	private boolean openRepository(File directory) {
		try {
			git = Git.open(directory);
		} catch (IOException e) {
			LOGGER.error(
					"Git repository could not be opened: " + directory.getAbsolutePath() + "\n\t" + e.getMessage());
			return false;
		} finally {
			close();
		}
		return true;
	}

	private boolean pull() {
		LOGGER.info("Pulling Repository: " + repoUri);
		if (!isPullNeeded()) {
			// LOGGER.info("Repository is up to date: " + repoUri);
			return true;
		}
		try {
			ObjectId oldHead = getRepository().resolve("HEAD^{tree}");
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
						.setRemoveDeletedRefs(true).call();
				LOGGER.info("Fetched branches in " + git.getRepository().getDirectory());
			}
			git.pull().call();
			ObjectId newHead = getRepository().resolve("HEAD^{tree}");
			Diff diffSinceLastPull = getDiff(oldHead, newHead);
			CodeClassPersistenceManager persistenceManager = new CodeClassPersistenceManager(projectKey);
			persistenceManager.maintainCodeClassKnowledgeElements(diffSinceLastPull);
		} catch (GitAPIException | IOException e) {
			LOGGER.error("Issue occurred while pulling from a remote." + "\n\t " + e.getMessage());
			return false;
		}
		LOGGER.info("Pulled from remote in " + git.getRepository().getDirectory());
		return true;
	}

	/**
	 * Based on file timestamp, the method decides if pull is necessary.
	 *
	 * @return decision whether to make or not make the git pull call.
	 */
	private boolean isPullNeeded() {
		String trackerFilename = "condec.pullstamp.";
		Repository repository = this.getRepository();
		File file = new File(repository.getDirectory(), trackerFilename);

		if (!file.isFile()) {
			file.setWritable(true);
			try {
				file.createNewFile();
			} catch (IOException ex) {
				LOGGER.error("Could not create a file, repositories will be fetched on each request.");
			}
			return true;
		}
		if (isRepoOutdated(file.lastModified())) {
			updateFileModifyTime(file);
			return true;
		}
		return false;
	}

	private boolean isRepoOutdated(long lastModified) {
		Date date = new Date();
		long fileLifespan = date.getTime() - lastModified;
		return fileLifespan > GitClient.REPO_OUTDATED_AFTER;
	}

	private boolean updateFileModifyTime(File file) {
		Date date = new Date();
		if (!file.setLastModified(date.getTime())) {
			LOGGER.error("Could not modify a file modify time, repositories will be fetched on each request.");
			return false;
		}
		return true;
	}

	private boolean cloneRepository(File directory) {
		if (repoUri == null || repoUri.isEmpty()) {
			return false;
		}
		try {
			switch (authMethod) {
			case "HTTP":
				git = Git.cloneRepository().setURI(repoUri)
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
						.setDirectory(directory).setCloneAllBranches(true).call();
				break;

			case "GITHUB":
				git = Git.cloneRepository().setURI(repoUri)
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
						.setDirectory(directory).setCloneAllBranches(true).call();
				break;

			case "GITLAB":
				String gitlabUri = repoUri.replaceAll("https://", "https://gitlab-ci-token:" + token + "@");
				git = Git.cloneRepository().setURI(gitlabUri)
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
						.setDirectory(directory).setCloneAllBranches(true).call();
				break;

			default:
				git = Git.cloneRepository().setURI(repoUri).setDirectory(directory).setCloneAllBranches(true).call();
				break;
			}

			setConfig();
		} catch (GitAPIException e) {
			LOGGER.error("Git repository could not be cloned: " + repoUri + " " + directory.getAbsolutePath() + "\n\t"
					+ e.getMessage());
			return false;
		}
		// TODO checkoutDefault branch
		return true;
	}

	private boolean setConfig() {
		Repository repository = this.getRepository();
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
		// TODO Check if this is always correct
		RevCommit firstCommit = commits.get(commits.size() - 1);
		RevCommit lastCommit = commits.get(0);
		return getDiff(firstCommit, lastCommit);
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
		List<RevCommit> squashCommits = getCommits(jiraIssue, false);
		Ref branch = getRef(jiraIssue.getKey());
		List<RevCommit> commits = getCommits(branch);
		if (commits == null) {
			return new Diff();
		}
		commits.removeAll(getCommitsFromDefaultBranch());
		for (RevCommit com : squashCommits) {
			if (!commits.contains(com)) {
				commits.add(com);
			}
		}
		return getDiff(commits);
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
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
		DiffFormatter diffFormatter = getDiffFormater();
		try {
			RevCommit parentCommit = getParent(firstCommit);
			if (parentCommit != null) {
				diffEntries = diffFormatter.scan(parentCommit.getTree(), lastCommit.getTree());
			}
		} catch (IOException e) {
			LOGGER.error("Git diff could not be retrieved. Message: " + e.getMessage());
		}
		Diff diff = getDiffWithChangedFiles(diffEntries, diffFormatter);
		diffFormatter.close();
		return diff;
	}

	private Diff getDiffWithChangedFiles(List<DiffEntry> diffEntries, DiffFormatter diffFormatter) {
		Diff diff = new Diff();
		File directory = getDirectory();
		String baseDirectory = "";
		if (directory != null) {
			baseDirectory = getDirectory().toString().replace(".git", "");
		}
		for (DiffEntry diffEntry : diffEntries) {
			try {
				EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
				ChangedFile changedFile = new ChangedFile(diffEntry, editList, baseDirectory);
				changedFile.setRepoUri(repoUri);
				diff.addChangedFile(changedFile);
			} catch (IOException e) {
				LOGGER.error("Git diff for the file " + diffEntry.getNewPath() + " could not be retrieved. Message: "
						+ e.getMessage());
			}
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
			gitPath = this.getDirectory().getAbsolutePath();
			gitPath = gitPath.substring(0, gitPath.length() - 5);
			diffEntries = this.getGit().diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
		DiffFormatter diffFormatter = getDiffFormater();
		return getDiffWithChangedFiles(diffEntries, diffFormatter);
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
		List<RevCommit> branchUniqueCommits = new ArrayList<RevCommit>();
		List<RevCommit> branchCommits = getCommits(featureBranch);
		RevCommit lastCommonAncestor = null;
		if (defaultBranchCommits == null || defaultBranchCommits.isEmpty()) {
			defaultBranchCommits = getCommitsFromDefaultBranch();
		}
		for (RevCommit commit : branchCommits) {
			if (defaultBranchCommits != null && commit != null && defaultBranchCommits.contains(commit)) {
				LOGGER.info("Found last common commit " + commit.toString());
				lastCommonAncestor = commit;
				break;
			}
			branchUniqueCommits.add(commit);
		}
		if (lastCommonAncestor == null) {
			return Collections.emptyList();
		} else if (branchUniqueCommits.size() > 0) {
			branchUniqueCommits = Lists.reverse(branchUniqueCommits);
		} else {
			branchUniqueCommits = Collections.emptyList();
		}

		return branchUniqueCommits;
	}

	/**
	 * @param featureBranchName
	 *            name of the feature branch.
	 * @return list of unique commits of a <b>feature</b> branch, which do not exist
	 *         in the <b>default</b> branch. Commits are sorted by age, beginning
	 *         with the oldest.
	 */
	public List<RevCommit> getFeatureBranchCommits(String featureBranchName) {
		Ref featureBranch = getBranch(featureBranchName);
		if (null == featureBranch) {
			/**
			 * @issue What is the return value of methods that would normally return a
			 *        collection (e.g. list) with an invalid input parameter?
			 * @alternative Methods with an invalid input parameter return an empty list!
			 * @pro Would prevent a null pointer exception.
			 * @con Is misleading since it is not clear whether the list is empty but has a
			 *      valid input parameter or because of an invalid parameter.
			 * @alternative Methods with an invalid input parameter return null!
			 * @con null values might be intended as result.
			 * @decision Return an emtpy list to compensate for branch being in another
			 *           repository!
			 */
			return Collections.emptyList();
		}
		return getFeatureBranchCommits(featureBranch);
	}

	public Ref getBranch(String featureBranchName) {
		if (featureBranchName == null || featureBranchName.isBlank()) {
			LOGGER.info("Null or empty branch name was passed.");
			return null;
		}
		List<Ref> remoteBranches = getRemoteBranches();
		if (remoteBranches != null) {
			for (Ref branch : remoteBranches) {
				String branchName = branch.getName();
				if (branchName.endsWith("/" + featureBranchName)) {
					return branch;
				}
			}
		}
		LOGGER.info("Could not find branch " + featureBranchName);
		return null;
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
	public File getDirectory() {
		Repository repository = this.getRepository();
		if (repository == null) {
			return null;
		}
		return repository.getDirectory();
	}

	private RevCommit getParent(RevCommit revCommit) {
		RevCommit parentCommit = null;
		try {
			Repository repository = this.getRepository();
			RevWalk revWalk = new RevWalk(repository);
			parentCommit = revWalk.parseCommit(revCommit.getParent(0).getId());
			revWalk.close();
		} catch (Exception e) {
			LOGGER.error("Could not get the parent commit. Message: " + e.getMessage());
		}
		return parentCommit;
	}

	/**
	 * Closes the repository.
	 */
	public void close() {
		if (git == null) {
			return;
		}
		git.getRepository().close();
		git.close();
	}

	/**
	 * Closes the repository and deletes its local files.
	 */
	public boolean deleteRepository() {
		if (git == null || this.getDirectory() == null) {
			return false;
		}
		close();
		File directory = this.getDirectory().getParentFile().getParentFile().getParentFile();
		return deleteFolder(directory);
	}

	private static boolean deleteFolder(File directory) {
		if (directory.listFiles() == null) {
			return false;
		}
		boolean isDeleted = true;
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			} else {
				isDeleted = isDeleted && file.delete();
			}
		}
		return isDeleted && directory.delete();
	}

	/**
	 * Switches git client's directory to feature branch directory, i.e., DOES NOT
	 * go back to the default branch directory. DOES NOT go back to default branch
	 * directory.
	 *
	 * @param featureBranchShortName
	 *            name of the feature branch
	 * @return success or failure boolean
	 */
	public boolean checkoutFeatureBranch(String featureBranchShortName) {
		Ref featureBranch = getBranch(featureBranchShortName);
		if (null == featureBranch) {
			return false;
		}
		return checkoutFeatureBranch(featureBranch);

	}

	/**
	 * Switches git client's directory to commit directory, checks out files in
	 * working dir for the commit. DOES NOT go back to default branch directory.
	 *
	 * @param commit
	 *            name of the feature branch
	 * @return success or failure boolean
	 */
	public boolean checkoutCommit(RevCommit commit) {
		String commitName = commit.getName();

		// will copy default branch folder
		File directory = new File(fsManager.prepareBranchDirectory(commitName));

		return (switchGitDirectory(directory) && checkout(commitName, true));
	}

	private boolean checkout(String branchShortName) {
		return checkout(branchShortName, false);
	}

	/**
	 * Switch git client's directory to feature branch directory. DOES NOT go back
	 * to default branch directory.
	 *
	 * @param featureBranch
	 *            ref of the feature branch
	 * @return success or failure boolean
	 */
	public boolean checkoutFeatureBranch(Ref featureBranch) {
		String[] branchNameComponents = featureBranch.getName().split("/");
		String branchShortName = branchNameComponents[branchNameComponents.length - 1];
		String branchShortNameWithPrefix = featureBranch.getName().replaceFirst("refs/remotes/origin/", "");
		File directory = new File(fsManager.prepareBranchDirectory(branchShortName));

		return switchGitDirectory(directory) && pull() && checkout(branchShortNameWithPrefix);
	}

	/**
	 * @param jiraIssue
	 *            Jira issue. Its key is searched for in commit messages.
	 * @return commits with the Jira issue key in their commit message as a list of
	 *         {@link RevCommits}.
	 */
	public List<RevCommit> getCommits(Issue jiraIssue, boolean isDefaultBranch) {
		if (jiraIssue == null) {
			return new LinkedList<RevCommit>();
		}
		String jiraIssueKey = jiraIssue.getKey();
		List<RevCommit> commitsForJiraIssue = new LinkedList<RevCommit>();
		if (git == null || jiraIssueKey == null) {
			LOGGER.error("Commits cannot be retrieved since git object is null.");
			return commitsForJiraIssue;
		}
		/**
		 * @issue How to get the commits for branches that are not on the master branch?
		 * @alternative Assume that the branch name begins with the JIRA issue key!
		 * @pro This simple rule will work just fine.
		 * @con The rule is too simple. Issues with low key numbers could collect
		 *      branches of much higher issues also. ex. search for "CONDEC-1" would
		 *      find branches beginning with CONDEC-1 BUT as well the ones for issues
		 *      with keys "CONDEC-10", "CONDEC-11" , "CONDEC-100" etc.
		 * @decision Assume the branch name begins with the JIRA issue key and a dot
		 *           character follows directly afterwards!
		 * @pro issues with low key number (ex. CONDEC-1) and higher key numbers (ex.
		 *      CONDEC-1000) will not be confused.
		 */
		Ref branch = getRef(jiraIssueKey);
		List<RevCommit> commits = getCommits(branch, isDefaultBranch);
		for (RevCommit commit : commits) {
			// TODO Improve identification of jira issue key in commit message
			String jiraIssueKeyInCommitMessage = GitClient.getJiraIssueKey(commit.getFullMessage());
			if (jiraIssueKeyInCommitMessage.equalsIgnoreCase(jiraIssueKey)) {
				commitsForJiraIssue.add(commit);
				LOGGER.info("Commit message for key " + jiraIssueKey + ": " + commit.getShortMessage());
			}
		}
		return commitsForJiraIssue;
	}

	/**
	 * @return all commits of the git repository as a list of {@link RevCommits}.
	 */
	public List<RevCommit> getCommits() {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (Ref branch : getRemoteBranches()) {
			/**
			 * @issue All branches will be created in separate file system folders for this
			 *        method's loop. How can this be prevented?
			 * @alternative remove this method completely!
			 * @pro Fetching commits from all branches is not sensible.
			 * @con Fetching commits from all branches may still be needed in some use
			 *      cases.
			 * @pro this method seems to be used only for code testing (TestGetCommits)
			 * @con scraping it would require coding improvement in test code
			 *      (TestGetCommits), but who wants to spend time on that ;-)
			 * @alternative We could check whether the JIRA issue key is part of the branch
			 *              name and - if so - only use the commits from this branch!
			 * @con it is not clear what is meant with this alternative.
			 * @decision release branch folders if possible, so that in best case only one
			 *           folder will be used!
			 * @pro implementation does not seem to be complex at all.
			 * @pro until discussion are not finished, seems like a good trade-off.
			 * @con still some more code will be written. Scraping it, would require coding
			 *      improvement in test code (TestGetCommits).
			 */
			commits.addAll(getCommits(branch, false));
		}
		return commits;
	}

	/*
	 * TODO: This method and getCommits(Issue jiraIssue) need refactoring and deeper
	 * discussions!
	 */
	private Ref getRef(String jiraIssueKey) {
		List<Ref> refs = getAllBranches();
		Ref branch = null;
		for (Ref ref : refs) {
			if (ref.getName().contains(jiraIssueKey)) {
				return ref;
			} else if (ref.getName().equalsIgnoreCase("refs/heads/" + defaultBranchName)) {
				branch = ref;
			}
		}
		return branch;
	}

	/**
	 * @return list of remote branches in repository as {@link Ref}s.
	 */
	public List<Ref> getRemoteBranches() {
		return getRefs(ListBranchCommand.ListMode.REMOTE);
	}

	private List<Ref> getAllBranches() {
		return getRefs(ListBranchCommand.ListMode.ALL);
	}

	private List<Ref> getRefs(ListBranchCommand.ListMode listMode) {
		List<Ref> refs = new ArrayList<Ref>();
		try {
			refs = git.branchList().setListMode(listMode).call();
		} catch (GitAPIException | NullPointerException e) {
			LOGGER.error("Git could not get references. Message: " + e.getMessage());
		}
		return refs;
	}

	public List<RevCommit> getCommitsFromDefaultBranch() {
		Ref defaultBranch = getBranch(defaultBranchName);
		return getCommits(defaultBranch, true);
	}

	private List<RevCommit> getCommits(Ref branch) {
		return getCommits(branch, false);
	}

	private List<RevCommit> getCommits(Ref branch, boolean isDefaultBranch) {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		if (branch == null || fsManager == null) {
			return commits;
		}
		File directory;

		String[] branchNameComponents = branch.getName().split("/");
		String branchShortName = branchNameComponents[branchNameComponents.length - 1];
		String branchShortNameWithPrefix = branch.getName().replaceFirst("refs/remotes/origin/", "");
		branchShortNameWithPrefix = branchShortNameWithPrefix.replaceFirst("refs/heads/", "");
		boolean canReleaseRepoDirectory = false;

		if (isDefaultBranch) {
			String defaultBranchPath = fsManager.getDefaultBranchPath();
			directory = new File(defaultBranchPath);
		} else {
			canReleaseRepoDirectory = !fsManager.isBranchDirectoryInUse(branchShortName);
			directory = new File(fsManager.prepareBranchDirectory(branchShortName));
		}

		if (switchGitDirectory(directory) && pull() && checkout(branchShortNameWithPrefix)) {
			Iterable<RevCommit> iterable = null;
			try {
				iterable = git.log().call();
			} catch (GitAPIException e) {
				LOGGER.error("Git could not get commits for the branch: " + branch.getName() + " Message: "
						+ e.getMessage());
			}
			if (iterable != null) {
				for (RevCommit commit : iterable) {
					if (!commits.contains(commit)) {
						commits.add(commit);
					}
				}
			}
		}
		if (canReleaseRepoDirectory) {
			fsManager.releaseBranchDirectoryNameToTemp(branchShortName);
		}
		switchGitClientBackToDefaultDirectory();
		return commits;
	}

	private boolean checkout(String checkoutObjectName, boolean isCommitWithinBranch) {
		// checkout only remote branch
		String shortCheckoutObjectName = checkoutObjectName.replaceFirst("refs/heads/", "");
		if (!isCommitWithinBranch) {
			String checkoutName = "origin/" + shortCheckoutObjectName;
			try {
				git.checkout().setName(checkoutName).call();
			} catch (GitAPIException | JGitInternalException e) {
				LOGGER.error("Could not checkout " + checkoutName + ". " + e.getMessage());
				return false;
			}
			// create local branch
			if (!createLocalBranchIfNotExists(shortCheckoutObjectName)) {
				LOGGER.error("Could delete and create local branch");
				return false;

			}
		}

		// checkout local branch/commit
		try {
			git.checkout().setName(shortCheckoutObjectName).call();
		} catch (GitAPIException | JGitInternalException e) {

			LOGGER.error("Could not checkout " + shortCheckoutObjectName + ". " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean createLocalBranchIfNotExists(String branchShortName) {
		try {
			git.branchCreate().setName(branchShortName).call();
		} catch (RefAlreadyExistsException e) {
			return true;
		} catch (InvalidRefNameException | RefNotFoundException e) {
			LOGGER.error("Could not create local branch. " + e.getMessage());
			return false;
		} catch (GitAPIException e) {
			LOGGER.error("Could not create local branch. " + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean switchGitDirectory(File gitDirectory) {
		git.close();
		try {
			git = Git.open(gitDirectory);
		} catch (IOException e) {
			LOGGER.error(
					"Could not switch into git directory " + gitDirectory.getAbsolutePath() + "\r\n" + e.getMessage());
			return false;
		}
		return true;
	}

	private void switchGitClientBackToDefaultDirectory() {
		String defaultBranchPath = fsManager.getDefaultBranchPath();
		File directory = new File(defaultBranchPath);
		try {
			git.close();
			git = Git.open(directory);
		} catch (IOException e) {
			LOGGER.error("Git could not get back to default branch. Message: " + e.getMessage());
		}
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
		return repoUri;
	}

	/**
	 * @return name of the default branch (e.g. master).
	 */
	public String getDefaultBranchName() {
		return defaultBranchName;
	}
}
