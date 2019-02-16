package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.CoreConfig.AutoCRLF;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;

public class GitClient {

	// @issue What is the best place to clone the git repo to?
	// @issue To which directory does the Git integration for JIRA plug-in clone
	// the repo? Can we use this directory?
	// @alternative APKeys.JIRA_PATH_INSTALLED_PLUGINS
	public static final String DEFAULT_DIR = System.getProperty("user.home") + File.separator + "repository"
			+ File.separator;

	private static final String BASEURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

	private String uri;
	private Git git;
	private File directory;

	private Repository repository;

	public GitClient(String uri, String projectKey) {
		this.directory = new File(DEFAULT_DIR + projectKey);
		this.setRepository(DEFAULT_DIR + projectKey);
		this.uri = uri;
		pullOrClone();
	}

	public GitClient(String projectKey) {
		this.directory = new File(DEFAULT_DIR + projectKey);
		this.setRepository(DEFAULT_DIR + projectKey);
		this.uri = getUriFromGitIntegrationPlugin(projectKey);
		pullOrClone();
	}

	public void setRepository(String repositoryPath) {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.setMustExist(true);
		repositoryBuilder.setGitDir(new File(repositoryPath));
		try {
			this.repository = repositoryBuilder.build();
			// this.repository.resolve(reference);
			StoredConfig config = this.repository.getConfig();
			// @issue The internal representation of a file might add system dependent new
			// line statements, for example CR LF in Windows
			// @decision Disable system dependent new line statements
			config.setEnum(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF,
					AutoCRLF.TRUE);
			config.save();
		} catch (IOException e) {
			System.err.println("Repository could not be found.");
			e.printStackTrace();
		}
	}

	public void pullOrClone() {
		new Thread(() -> {
			if (existingRepository()) {
				this.pull();
			} else {
				this.cloneRepo();
			}
		}).start();
	}

	private boolean existingRepository() {
		if (directory.exists() && directory.list().length > 0) {
			if (git != null) {
				closeRepo();
			}
			try {
				git = Git.open(directory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return checkExistingRepository();
	}

	/**
	 * check whether the current directory is a git repository.
	 * 
	 * @return true if the directory is a git repository, false otherwise.
	 */
	private boolean checkExistingRepository() {
		boolean isExistent = false;
		if (git == null) {
			return false;
		}
		try {
			isExistent = git.getRepository().exactRef("HEAD") != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isExistent;
	}

	private void pull() {
		try {
			git.pull().call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private void cloneRepo() {
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
		String repository = oAuthManager
				.startRequest(BASEURL + "/rest/gitplugin/latest/repository?projectKey=" + projectKey);

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
		String commits = oAuthManager.startRequest(BASEURL + "/rest/gitplugin/latest/issues/" + issueKey + "/commits");
		JSONObject commitObj = null;
		try {
			commitObj = new JSONObject(commits);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return commitObj;
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