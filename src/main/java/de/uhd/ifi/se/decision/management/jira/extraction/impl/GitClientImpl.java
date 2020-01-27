package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.google.common.collect.Lists;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryFSManager;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * @issue How to access commits related to a Jira issue?
 * @decision Use the jGit library to access the git repositories for a Jira
 *           project!
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for Jira plugin
 *              were used to access git repositories!
 * @con An application link and oAuth is needed to call REST API on Java side in
 *      order to access the git repository with the git integration for Jira
 *      plugin.
 *
 *
 *      This implementation works well only with configuration for one remote
 *      git server. Multiple instances of this class are "thread-safe" in the
 *      limited way that the checked-out branch files are stored in dedicated
 *      branch folders and can be read. Modifying files is not safe and not
 *      supported.
 */
public class GitClientImpl implements GitClient {

    private static final long REPO_OUTDATED_AFTER = 15 * 60 * 1000; // ex. 15 minutes = 15 minutes * 60 seconds * 1000
								    // miliseconds
    private Git git;
    private String remoteUri;
    private String projectKey;
    private String defaultDirectory;
    private String defaultBranchFolderName;
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
	// TODO: the last parameter should be a setting retrievable with
	// ConfigPersistenceManager
	repoInitSuccess = pullOrCloneRepository(projectKey, defaultDirectory, uri, "develop");
    }

    public GitClientImpl(String uri, String projectKey) {
	// TODO: the last parameter should be a setting retrievable with
	// ConfigPersistenceManager
	repoInitSuccess = pullOrCloneRepository(projectKey, DEFAULT_DIR, uri, "develop");
    }

    public GitClientImpl(GitClientImpl originalClient) {
	repoInitSuccess = pullOrCloneRepository(originalClient.getProjectKey(), originalClient.getDefaultDirectory(),
		originalClient.getRemoteUri(), originalClient.getDefaultBranchFolderName());
    }

    public GitClientImpl(String projectKey) {
	String uri = ConfigPersistenceManager.getGitUri(projectKey);
	// TODO: the last parameter should be a setting retrievable with
	// ConfigPersistenceManager
	repoInitSuccess = pullOrCloneRepository(projectKey, DEFAULT_DIR, uri, "develop");
    }

    private boolean pullOrCloneRepository(String projectKey, String defaultDirectory, String uri,
	    String defaultBranchFolderName) {
	fsManager = new GitRepositoryFSManager(defaultDirectory, projectKey, uri, defaultBranchFolderName);
	File directory = new File(fsManager.getDefaultBranchPath());
	this.remoteUri = uri;
	this.projectKey = projectKey;
	this.defaultDirectory = defaultDirectory;
	this.defaultBranchFolderName = defaultBranchFolderName;
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
	    LOGGER.error(
		    "Git repository could not be opened: " + directory.getAbsolutePath() + "\n\t" + e.getMessage());
	    return false;
	}
	return true;
    }

    private boolean pull() {
	if (!isPullNeeded()) {
	    return true;
	}
	try {
	    List<RemoteConfig> remotes = git.remoteList().call();
	    for (RemoteConfig remote : remotes) {
		git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs())
			.setRemoveDeletedRefs(true).call();
		LOGGER.info("Fetched branches in " + git.getRepository().getDirectory());
	    }
	    git.pull().call();
	} catch (GitAPIException e) {
	    LOGGER.error("Issue occurred while pulling from a remote." + "\n\t" + e.getMessage());
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
	} else {
	    if (isRepoOutdated(file.lastModified())) {
		updateFileModifyTime(file);
		return true;
	    }
	    return false;
	}
    }

    private boolean isRepoOutdated(long lastModified) {
	Date date = new Date();
	long fileLifespan = date.getTime() - lastModified;
	return fileLifespan > REPO_OUTDATED_AFTER;
    }

    private boolean updateFileModifyTime(File file) {
	Date date = new Date();
	if (!file.setLastModified(date.getTime())) {
	    LOGGER.error("Could not modify a file modify time, repositories will be fetched on each request.");
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
	    LOGGER.error("Git repository could not be cloned: " + uri + " " + directory.getAbsolutePath() + "\n\t"
		    + e.getMessage());
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
	/**
	 * @issue The internal representation of a file might add system dependent new
	 *        line statements, for example CR LF in Windows. How to deal with
	 *        different line endings?
	 * @decision Disable system dependent new line statements!
	 * @pro It is a common approach
	 * @alternative Ignore the problem! The plug-in in hosted on one machine, which
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

    @Override
    public Diff getDiff(List<RevCommit> commits) {
	if (commits == null || commits.size() == 0) {
	    return null;
	}
	// TODO Check if this is always correct
	RevCommit firstCommit = commits.get(commits.size() - 1);
	RevCommit lastCommit = commits.get(0);
	return getDiff(firstCommit, lastCommit);
    }

    @Override
    public Diff getDiff(Issue jiraIssue) {
	if (jiraIssue == null) {
	    return null;
	}
	List<RevCommit> commits = getCommits(jiraIssue);
	return getDiff(commits);
    }

    @Override
    public Diff getDiff(RevCommit firstCommit, RevCommit lastCommit) {
	Diff diff = new DiffImpl();
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

	File directory = getDirectory();
	String baseDirectory = "";
	if (directory != null) {
	    baseDirectory = getDirectory().toString().replace(".git", "");
	}
	for (DiffEntry diffEntry : diffEntries) {
	    try {
		EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
		diff.addChangedFile(new ChangedFileImpl(diffEntry, editList, baseDirectory));
	    } catch (IOException e) {
		LOGGER.error("Git diff for the file " + diffEntry.getNewPath() + " could not be retrieved. Message: "
			+ e.getMessage());
	    }
	}
	diffFormatter.close();
	return diff;
    }

    @Override
    public Diff getDiff(RevCommit revCommit) {
	return getDiff(revCommit, revCommit);
    }

    /**
     * Temporally switches git client's directory to feature branch directory to
     * fetch commits, afterwards returns to default branch directory after.
     * 
     * @param featureBranch ref of the feature branch.
     * @return list of unique commits.
     */
    @Override
    public List<RevCommit> getFeatureBranchCommits(Ref featureBranch) {
	List<RevCommit> branchUniqueCommits = new ArrayList<RevCommit>();
	List<RevCommit> branchCommits = getCommits(featureBranch);
	RevCommit lastCommonAncestor = null;
	for (RevCommit commit : branchCommits) {
	    if (defaultBranchCommits.contains(commit)) {
		LOGGER.info("Found last common commit " + commit.toString());
		lastCommonAncestor = commit;
		break;
	    }
	    branchUniqueCommits.add(commit);
	}
	if (lastCommonAncestor == null) {
	    branchUniqueCommits = null;
	} else if (branchUniqueCommits.size() > 0) {
	    branchUniqueCommits = Lists.reverse(branchUniqueCommits);
	} else {
	    System.out.println("I am here");
	    branchUniqueCommits = branchCommits;
	}

	return branchUniqueCommits;
    }

    @Override
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
	     * @decision Throw exception!
	     * @pro this is the best practice, just look at most libraries.
	     */
	    return null;
	}
	return getFeatureBranchCommits(featureBranch);
    }

    private Ref getBranch(String featureBranchName) {
	if (featureBranchName == null || featureBranchName.length() == 0) {
	    LOGGER.info("Null or empty branch name was passed.");
	    return null;
	}
	List<Ref> remoteBranches = getRemoteBranches();
	if (remoteBranches != null) {
	    for (Ref branch : remoteBranches) {
		String branchName = branch.getName();
		if (branchName.endsWith(featureBranchName)) {
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
	if (git == null | this.getDirectory() != null) {
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

    /**
     * Switches git client's directory to feature branch directory, i.e., DOES NOT
     * go back to the default branch directory.
     */
    @Override
    public boolean checkoutFeatureBranch(String featureBranchShortName) {
	Ref featureBranch = getBranch(featureBranchShortName);
	if (null == featureBranch) {
	    return false;
	}
	return checkoutFeatureBranch(featureBranch);

    }

    /**
     * Switches git client's directory to commit directory, checks out files in
     * working dir for the commit. I.e., DOES NOT go back to default branch
     * directory.
     */
    @Override
    public boolean checkoutCommit(RevCommit commit) {
	String commitName = commit.getName();

	// will copy default branch folder
	File directory = new File(fsManager.prepareBranchDirectory(commitName));

	return (switchGitDirectory(directory) && checkout(commit.getName(), true));
    }

    private boolean checkout(String branchShortName) {
	return checkout(branchShortName, false);
    }

    /**
     * Switches git client's directory to feature branch directory, i.e., DOES NOT
     * go back to default branch directory.
     */
    @Override
    public boolean checkoutFeatureBranch(Ref featureBranch) {
	String[] branchNameComponents = featureBranch.getName().split("/");
	String branchShortName = branchNameComponents[branchNameComponents.length - 1];
	File directory = new File(fsManager.prepareBranchDirectory(branchShortName));

	return (switchGitDirectory(directory) && pull() && checkout(branchShortName));
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
	for (Ref branch : getRemoteBranches()) {
	    /**
	     * @issue All branches will be created in separate file system folders for this
	     *        method's loop. How can this be prevented?
	     * @alternative remove this method completely!
	     * @pro Fetching commits from all branches is not sensible.
	     * @con Fetching commits from all branches may still be needed in some use
	     *      cases. *
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
	    commits.addAll(getCommits(branch));
	}
	return commits;
    }

    /*
     * TODO: This method and getCommits(Issue jiraIssue) need refactoring and deeper
     * discussions!
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

    @Override
    public List<Ref> getRemoteBranches() {
	return getRefs(ListBranchCommand.ListMode.REMOTE);
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

    private List<RevCommit> getCommitsFromDefaultBranch() {
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
	boolean canReleaseRepoDirectory = false;

	if (isDefaultBranch) {
	    directory = new File(fsManager.getDefaultBranchPath());
	} else {
	    canReleaseRepoDirectory = !fsManager.isBranchDirectoryInUse(branchShortName);
	    directory = new File(fsManager.prepareBranchDirectory(branchShortName));
	}

	if (switchGitDirectory(directory) && pull() && checkout(branchShortName)) {
	    Iterable<RevCommit> iterable = null;
	    try {
		iterable = git.log().call();
	    } catch (GitAPIException e) {
		LOGGER.error("Git could not get commits for the branch: " + branch.getName() + " Message: "
			+ e.getMessage());
	    }
	    if (iterable != null) {
		for (RevCommit commit : iterable) {
		    commits.add(commit);
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
	if (!isCommitWithinBranch) {
	    String checkoutName = "origin/" + checkoutObjectName;
	    try {
		git.checkout().setName(checkoutName).call();
	    } catch (GitAPIException | JGitInternalException e) {

		LOGGER.error("Could not checkout " + checkoutName + ". " + e.getMessage());
		return false;
	    }
	    // create local branch
	    if (!createLocalBranchIfNotExists(checkoutObjectName)) {
		LOGGER.error("Could delete and create local branch");
		return false;

	    }
	}

	// checkout local branch/commit
	try {
	    git.checkout().setName(checkoutObjectName).call();
	} catch (GitAPIException | JGitInternalException e) {

	    LOGGER.error("Could not checkout " + checkoutObjectName + ". " + e.getMessage());
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
	File directory = new File(fsManager.getDefaultBranchPath());
	try {
	    git.close();
	    git = Git.open(directory);
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

    public String getRemoteUri() {
	return remoteUri;
    }

    public String getProjectKey() {
	return projectKey;
    }

    public String getDefaultDirectory() {
	return defaultDirectory;
    }

    public String getDefaultBranchFolderName() {
	return defaultBranchFolderName;
    }

    public boolean isRepoInitSuccess() {
	return repoInitSuccess;
    }
}