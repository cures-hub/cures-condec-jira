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

public interface GitClient {

	// @issue What is the best place to clone the git repo to?
	// @issue To which directory does the Git integration for JIRA plug-in clone
	// the repo? Can we use this directory?
	// @alternative APKeys.JIRA_PATH_INSTALLED_PLUGINS
	String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository" + File.separator;

	Map<DiffEntry, EditList> getDiff(String jiraIssueKey);

	Map<DiffEntry, EditList> getDiff(JSONObject commits);

	Map<DiffEntry, EditList> getDiff(RevCommit revCommitFirst, RevCommit revCommitLast);

	Map<DiffEntry, EditList> getDiff(RevCommit revCommit);

	Repository getRepository();

	File getDirectory();

	void closeRepo();

	/**
	 * Closes the repository and deletes its local files.
	 */
	void deleteRepo();

	static JSONObject getCommits(String issueKey) {
		OAuthManager oAuthManager = new OAuthManager();
		String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
	
		String commits = oAuthManager.startRequest(baseUrl + "/rest/gitplugin/latest/issues/" + issueKey + "/commits");
		JSONObject commitObj = null;
		try {
			commitObj = new JSONObject(commits);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return commitObj;
	}

}