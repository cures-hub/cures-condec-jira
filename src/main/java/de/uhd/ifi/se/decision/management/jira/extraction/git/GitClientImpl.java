package de.uhd.ifi.se.decision.management.jira.extraction.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @issue How to access commits related to a JIRA issue?
 * @decision Only use jGit.
 * @pro The jGit library is open source.
 * @alternative Both, the jgit library and the git integration for JIRA plugin
 *              were used to access git repositories.
 * @con An application link and oAuth is needed to call REST API on Java side.
 */
public class GitClientImpl implements GitClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitClientImpl.class);
	private Git git;

	public GitClientImpl(String uri, String projectKey) {
		File directory = new File(DEFAULT_DIR + projectKey);
		pullOrClone(uri, directory);
	}

	public GitClientImpl(String projectKey) {
		File directory = new File(DEFAULT_DIR + projectKey);
		String uri = GitClient.getUriFromGitIntegrationPlugin(projectKey);
		pullOrClone(uri, directory);
	}

	private void pullOrClone(String uri, File directory) {
		if (directory.exists()) {
			openRepository(uri, directory);
			new Thread(() -> {
				pull();
			}).start();
		} else {
			new Thread(() -> {
				cloneRepository(uri, directory);
			}).start();
		}
	}

	private void openRepository(String uri, File directory) {
		if (git != null) {
			closeRepo();
		}
		try {
			git = Git.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Git repository could not be opened.");
			cloneRepository(uri, directory);
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

	private void cloneRepository(String uri, File directory) {
		if (uri == null || uri.isEmpty()) {
			return;
		}
		try {
			git = Git.cloneRepository().setURI(uri).setDirectory(directory).setCloneAllBranches(true).call();
			setConfig();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private void setConfig() {
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

	@Override
	public Map<DiffEntry, EditList> getDiff(List<RevCommit> commits) {
		if (commits == null || commits.size() == 0) {
			return null;
		}
		// TODO Check if this is always correct
		RevCommit firstCommit = commits.get(commits.size() - 1);
		System.out.println(firstCommit.getShortMessage());
		RevCommit lastCommit = commits.get(0);
		System.out.println(lastCommit.getShortMessage());
		return getDiff(firstCommit, lastCommit);
	}

	@Override
	public Map<DiffEntry, EditList> getDiff(String jiraIssueKey) {
		List<RevCommit> commits = getCommits(jiraIssueKey);
		return getDiff(commits);
	}

	@Override
	public Map<DiffEntry, EditList> getDiff(RevCommit revCommitFirst, RevCommit revCommitLast) {
		Map<DiffEntry, EditList> diffEntriesMappedToEditLists = new HashMap<DiffEntry, EditList>();
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();

		DiffFormatter diffFormatter = getDiffFormater();
		try {
			RevCommit parentCommit = getParent(revCommitFirst);
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

	private RevCommit getFirstCommit(JSONObject commitObj) {
		if (commitObj == null || commitObj.isNull("commits")) {
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
		if (commitObj == null || commitObj.isNull("commits")) {
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

	private RevCommit getParent(RevCommit revCommit) {
		RevCommit parentCommit;
		try {
			Repository repository = this.getRepository();
			RevWalk revWalk = new RevWalk(repository);
			parentCommit = revWalk.parseCommit(revCommit.getParent(0).getId());
			revWalk.close();
		} catch (Exception e) {
			System.err.println("Could not get the parent commit.");
			e.printStackTrace();
			return null;
		}
		return parentCommit;
	}

	@Override
	public void closeRepo() {
		if (git == null) {
			return;
		}
		git.getRepository().close();
		git.close();
	}

	@Override
	public void deleteRepo() {
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

	@Override
	public List<RevCommit> getCommits(String jiraIssueKey) {
		List<RevCommit> commitsForJiraIssue = new LinkedList<RevCommit>();
		if (git == null) {
			LOGGER.error("Commits cannot be retrieved since git object is null.");
			return commitsForJiraIssue;
		}
		try {
			Iterable<RevCommit> iterable = git.log().call();
			Iterator<RevCommit> iterator = iterable.iterator();
			while (iterator.hasNext()) {
				RevCommit commit = iterator.next();
				// TODO Improve identification of jira issue key in commit message
				if (GitClient.getJiraIssueKey(commit.getFullMessage()).equalsIgnoreCase(jiraIssueKey)) {
					commitsForJiraIssue.add(commit);
					LOGGER.info("Commit message for key " + jiraIssueKey + ": " + commit.getShortMessage());
				}
			}
		} catch (GitAPIException e) {
			LOGGER.error("Could not retrieve commits for the JIRA issue key " + jiraIssueKey);
			e.printStackTrace();
		}
		return commitsForJiraIssue;
	}
}