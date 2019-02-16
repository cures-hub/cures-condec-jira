package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CherryPickCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class GitClient {

	// @issue What is the best place to clone the git repo to?
	// @issue To which directory does the Git integration for JIRA plug-in clone
	// the repo? Can we use this directory?
	// @alternative APKeys.JIRA_PATH_INSTALLED_PLUGINS
	public static final String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository"
			+ File.separator;

	private Git git;

	public GitClient(String uri, String projectKey) {
		File directory = new File(DEFAULT_DIR + projectKey);
		setGit(directory);
		pullOrClone(uri, directory);
		setConfig();
	}

	public GitClient(String projectKey) {
		File directory = new File(DEFAULT_DIR + projectKey);
		String uri = getUriFromGitIntegrationPlugin(projectKey);
		setGit(directory);
		pullOrClone(uri, directory);
	}

	public void setConfig() {
		Repository repository = this.getRepository();
		StoredConfig config = repository.getConfig();
		// @issue The internal representation of a file might add system dependent new
		// line statements, for example CR LF in Windows
		// @decision Disable system dependent new line statements
		config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, AutoCRLF.TRUE);
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Repository getRepository() {
		if (git == null) {
			return null;
		}
		return this.git.getRepository();
	}

	public File getDirectory() {
		Repository repository = this.getRepository();
		if (repository == null) {
			return null;
		}
		return repository.getDirectory();
	}

	public void pullOrClone(String uri, File directory) {
		new Thread(() -> {
			if (directory.exists() && directory.list().length > 0) {
				this.pull();
			} else {
				this.cloneRepo(uri, directory);
			}
		}).start();
	}

	private void setGit(File directory) {
		if (git != null) {
			closeRepo();
		}
		try {
			git = Git.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void pull() {
		try {
			git.pull().call();
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs()).call();
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private void cloneRepo(String uri, File directory) {
		if (uri == null) {
			return;
		}
		try {
			git = Git.cloneRepository().setURI(uri).setDirectory(directory).setCloneAllBranches(true).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private String getUriFromGitIntegrationPlugin(String projectKey) {
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

	public static JSONObject getCommits(String issueKey) {
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

	public Map<DiffEntry, EditList> getDiff(String commits, String projectKey, boolean commitsKnown) {
		if (projectKey == null || !ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return null;
		}
		JSONObject commitObj = null;
		try {
			commitObj = new JSONObject(commits);
			// cherryPickAllCommits(commitObj, git);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RevCommit firstCommit = getFirstCommit(commitObj);
		RevCommit lastCommit = getLastCommit(commitObj);
		return getDiffEntriesMappedToEditLists(firstCommit, lastCommit);
	}

	public Map<DiffEntry, EditList> getDiff(String projectKey, String jiraIssueKey) {
		if (projectKey == null || !ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return null;
		}

		JSONObject commitObj = GitClient.getCommits(jiraIssueKey);
		// cherryPickAllCommits(commitObj, git);
		RevCommit firstCommit = getFirstCommit(commitObj);
		RevCommit lastCommit = getLastCommit(commitObj);
		return getDiffEntriesMappedToEditLists(firstCommit, lastCommit);
	}

	private void cherryPickAllCommits(JSONObject commitObj, Git git) {
		if (commitObj.isNull("commits")) {
			return;
		}
		JSONArray commits = null;
		try {
			commits = commitObj.getJSONArray("commits");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (commits == null || commits.length() == 0) {
			return;
		}

		Repository repository = this.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		for (int i = 0; i < commits.length(); i++) {
			try {
				ObjectId id = repository.resolve(commits.getJSONObject(i).getString("commitId"));
				RevCommit commit = revWalk.parseCommit(id);
				CherryPickCommand cherryPick = git.cherryPick();
				cherryPick.setMainlineParentNumber(1);
				cherryPick.include(commit);
				cherryPick.setNoCommit(true);
				cherryPick.call();
			} catch (RevisionSyntaxException | IOException | JSONException | GitAPIException e) {
				e.printStackTrace();
			}
		}
		revWalk.close();
	}

	private RevCommit getFirstCommit(JSONObject commitObj) {
		if (commitObj.isNull("commits")) {
			return null;
		}
		JSONArray commits = null;
		try {
			commits = commitObj.getJSONArray("commits");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (commits == null || commits.length() == 0) {
			return null;
		}

		RevCommit firstCommit = null;
		Repository repository = this.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		try {
			ObjectId id = repository.resolve(commits.getJSONObject(commits.length() - 1).getString("commitId"));
			firstCommit = revWalk.parseCommit(id);
		} catch (RevisionSyntaxException | IOException | JSONException e) {
			e.printStackTrace();
		}
		revWalk.close();
		return firstCommit;
	}

	private RevCommit getLastCommit(JSONObject commitObj) {
		if (commitObj.isNull("commits")) {
			return null;
		}
		JSONArray commits = null;
		try {
			commits = commitObj.getJSONArray("commits");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (commits == null || commits.length() == 0) {
			return null;
		}

		RevCommit lastCommit = null;
		Repository repository = this.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		ObjectId id;
		try {
			id = repository.resolve(commits.getJSONObject(0).getString("commitId"));
			lastCommit = revWalk.parseCommit(id);
		} catch (RevisionSyntaxException | IOException | JSONException e) {
			e.printStackTrace();
		}
		revWalk.close();
		return lastCommit;
	}

	private RevCommit getParentOfFirstCommit(RevCommit revCommit) {
		RevCommit parentCommit;
		try {
			Repository repository = this.getRepository();
			RevWalk revWalk = new RevWalk(repository);
			parentCommit = revWalk.parseCommit(revCommit.getParent(0).getId());
			revWalk.close();
		} catch (Exception e) {
			System.err.println("Could not get the parent commit");
			e.printStackTrace();
			return null;
		}
		return parentCommit;
	}

	private DiffFormatter getDiffFormater() {
		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		Repository repository = this.getRepository();
		diffFormatter.setRepository(repository);
		diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
		diffFormatter.setDetectRenames(true);
		return diffFormatter;
	}

	public Map<DiffEntry, EditList> getDiffEntriesMappedToEditLists(RevCommit revCommitFirst, RevCommit revCommitLast) {
		Map<DiffEntry, EditList> diffEntriesMappedToEditLists = new HashMap<DiffEntry, EditList>();
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();

		DiffFormatter diffFormatter = getDiffFormater();
		try {
			RevCommit parentCommit = getParentOfFirstCommit(revCommitFirst);
			if (parentCommit != null) {
				diffEntries = diffFormatter.scan(parentCommit.getTree(), revCommitLast.getTree());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (DiffEntry diffEntry : diffEntries) {
			try {
				EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
				diffEntriesMappedToEditLists.put(diffEntry, editList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		diffFormatter.close();
		return diffEntriesMappedToEditLists;
	}

	// public List<DiffEntry> getDiffEntries(RevCommit revCommit) {
	// List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
	//
	// DiffFormatter diffFormatter = getDiffFormater(revCommit);
	// try {
	// RevCommit parentCommit = this.getParent(revCommit);
	// diffEntries = diffFormatter.scan(parentCommit.getTree(),
	// revCommit.getTree());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// diffFormatter.close();
	// return diffEntries;
	// }
	//
	// private DiffFormatter getDiffFormater(RevCommit revCommit) {
	// DiffFormatter diffFormatter = new
	// DiffFormatter(DisabledOutputStream.INSTANCE);
	// diffFormatter.setRepository(this.repository);
	// diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
	// diffFormatter.setDetectRenames(true);
	// return diffFormatter;
	// }
	//
	// public RevCommit getParent(RevCommit revCommit) {
	// RevCommit parentCommit;
	// try {
	// RevWalk revWalk = new RevWalk(repository);
	// parentCommit = revWalk.parseCommit(revCommit.getParent(0).getId());
	// revWalk.close();
	// } catch (IOException e) {
	// System.err.println("Could not get the parent commit for " + revCommit);
	// e.printStackTrace();
	// return null;
	// }
	// return parentCommit;
	// }
	//
	// public Map<DiffEntry, EditList> getDiffEntriesMappedToEditLists(RevCommit
	// revCommit) {
	// Map<DiffEntry, EditList> diffEntriesMappedToEditLists = new
	// HashMap<DiffEntry, EditList>();
	// List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
	//
	// DiffFormatter diffFormatter = getDiffFormater(revCommit);
	// try {
	// RevCommit parentCommit = this.getParent(revCommit);
	// diffEntries = diffFormatter.scan(parentCommit.getTree(),
	// revCommit.getTree());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// for (DiffEntry diffEntry : diffEntries) {
	// try {
	// EditList editList = diffFormatter.toFileHeader(diffEntry).toEditList();
	// diffEntriesMappedToEditLists.put(diffEntry, editList);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// diffFormatter.close();
	// return diffEntriesMappedToEditLists;
	// }

	public void closeRepo() {
		if (git == null) {
			return;
		}
		git.getRepository().close();
		git.close();
	}

	/**
	 * Closes the repository and deletes its local files.
	 */
	public void closeAndDeleteRepo() {
		closeRepo();
		File directory = this.getDirectory();
		if (directory.exists()) {
			deleteFolder(directory);
		}
	}

	private static void deleteFolder(File directory) {
		if (directory.listFiles() != null) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					deleteFolder(file);
				}
				file.delete();
			}
		}
		directory.delete();
	}
}