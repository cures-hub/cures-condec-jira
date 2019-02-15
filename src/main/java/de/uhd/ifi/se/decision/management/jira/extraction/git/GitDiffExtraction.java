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
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class GitDiffExtraction {

	private static File directory;
	private static RevCommit firstCommit;
	private static RevCommit lastCommit;
	private static Repository repository;
	private static Git git;

	public static Map<DiffEntry, EditList> getGitDiff(String commits, String projectKey, boolean commitsKnown)
			throws IOException, GitAPIException, JSONException, InterruptedException {
		if (projectKey == null || !ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return null;
		}
		initGit(projectKey);

		JSONObject commitObj = new JSONObject(commits);
		cherryPickAllCommits(commitObj, git);
		if (!initFirstLastCommits(commitObj)) {
			return null;
		}
		return getDiffEntriesMappedToEditLists(firstCommit, lastCommit);
	}

	public static Map<DiffEntry, EditList> getGitDiff(String projectKey, String jiraIssueKey)
			throws IOException, GitAPIException, JSONException, InterruptedException {
		if (projectKey == null || !ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return null;
		}
		initGit(projectKey);

		JSONObject commitObj = GitClient.getCommits(projectKey, jiraIssueKey);
		cherryPickAllCommits(commitObj, git);
		if (!initFirstLastCommits(commitObj)) {
			return null;
		}
		return getDiffEntriesMappedToEditLists(firstCommit, lastCommit);
	}

	private static void initGit(String projectKey) {
		directory = new File(GitClient.DEFAULT_DIR + projectKey);		
		try {
			git = Git.open(directory);
			repository = git.getRepository();
			git.pull();
			List<RemoteConfig> remotes = git.remoteList().call();
			for (RemoteConfig remote : remotes) {
				git.fetch().setRemote(remote.getName()).setRefSpecs(remote.getFetchRefSpecs()).call();
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
		}
	}

	private static boolean initFirstLastCommits(JSONObject commitObj) {
		firstCommit = getFirstCommit(commitObj);
		if (firstCommit == null) {
			return false;
		}
		lastCommit = getLastCommit(commitObj);
		return true;
	}

	private static void cherryPickAllCommits(JSONObject commitObj, Git git) {
		if (commitObj.isNull("commits")) {
			return;
		}
		JSONArray commits = null;
		try {
			commits = commitObj.getJSONArray("commits");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (commits == null || commits.length() == 0) {
			return;
		}

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
		//revWalk.close();
	}

	private static RevCommit getFirstCommit(JSONObject commitObj) {
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

	private static RevCommit getLastCommit(JSONObject commitObj) {
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

	private static RevCommit getParentOfFirstCommit(RevCommit revCommit) {
		RevCommit parentCommit;
		try {
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

	private static DiffFormatter getDiffFormater() {
		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(repository);
		diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
		diffFormatter.setDetectRenames(true);
		return diffFormatter;
	}

	public static Map<DiffEntry, EditList> getDiffEntriesMappedToEditLists(RevCommit revCommitFirst,
			RevCommit revCommitLast) {
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

}
