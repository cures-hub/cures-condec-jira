package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

/**
 * Responsible for getting the commits that changed a certain file.
 */
public class GitCodeClassExtractor {

	private GitClient gitClient;

	// TODO Get rid of this map
	private Map<String, String> codeClassOriginMap;

	public GitCodeClassExtractor(String projectKey) {
		codeClassOriginMap = new HashMap<String, String>();
		if (projectKey == null) {
			return;
		}
		gitClient = GitClient.getOrCreate(projectKey);
		getCodeClasses();
	}

	public List<ChangedFile> getCodeClasses() {
		List<ChangedFile> codeClasses = new ArrayList<>();
		if (gitClient == null) {
			return codeClasses;
		}

		for (String repoUri : gitClient.getRemoteUris()) {
			codeClasses.addAll(getCodeClasses(repoUri));
		}
		return codeClasses;
	}

	public List<ChangedFile> getCodeClasses(String uri) {
		List<ChangedFile> codeClasses = new ArrayList<>();
		Repository repository = gitClient.getRepository(uri);
		if (repository == null) {
			return codeClasses;
		}
		if (gitClient.getDefaultBranchCommits(uri).isEmpty()) {
			return codeClasses;
		}
		TreeWalk treeWalk = new TreeWalk(repository);
		try {
			treeWalk.addTree(gitClient.getDefaultBranchCommits(uri).get(0).getTree());
			treeWalk.setRecursive(false);
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					treeWalk.enterSubtree();
				} else {
					File file = new File(repository.getWorkTree(), treeWalk.getPathString());
					ChangedFile changedFile = new ChangedFile(file);
					if (changedFile.isExistingJavaClass()) {
						codeClasses.add(changedFile);
						codeClassOriginMap.put(file.getAbsolutePath(), uri);
					}
				}
			}
		} catch (IOException e) {
			// TODO Use Logger instead
			e.printStackTrace();
		}
		treeWalk.close();
		return codeClasses;
	}

	/**
	 * TODO Integrate with ChangedFile class.
	 * 
	 * @param file
	 *            in the git repository, e.g. a Java class.
	 * @return a set of Jira issue keys associated to the file.
	 */
	public Set<String> getIssuesKeysForFile(File file) {
		Set<String> jiraIssueKeysForFile = new LinkedHashSet<String>();
		BlameResult blameResult = getGitBlameForFile(file);
		if (blameResult == null) {
			return jiraIssueKeysForFile;
		}

		int lines = countLines(file);
		for (int line = 0; line < lines; line++) {
			RevCommit revCommit = blameResult.getSourceCommit(line);
			if (revCommit != null) {
				jiraIssueKeysForFile = gitClient.getJiraIssueKeys(revCommit.getFullMessage());
			}
		}
		return jiraIssueKeysForFile;
	}

	public Set<String> getIssuesKeysForFile(ChangedFile changedFile) {
		if (changedFile == null || changedFile.getFile() == null) {
			return new HashSet<>();
		}
		return getIssuesKeysForFile(changedFile.getFile());
	}

	private static int countLines(File file) {
		LineNumberReader reader = null;
		int lineNumber = -1;
		try {
			reader = new LineNumberReader(new FileReader(file));
			while ((reader.readLine()) != null)
				;
			lineNumber = reader.getLineNumber();
			reader.close();
		} catch (Exception e) {

		}
		return lineNumber;
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
			blameResult = gitClient.getGit(repoUri).blame().setFilePath(file.getAbsolutePath()).call();
		} catch (RevisionSyntaxException | GitAPIException e) {
			e.printStackTrace();
		}
		return blameResult;
	}

	public KnowledgeElement createKnowledgeElementFromFile(File file, Set<String> issueKeys) {
		if (file == null || issueKeys == null || gitClient == null) {
			return null;
		}
		KnowledgeElement element = new KnowledgeElement();
		String keyString = "";
		for (String key : issueKeys) {
			keyString = keyString + key + ";";
		}
		element.setSummary(file.getName());
		element.setProject(gitClient.getProjectKey());
		element.setDocumentationLocation(DocumentationLocation.COMMIT);
		element.setStatus(KnowledgeStatus.UNDEFINED);
		element.setType(KnowledgeType.OTHER);
		element.setDescription(keyString);
		return element;
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