package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;

/**
 * Class to connect to commits and code in git.
*/
public interface GitClient {

	/**
	 * @issue What is the best place to clone the git repo to?
	 * @issue To which directory does the Git integration for JIRA plug-in clone the
	 *        repo? Can we use this directory?
	 * @alternative APKeys.JIRA_PATH_INSTALLED_PLUGINS
	 */
	String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository" + File.separator;

	/**
	 * Get a map of diff entries and the respective edit lists for a commit.
	 * 
	 * @param revCommit
	 *            commit as a RevCommit object.
	 * @return map of diff entries and respective edit lists.
	 */
	Map<DiffEntry, EditList> getDiff(RevCommit revCommit);
	
	/**
	 * Get a map of diff entries and the respective edit lists for all commits belonging to a JIRA issue.
	 * 
	 * @param revCommit
	 *            commit as a RevCommit object
	 * @return map of diff entries and respective edit lists
	 */
	Map<DiffEntry, EditList> getDiff(String jiraIssueKey);

	Map<DiffEntry, EditList> getDiff(JSONObject commits);

	Map<DiffEntry, EditList> getDiff(RevCommit revCommitFirst, RevCommit revCommitLast);

	/**
	 * Get the jgit repository object.
	 * 
	 * @return jgit repository object.
	 */
	Repository getRepository();

	/**
	 * Get the path to the repository.
	 * 
	 * @return path to the repository as a File object.
	 */
	File getDirectory();

	/**
	 * Closes the repository.
	 */
	void closeRepo();

	/**
	 * Closes the repository and deletes its local files.
	 */
	void deleteRepo();
	
	/**
	 * Retrieves the commits with the issue key in their commit message.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return commits with the issue key in their commit message as a JSONObject.
	 */
	static String getUriFromGitIntegrationPlugin(String projectKey) {
		OAuthManager oAuthManager = new OAuthManager();
		String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
	
		String repository = oAuthManager
				.startRequest(baseUrl + "/rest/gitplugin/latest/repository?projectKey=" + projectKey);
	
		String uri = null;
		try {
			JSONObject jsonObject = new JSONObject(repository);
			if (!jsonObject.isNull("repositories")) {
				uri = jsonObject.getJSONArray("repositories").getJSONObject(0).getString("origin");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		return uri;
	}

	/**
	 * Retrieves the commits with the issue key in their commit message.
	 * 
	 * @param jiraIssueKey
	 *            JIRA issue key that is searched for in commit messages.
	 * @return commits with the issue key in their commit message as a JSONObject.
	 */
	static JSONObject getCommits(String jiraIssueKey) {
		OAuthManager oAuthManager = new OAuthManager();
		String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

		String commits = oAuthManager.startRequest(baseUrl + "/rest/gitplugin/latest/issues/" + jiraIssueKey + "/commits");
		JSONObject commitObj = null;
		try {
			commitObj = new JSONObject(commits);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return commitObj;
	}
}