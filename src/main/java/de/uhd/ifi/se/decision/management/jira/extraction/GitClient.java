package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

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
	public static final String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
			.getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "git" + File.separator;

	/**
	 * Retrieves the commits with the JIRA issue key in their commit message.
	 * 
	 * @see RevCommit
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return commits with the JIRA issue key in their commit message as a list of
	 *         RevCommits.
	 */
	List<RevCommit> getCommits(String jiraIssueKey);

	/**
	 * Retrieves all commits of the git repository.
	 * 
	 * @see RevCommit
	 * @return all commits as a list of RevCommits.
	 */
	List<RevCommit> getCommits();

	/**
	 * Get a map of diff entries and the respective edit lists for a commit.
	 * 
	 * @see RevCommit
	 * @param revCommit
	 *            commit as a RevCommit object.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(RevCommit revCommit);

	/**
	 * Get a map of diff entries and the respective edit lists for all commits
	 * belonging to a JIRA issue.
	 * 
	 * @param revCommit
	 *            commit as a RevCommit object.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(String jiraIssueKey);

	/**
	 * Get a map of diff entries and the respective edit lists for a list of
	 * commits.
	 * 
	 * @param commits
	 *            commits as a list of RevCommit objects.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(List<RevCommit> commits);

	/**
	 * Get a map of diff entries and the respective edit lists for a branch of
	 * commits indicated by the first and last commit on the branch.
	 * 
	 * @param firstCommits
	 *            first commit on a branch as a RevCommit object.
	 * @param lastCommits
	 *            last commit on a branch as a RevCommit object.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(RevCommit firstCommit, RevCommit lastCommit);

	/**
	 * Get the jgit repository object.
	 * 
	 * @return jgit repository object.
	 */
	Repository getRepository();

	/**
	 * Get the path to the .git folder.
	 * 
	 * @return path to the .git folder as a File object.
	 */
	File getDirectory();

	/**
	 * Closes the repository.
	 */
	void close();

	/**
	 * Closes the repository and deletes its local files.
	 */
	void deleteRepository();

	/**
	 * Returns the number of commits with the JIRA issue key in their commit message.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return number of commits with the JIRA issue key in their commit message.
	 */
	public int getNumberOfCommits(String jiraIssueKey);

	/**
	 * Retrieves the JIRA issue key from a commit message.
	 * 
	 * @param commitMessage
	 *            a commit message that should contain a JIRA issue key.
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
	 * Returns the git object.
	 * 
	 * @return git object.
	 */
	public Git getGit();

	/**
	 * Sets the git object.
	 * 
	 * @param git
	 *            object.
	 */
	public void setGit(Git git);
}