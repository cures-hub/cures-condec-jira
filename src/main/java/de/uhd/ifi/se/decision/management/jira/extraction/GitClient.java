package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Class to connect to commits and code in git.
 */
public interface GitClient {

    /**
     * @issue What is the best place to clone the git repo to?
     * @decision Clone git repo to JIRAHome/data/condec-plugin/git!
     * @pro The Git integration for JIRA plug-in clones its repos to a similar
     *      folder: JIRAHome/data/git-plugin.
     */
    String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
	    + File.separator + "condec-plugin" + File.separator + "git" + File.separator;

    /**
     * Switch git client's directory to the commit.
     *
     * @param commit name of the feature branch
     * @return success or failure boolean
     */
    boolean checkoutCommit(RevCommit commit, String repoUri);

    /**
     * Switch git client's directory to dedicated feature branch directory.
     *
     * @param featureBranchShortName name of the feature branch
     * @return success or failure boolean
     */
    boolean checkoutFeatureBranch(String featureBranchShortName, String repoUri);

    /**
     * Switch git client's directory to dedicated feature branch directory.
     *
     * @param featureBranch ref of the feature branch
     * @return success or failure boolean
     */
    boolean checkoutFeatureBranch(Ref featureBranch);

    /**
     * Retrieves the commits with the JIRA issue key in their commit message.
     * 
     * @see RevCommit
     * @param jiraIssue JIRA issue. Its key is searched for in commit messages.
     * @return commits with the JIRA issue key in their commit message as a list of
     *         RevCommits.
     */
    List<RevCommit> getCommits(Issue jiraIssue, String repoUri);

    /**
     * Retrieves all commits with the JIRA issue key in their commit message across
     * all existing repositories.
     * 
     * @see RevCommit
     * @param jiraIssue JIRA issue. Its key is searched for in commit messages.
     * @return commits with the JIRA issue key in their commit message as a list of
     *         RevCommits.
     */
    List<RevCommit> getAllRelatedCommits(Issue jiraIssue);

    /**
     * Retrieves all commits of the git repository.
     * 
     * @see RevCommit
     * @return all commits as a list of RevCommits.
     */
    List<RevCommit> getCommits(String repoUri);

    /**
     * Get the {@link Diff} for a commit containing the {@link ChangedFile}s. Each
     * {@link ChangedFile} is created from a diff entry and contains the respective
     * edit list.
     * 
     * @see RevCommit
     * @param revCommit commit as a RevCommit object.
     * @return {@link Diff} object containing the {@link ChangedFile}s.
     */
    Diff getDiff(RevCommit revCommit, String repoUri);

    /**
     * Get the {@link Diff} for a JIRA issue containing the {@link ChangedFile}s.
     * Each {@link ChangedFile} is created from a diff entry and contains the
     * respective edit list.
     *
     * @param jiraIssue a JIRA issue object.
     * @return {@link Diff} object containing the {@link ChangedFile}s.
     */
    Diff getDiff(Issue jiraIssue, String repoUri);

    /**
     * Get the {@link Diff} for a list of commits containing the
     * {@link ChangedFile}s. Each {@link ChangedFile} is created from a diff entry
     * and contains the respective edit list.
     * 
     * @param commits commits as a list of RevCommit objects.
     * @return {@link Diff} object containing the {@link ChangedFile}s.
     */
    Diff getDiff(List<RevCommit> commits, String repoUri);

    /**
     * Get the {@link Diff} for a branch of commits indicated by the first and last
     * commit on the branch containing the {@link ChangedFile}s. Each
     * {@link ChangedFile} is created from a diff entry and contains the respective
     * edit list.
     *
     * @param firstCommit first commit on a branch as a RevCommit object.
     * @param lastCommit  last commit on a branch as a RevCommit object.
     * @return {@link Diff} object containing the {@link ChangedFile}s.
     */
    Diff getDiff(RevCommit firstCommit, RevCommit lastCommit, String repoUri);

    /**
     * Get a list of remote branches in repository.
     *
     * @return Refs list
     */
    List<Ref> getRemoteBranches(String repoUri);

    /**
     * Get a list of remote branches in all repository.
     *
     * @return Refs list
     */
    List<Ref> getAllRemoteBranches();

    /**
     * Get a list of all commits of a <b>feature</b> branch, which do not exist in
     * the <b>default</b> branch. Commits are sorted by age, beginning with the
     * oldest.
     *
     * @param featureBranchName name of the feature branch.
     * @return ordered list of commits unique to this branch.
     */
    List<RevCommit> getFeatureBranchCommits(String featureBranchName, String repoUri);

    /**
     * Get a list of all commits of a <b>feature</b> branch, which do not exist in
     * the <b>default</b> branch. Commits are sorted by age, beginning with the
     * oldest.
     *
     * @param featureBranch ref of the feature branch.
     * @return ordered list of commits unique to this branch.
     */
    List<RevCommit> getFeatureBranchCommits(Ref featureBranch);

    /**
     * Get the jgit repository object.
     * 
     * @return jgit repository object.
     */
    Repository getRepository(String repoUri);

    /**
     * Get the path to the .git folder.
     * 
     * @return path to the .git folder as a File object.
     */
    File getDirectory(String repoUri);

    /**
     * Closes the repository.
     */
    void close(String repoUri);

    /**
     * Closes all repositories.
     */
    void closeAll();

    /**
     * Closes the repository and deletes its local files.
     */
    void deleteRepository(String repoUri);

    /**
     * Returns the number of commits with the JIRA issue key in their commit
     * message.
     *
     * @param jiraIssue JIRA issue. Its key is searched for in commit messages.
     * @return number of commits with the JIRA issue key in their commit message.
     */
    int getNumberOfCommits(Issue jiraIssue, String repoUri);

    /**
     * Return the remote Uri identifier of the Repository containing the given
     * Branch.
     * 
     * @param featureBranch
     * @return String of Remote Repository Uri or Null if branch not contained in
     *         any Repo.
     */
    String getRepoUriFromBranch(Ref featureBranch);

    /**
     * Retrieves the JIRA issue key from a commit message.
     * 
     * @param commitMessage a commit message that should contain a JIRA issue key.
     * @return extracted JIRA issue key or empty String if no JIRA issue key could
     *         be found.
     * 
     * @issue How to identify the JIRA issue key(s) in a commit message?
     * @alternative This is a very simple method to detect the JIRA issue key as the
     *              first word in the message and should be improved!
     */
    static String getJiraIssueKey(String commitMessage) {
	if (commitMessage.isEmpty()) {
	    return "";
	}
	String[] split = commitMessage.split("[:+ ]");
	return split[0].toUpperCase(Locale.ENGLISH);
    }

    /**
     * Returns List of remoteUri Strings
     * 
     * @return List<String> remoteUris.
     */

    List<String> getRemoteUris();

    /**
     * Returns the git object.
     * 
     * @return git object.
     */
    Git getGit(String repoUri);

    /**
     * Return the default branch for the give Repository Uri
     * 
     * @param String repoUri
     * @return Ref defaultBranch
     */

    Ref getDefaultBranch(String repoUri);

    /**
     * Sets the git object.
     * 
     * @param git object.
     */
    void setGit(Git git, String repoUri);

    /**
     * Return Map of DefaultBranchFolderNames with RepoUri as key
     * 
     * @return DefaultBranchFolderNames parameter.
     */
    Map<String, String> getDefaultBranchFolderNames();

    /**
     * Get Default Branch Commits
     * 
     * @param repoUri
     * @return
     */
    List<RevCommit> getDefaultBranchCommits(String repoUri);

}