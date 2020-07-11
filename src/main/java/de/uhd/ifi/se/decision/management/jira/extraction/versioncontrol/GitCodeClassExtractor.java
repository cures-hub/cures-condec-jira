package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

public class GitCodeClassExtractor {

	private String projectKey;
	private List<File> codeClasses;
	private Map<String, String> codeClassOriginMap;
	private Map<String, String> treeWalkPath;
	private GitClient gitClient;

	public GitCodeClassExtractor(String projectKey) {
		if (projectKey == null) {
			return;
		}
		this.projectKey = projectKey;
		codeClassOriginMap = new HashMap<String, String>();
		treeWalkPath = new HashMap<String, String>();
		getCodeClassFiles();
	}

	public List<File> getCodeClassFiles() {
		List<File> codeClasses = new ArrayList<>();

		gitClient = GitClient.getOrCreate(projectKey);
		for (String repoUri : gitClient.getRemoteUris()) {
			Repository repository = gitClient.getRepository(repoUri);
			if (repository != null) {
				TreeWalk treeWalk = new TreeWalk(repository);
				try {
					if (gitClient.getDefaultBranchCommits(repoUri).size() == 0) {
						break;
					}
					treeWalk.addTree(gitClient.getDefaultBranchCommits(repoUri).get(0).getTree());
					treeWalk.setRecursive(false);
					while (treeWalk.next()) {
						if (treeWalk.isSubtree()) {
							treeWalk.enterSubtree();
						} else {
							File file = new File(repository.getWorkTree(), treeWalk.getPathString());
							ChangedFile chfile = new ChangedFile(file);
							if (chfile.isExistingJavaClass() && file != null) {
								codeClasses.add(file);
								codeClassOriginMap.put(file.getAbsolutePath(), repoUri);
								treeWalkPath.put(file.getAbsolutePath(), treeWalk.getPathString());
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				treeWalk.close();
			}
		}
		this.codeClasses = codeClasses;
		return codeClasses;
	}

	public List<String> getIssuesKeysForFile(File file) {
		BlameResult blameResult = getGitBlameForFile(file);
		List<String> allKeys = new ArrayList<String>();
		if (blameResult == null) {
			return null;
		}
		try {
			int lines = countLines(file);
			for (int line = 0; line < lines; line++) {
				RevCommit revCommit = blameResult.getSourceCommit(line);
				if (revCommit != null) {
					Set<String> returnValue = getJiraIssueKeys(revCommit.getFullMessage());
					for (String val : returnValue) {
						if (!allKeys.contains(val)) {
							allKeys.add(val);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (allKeys == null || allKeys.size() == 0) {
			allKeys.add("");
		}
		return allKeys;
	}

	private static int countLines(File aFile) throws IOException {
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(new FileReader(aFile));
			while ((reader.readLine()) != null)
				;
			return reader.getLineNumber();
		} catch (Exception ex) {
			return -1;
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	private BlameResult getGitBlameForFile(File file) {
		BlameResult blameResult = null;
		if (file == null) {
			return blameResult;
		}
		try {
			String repoUri = codeClassOriginMap.get(file.getAbsolutePath());
			if (repoUri == null) {
				return null;
			}
			Repository repository = gitClient.getGit(repoUri).getRepository();
			BlameCommand blamer = new BlameCommand(repository);
			blamer.setFilePath(treeWalkPath.get(file.getAbsolutePath()));
			blamer.setStartCommit(gitClient.getDefaultBranchCommits(repoUri).get(0));
			blameResult = blamer.call();
			// blameResult =
			// gitClient.getGit(repoUri).blame().setFilePath(gitFile.getPath()).call();
		} catch (RevisionSyntaxException | GitAPIException e) {
			e.printStackTrace();
		}
		return blameResult;
	}

	private Set<String> getJiraIssueKeys(String message) {
		Set<String> keys = new LinkedHashSet<String>();
		if (projectKey == null) {
			return keys;
		}
		String baseKey = projectKey.toUpperCase(Locale.ENGLISH);
		String pattern = "(" + baseKey + "-)\\d+";

		String[] words = message.split("[\\s,:]+");
		for (String word : words) {
			word = word.toUpperCase(Locale.ENGLISH);
			if (word.matches(pattern)) {
				keys.add(word);
			}
		}
		return keys;
	}

	public KnowledgeElement createKnowledgeElementFromFile(File file, List<String> issueKeys) {
		if (file == null || issueKeys == null || projectKey == null) {
			return null;
		}
		KnowledgeElement element = new KnowledgeElement();
		String keyString = "";
		for (String key : issueKeys) {
			keyString = keyString + key + ";";
		}
		element.setSummary(file.getName());
		element.setProject(projectKey);
		element.setDocumentationLocation(DocumentationLocation.COMMIT);
		element.setStatus(KnowledgeStatus.UNDEFINED);
		element.setType(KnowledgeType.OTHER);
		element.setDescription(keyString);
		return element;
	}

	public int getNumberOfCodeClasses() {
		return codeClasses.size();
	}

	public List<File> getCodeClassListFull() {
		return codeClasses;
	}

	public void close() {
		gitClient.closeAll();
	}

	public Map<String, String> getCodeClassOriginMap() {
		return codeClassOriginMap;
	}

	public GitClient getGitClient() {
		return gitClient;
	}
}
