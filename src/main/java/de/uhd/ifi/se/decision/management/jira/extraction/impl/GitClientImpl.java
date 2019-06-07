package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryFSManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * @issue How to access commits related to a JIRA issue?
 * @decision Only use jGit.
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for JIRA plugin
 * were used to access git repositories.
 * @con An application link and oAuth is needed to call REST API on Java side.
 * 
 * This implementation works well only with configuration for one remote git server.
 * Multiple instances of this class are "thread-safe" in the limited way that
 * the checked out branch files are stored in dedicated branch folders and can be read,
 * modifing files is not safe and not supported.
 */
public class GitClientImpl implements GitClient {

	private Git git;
	private boolean repoInitSuccess = false; // will be later made readable with upcoming features
	private Ref defaultBranch; // TODO: should come from configuration of the project
	private List<RevCommit> defaultBranchCommits; // will be later needed for upcoming features
	private GitRepositoryFSManager fsManager;
	private static final Logger LOGGER = LoggerFactory.getLogger(GitClientImpl.class);

	public GitClientImpl() {
	}

	public GitClientImpl(File directory) {
		repoInitSuccess = initRepository(directory);
	}

	public GitClientImpl(String uri, String defaultDirectory, String projectKey) {
		// TODO: the last parameter should be a setting retrievable with ConfigPersistenceM
		repoInitSuccess = pullOrCloneRepository(projectKey, defaultDirectory, uri, "develop");
	}

	public GitClientImpl(String uri, String projectKey) {
		// TODO: the last parameter should be a setting retrievable with ConfigPersistenceManager
		repoInitSuccess = pullOrCloneRepository(projectKey, DEFAULT_DIR, uri, "develop");
	}

	public GitClientImpl(String projectKey) {
		String uri = ConfigPersistenceManager.getGitUri(projectKey);
		// TODO: the last parameter should be a setting retrievable with ConfigPersistenceManager
		repoInitSuccess = pullOrCloneRepository(projectKey, DEFAULT_DIR, uri, "develop");
	}

	private boolean pullOrCloneRepository(String projectKey, String defaultDirectory, String uri, String defaultBranchFolderName) {
		fsManager = new GitRepositoryFSManager(defaultDirectory, projectKey, uri, defaultBranchFolderName);
		File directory = new File(fsManager.getDefaultBranchPath());
		return pullOrClone(uri, directory);
	}

	private boolean pullOrClone(String uri, File directory) {
		if (isGitDirectory(directory)) {
			if (openRepository(directory)) {
				if (!pull()) {
					LOGGER.error("failed Git pull " + directory);
					return false;
				}
			} else {
				LOGGER.error("Could not open repository: " + directory.getAbsolutePath());
				return false;
			}
		} else {
			if (!cloneRepository(uri, directory)) {
				LOGGER.error("Could not clone repository " + uri + " to " + directory.getAbsolutePath());
				return false;
			}
		}

		// get name of current branch, should be later replaced by project setting
		defaultBranch = getCurrentBranch();
		if (defaultBranch == null) {
			return false;
		}
		defaultBranchCommits = getCommitsFromDefaultBranch();
		return true;
	}

	private boolean isGitDirectory(File directory) {
		File gitDir = new File(directory, ".git/");
		return directory.exists() && (gitDir.isDirectory());
	}

	private Ref getCurrentBranch() {
		String branchName = null;
		Ref branch = null;
		Repository repository;

		try {
			repository = this.getRepository();
		} catch (Exception e) {
			LOGGER.error("getRepository: " + e.getMessage());
			return null;
		}

		if (repository == null) {
			LOGGER.error("Git repository does not seem to exist");
			return null;
		}
		try {
			branchName = repository.getFullBranch();
			branch = repository.findRef(branchName);
			if (branch == null) {
				LOGGER.error("Git repository does not seem to be on a branch");
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Git client has thrown error while getting branch name. " + e.getMessage());
			return null;
		}
		return branch;
	}

	private boolean openRepository(File directory) {
		try {
			git = Git.open(directory);
		} catch (IOException e) {
			LOGGER.error("Git repository could not be opened: " + directory.getAbsolutePath() +
					"\n\t" + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean pull() {
		try {
			git.pull().call();
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs()).call();
			}
		} catch (GitAPIException e) {
			LOGGER.error("Issue occurred while pulling from a remote." +
					"\n\t" + e.getMessage());
			return false;
		}
		return true;
	}

	private boolean cloneRepository(String uri, File directory) {
		if (uri == null || uri.isEmpty()) {
			return false;
		}
		try {
			git = Git.cloneRepository().setURI(uri).setDirectory(directory).setCloneAllBranches(true).call();
			setConfig();
		} catch (GitAPIException e) {
			LOGGER.error("Git repository could not be cloned: " + uri + " " + directory.getAbsolutePath() +
					"\n\t" + e.getMessage());
			return false;
		}
		// TODO checkoutDefault branch
		return true;
	}

	private boolean initRepository(File directory) {
		try {
			git = Git.init().setDirectory(directory).call();
		} catch (IllegalStateException | GitAPIException e) {
			LOGGER.error("Bare git repository could not be initiated: " + directory.getAbsolutePath());
			return false;
		}
		return true;
	}

	private boolean setConfig() {
		Repository repository = this.getRepository();
		StoredConfig config = repository.getConfig();
		// @issue The internal representation of a file might add system dependent new
		// line statements, for example CR LF in Windows
		// @decision Disable system dependent new line statements
		config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, AutoCRLF.TRUE);
		try {
			config.save();
		} catch (IOException e) {
			LOGGER.error("Git configuration could not be set. Message: " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public Map<DiffEntry, EditList> getDiff(List<RevCommit> commits) {
		if (commits == null || commits.size() == 0) {
			return null;
		}
		// TODO Check if this is always correct
		RevCommit firstCommit = commits.get(commits.size() - 1);
		RevCommit lastCommit = commits.get(0);
		return getDiff(firstCommit, lastCommit);
	}


	@Override
	public Map<DiffEntry, EditList> getDiff(Issue jiraIssue) {
		if (jiraIssue == null) {
			return null;
		}
		List<RevCommit> commits = getCommits(jiraIssue);
		return getDiff(commits);
	}

	@Override
	public Map<DiffEntry, EditList> getDiff(RevCommit firstCommit, RevCommit lastCommit) {
		Map<DiffEntry, EditList> diffEntriesMappedToEditLists = new HashMap<DiffEntry, EditList>();
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

		for (DiffEntry diffEntry : diffEntries) {
			try {
				EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
				diffEntriesMappedToEditLists.put(diffEntry, editList);
			} catch (IOException e) {
				LOGGER.error("Git diff for the file " + diffEntry.getNewPath() + " could not be retrieved. Message: " + e.getMessage());
			}
		}
		diffFormatter.close();
		return diffEntriesMappedToEditLists;
	}

	@Override
	public Map<DiffEntry, EditList> getDiff(RevCommit revCommit) {
		return getDiff(revCommit, revCommit);
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

	@Override
	public Repository getRepository() {
		if (git == null) {
			return null;
		}
		return this.git.getRepository();
	}

	@Override
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

	@Override
	public void close() {
		if (git == null) {
			return;
		}
		git.getRepository().close();
		git.close();
	}

	@Override
	public void deleteRepository() {
		if (git == null) {
			return;
		}
		close();
		File directory = this.getDirectory().getParentFile();
		deleteFolder(directory);
	}

	private static void deleteFolder(File directory) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteFolder(file);
			}
			file.delete();
		}
		directory.delete();
	}

	@Override
	public List<RevCommit> getCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return new LinkedList<RevCommit>();
		}
		String jiraIssueKey = jiraIssue.getKey();
		List<RevCommit> commitsForJiraIssue = new LinkedList<RevCommit>();
		if (git == null || jiraIssueKey == null) {
			LOGGER.error("Commits cannot be retrieved since git object is null.");
			return commitsForJiraIssue;
		}
		// @issue How to get the commits for branches that are not on the master branch?
		// @decision Assume that the JIRA issue key equals the branch name, otherwise
		// return commits on master branch
		Ref branch = getRef(jiraIssueKey);
		List<RevCommit> commits = getCommits(branch);
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

	@Override
	public List<RevCommit> getCommits() {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		for (Ref branch : getOnlyRemoteRefs()) {
			/* @issue: All branches will be created in separate file system
			 * folders for this method's loop. How can this be prevented?
			 *
			 * @alternative: remove this method completely,
			 * fetching commits from all branches is not sensible!
			 * @pro: this method seems to be used only for code testing (TestGetCommits)
			 * @con: scraping it would require coding improvement in test code (TestGetCommits),
			 * but who wants to spend time on that;)
			 *
			 * @decision: release branch folders if possible,
			 * so that in best case only one folder will be used!
			 * @pro: implementation does not seem to be complex at all.
			 * @pro: until discussion are not finished, seems like a cheap workaround
			 * @con: a workaround which has potential to stay forever in the code base
			 * @con: still some more code will be written
			 * @con: scraping it, would require coding improvement in test code (TestGetCommits)
			 */
			commits.addAll(getCommits(branch));
		}
		return commits;
	}

	/*
	 * TODO: This method and getCommits(Issue jiraIssue) need refactoring and
	 * deeper discussions!
	 */
	private Ref getRef(String jiraIssueKey) {
		List<Ref> refs = getAllRefs();
		Ref branch = null;
		for (Ref ref : refs) {
			if (ref.getName().contains(jiraIssueKey)) {
				return ref;
			} else if (ref.getName().equalsIgnoreCase("refs/heads/develop")) {
				branch = ref;
			} else if (ref.getName().equalsIgnoreCase("refs/heads/master")) {
				branch = ref;
			}
		}
		return branch;
	}

	private List<Ref> getAllRefs() {
		return getRefs(ListBranchCommand.ListMode.ALL);
	}

	private List<Ref> getOnlyRemoteRefs() {
		return getRefs(ListBranchCommand.ListMode.REMOTE);
	}

	private List<Ref> getRefs(ListBranchCommand.ListMode listMode) {
		List<Ref> refs = new ArrayList<Ref>();
		try {
			refs = git.branchList().setListMode(listMode).call();
		} catch (GitAPIException e) {
			LOGGER.error("Git could not get references. Message: " + e.getMessage());
		}
		return refs;
	}

	private List<RevCommit> getCommitsFromDefaultBranch() {
		return getCommits(defaultBranch, true);
	}

	private List<RevCommit> getCommits(Ref branch) {
		return getCommits(branch, false);
	}

	private List<RevCommit> getCommits(Ref branch, boolean isDefaultBranch) {
		List<RevCommit> commits = new ArrayList<RevCommit>();
		if (branch == null) {
			return commits;
		}

		File directory;
		String branchNameComponents[] = branch.getName().split("/");
		String branchShortName = branchNameComponents[branchNameComponents.length - 1];
		boolean canReleaseRepoDirectory = false;

		if (isDefaultBranch) {
			directory = new File(fsManager.getDefaultBranchPath());
		} else {
			canReleaseRepoDirectory = !fsManager.isBranchDirectoryInUse(branchShortName);
			directory = new File(fsManager.prepareBranchDirectory(branchShortName));
		}
		try {
			git.close();
			git = git.open(directory);
			git.checkout().setName(branchShortName).call();
			Iterable<RevCommit> iterable = git.log().call();
			for (RevCommit commit : iterable) {
				commits.add(commit);
			}
		} catch (IOException | GitAPIException e) {
			LOGGER.error("Git could not get commits for the branch: "
					+ branch.getName() + " Message: " + e.getMessage());
		}
		if (canReleaseRepoDirectory) {
			fsManager.releaseBranchDirectoryNameToTemp(branchShortName);
		}
		switchGitClientBackToDefaultDirectory();
		return commits;
	}

	private void switchGitClientBackToDefaultDirectory() {
		File directory = new File(fsManager.getDefaultBranchPath());
		try {
			git.close();
			git = git.open(directory);
		} catch (IOException e) {
			LOGGER.error("Git could not get back to default branch. Message: " + e.getMessage());
		}
	}

	@Override
	public int getNumberOfCommits(Issue jiraIssue) {
		if (jiraIssue == null) {
			return 0;
		}
		List<RevCommit> commits = getCommits(jiraIssue);
		return commits.size();
	}

	@Override
	public Git getGit() {
		return git;
	}

	@Override
	public void setGit(Git git) {
		this.git = git;
	}
}