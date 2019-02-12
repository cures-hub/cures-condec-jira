package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class GitClient {

	public static final String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository"
			+ File.separator;

	private static final String BASEURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

	/**
	 * URI the Repository should be cloned from.
	 */
	private static String uri;

	/**
	 * The Git Object for the cloned Repository, to perform the operations on.
	 */
	private static Git git;

	/**
	 * The directory the repository should be cloned to.
	 */
	private static File directory;

	public static void getGitRepo(String repositoryUri, String projectKey) {
		if (projectKey != null) {
			directory = new File(DEFAULT_DIR + projectKey);
			uri = repositoryUri;
			new Thread(() -> {
				try {
					if (repositoryUri != null) {
						cloneRepo();
					}
				} catch (GitAPIException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	public static void getGitRepo(String projectKey) throws JSONException {
		if (projectKey != null && ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			directory = new File(DEFAULT_DIR + projectKey);
			OAuthManager ar = new OAuthManager();
			String repository = ar.startRequest(BASEURL + "/rest/gitplugin/1.0/repository?projectKey=" + projectKey);
			uri = getRemoteURL(repository);
			new Thread(() -> {
				try {
					if (repository != null) {
						cloneRepo();
					}
				} catch (GitAPIException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	private static void cloneRepo() throws GitAPIException {
		try {
			if (existingRepository()) {
				git.pull().call();
			} else {
				try {
					git = Git.cloneRepository().setURI(uri).setDirectory(directory).setCloneAllBranches(true).call();
				} catch (GitAPIException e) {
					e.printStackTrace();
					throw e;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean existingRepository() throws IOException {
		if (directory.exists() && directory.list().length > 0) {
			if (git != null) {
				closeRepo();
			}
			try {
				git = Git.open(directory);
			} catch (IOException io) {
				io.printStackTrace();
				throw io;
			}
		}
		return checkExistingRepository();
	}

	/**
	 * check whether the current directory is an git repository
	 * 
	 * @return true if the directory is an repository false otherwise
	 * @throws IOException
	 */
	private static boolean checkExistingRepository() throws IOException {
		if (git != null) {
			try {
				if (git.getRepository().exactRef("HEAD") != null) {
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return false;
	}

	/**
	 * Closes the Repo.
	 */
	public static void closeRepo() {
		if (git != null) {
			git.getRepository().close();
			git.close();
		}
	}

	/**
	 * Closes the Repo and deletes it's local files.
	 */
	public static void closeAndDeleteRepo() {
		if (git != null) {
			git.getRepository().close();
			git.close();
		}

		try {
			if (directory.exists()) {
				FileUtils.deleteDirectory(directory);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getRemoteURL(String repository) throws JSONException {
		JSONObject obj = new JSONObject(repository);
		if (!obj.isNull("repositories")) {
			return obj.getJSONArray("repositories").getJSONObject(0).getString("origin");
		}
		return null;
	}

}
