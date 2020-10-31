package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
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
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.CommitMessageParser;
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
		fsManager = new GitRepositoryFSManager(GitClient.DEFAULT_DIR, projectKey, uri);
		pullOrClone();
		defaultBranchCommits = getDefaultBranchCommits();
	}

	public boolean pullOrClone() {
		File directory = new File(fsManager.getPathToRepositoryInFileSystem());
		File gitDirectory = new File(directory, ".git/");
		if (gitDirectory.exists()) {
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
		close();
		return true;
	}

	private boolean openRepository() {
		File directory = getDirectory();
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
		Repository repository = getRepository();
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
			CloneCommand cloneCommand = Git.cloneRepository().setURI(repoUri).setDirectory(directory)
					.setCloneAllBranches(true);
			UsernamePasswordCredentialsProvider credentialsProvider = getCredentialsProvider();
			if (credentialsProvider != null) {
				cloneCommand.setCredentialsProvider(credentialsProvider);
			}
			git = cloneCommand.call();
			setConfig();
		} catch (GitAPIException e) {
			LOGGER.error("Git repository could not be cloned: " + repoUri + " " + directory.getAbsolutePath() + "\n\t"
					+ e.getMessage());
			return false;
		} finally {
			close();
		}
		return true;
	}

	private UsernamePasswordCredentialsProvider getCredentialsProvider() {
		switch (authMethod) {
		case "HTTP":
			return new UsernamePasswordCredentialsProvider(username, token);
		case "GITHUB":
			return new UsernamePasswordCredentialsProvider(token, "");
		case "GITLAB":
			return new UsernamePasswordCredentialsProvider(username, token);
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
	 * Closes the repository.
	 */
	public void close() {
		if (git == null) {
			return;
		}
		getRepository().close();
		git.close();
	}

	/**
	 * Closes the repository and deletes its local files.
	 */
	public boolean deleteRepository() {
		if (git == null || getDirectory() == null) {
			return false;
		}
		close();
		File directory = getDirectory().getParentFile().getParentFile();
		return deleteFolder(directory);
	}

	private static boolean deleteFolder(File directory) {
		if (directory.listFiles() == null) {
			return true;
		}
		boolean isDeleted = true;
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			} else {
				try {
					file.delete();
					FileUtils.delete(file,
							FileUtils.RECURSIVE | FileUtils.RETRY | FileUtils.SKIP_MISSING | FileUtils.IGNORE_ERRORS);
				} catch (IOException e) {
					System.out.print(file.getAbsolutePath() + " " + e);
					isDeleted = false;
				}
			}
		}
		try {
			directory.delete();
			FileUtils.delete(directory,
					FileUtils.RECURSIVE | FileUtils.RETRY | FileUtils.SKIP_MISSING | FileUtils.IGNORE_ERRORS);
		} catch (IOException e) {
			System.out.print(directory.getAbsolutePath() + " " + e);
			isDeleted = false;
		}
		return isDeleted;
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
		Ref branch = getRef(jiraIssueKey);
		List<RevCommit> commits = getCommits(branch);
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
	private Ref getRef(String jiraIssueKey) {
		List<Ref> refs = getAllBranches();
		for (Ref ref : refs) {
			if (ref.getName().contains(jiraIssueKey)) {
				return ref;
			} else if (ref.getName().equalsIgnoreCase("refs/heads/" + defaultBranchName)) {
				return ref;
			}
		}
		return null;
	}

	public Ref getDefaultBranch() {
		List<Ref> refs = getAllBranches();
		for (Ref ref : refs) {
			if (ref.getName().equalsIgnoreCase("refs/heads/" + defaultBranchName)) {
				return ref;
			}
		}
		return null;
	}

	/**
	 * @return remote branches in repository as a list of {@link Ref}s.
	 */
	public List<Ref> getRemoteBranches() {
		return getRefs(ListBranchCommand.ListMode.REMOTE);
	}

	private List<Ref> getAllBranches() {
		return getRefs(ListBranchCommand.ListMode.ALL);
	}

	private List<Ref> getRefs(ListBranchCommand.ListMode listMode) {
		List<Ref> refs = new ArrayList<Ref>();
		openRepository();
		try {
			refs = git.branchList().setListMode(listMode).call();
		} catch (GitAPIException | NullPointerException e) {
			System.out.println(e.getMessage());
			LOGGER.error("Git could not get references. Message: " + e.getMessage());
		}
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
		if (branch == null || fsManager == null) {
			return new ArrayList<RevCommit>();
		}

		List<RevCommit> commits = new ArrayList<>();

		try {
			openRepository();
			ObjectId commitId = getRepository().resolve(branch.getName());
			Iterable<RevCommit> iterable = git.log().add(commitId).call();
			commits = Lists.newArrayList(iterable.iterator());
		} catch (RevisionSyntaxException | IOException | GitAPIException e) {

		}
		return commits;
	}

	/**
	 * @param branch
	 *            as a {@link Ref} object.
	 * @return e.g. "TEST-4.transcriberBranch" instead of
	 *         "refs/remotes/origin/TEST-4.transcriberBranch"
	 */
	public static String simplifyBranchName(Ref branch) {
		String[] branchNameComponents = branch.getName().split("/");
		return branchNameComponents[branchNameComponents.length - 1];
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
