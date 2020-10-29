package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.CommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

/**
 * Responsible for getting the commits that changed a certain file.
 */
public class GitCodeClassExtractor {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitCodeClassExtractor.class);
	private String projectKey;
	private GitClient gitClient;

	public GitCodeClassExtractor(String projectKey) {
		if (projectKey == null) {
			return;
		}
		this.projectKey = projectKey;
		gitClient = GitClient.getOrCreate(projectKey);
	}

	public List<ChangedFile> getCodeClasses() {
		List<ChangedFile> codeClasses = new ArrayList<>();
		if (gitClient == null) {
			return codeClasses;
		}

		for (GitClientForSingleRepository gitClientForSingleRepo : gitClient.getGitClientsForSingleRepos()) {
			codeClasses.addAll(getCodeClasses(gitClientForSingleRepo));
		}
		return codeClasses;
	}

	public List<ChangedFile> getCodeClasses(GitClientForSingleRepository gitClient) {
		List<ChangedFile> codeClasses = new ArrayList<>();
		Repository repository = gitClient.getRepository();
		if (repository == null) {
			return codeClasses;
		}
		if (gitClient.getCommitsFromDefaultBranch().isEmpty()) {
			return codeClasses;
		}
		TreeWalk treeWalk = new TreeWalk(repository);
		try {
			treeWalk.addTree(gitClient.getCommitsFromDefaultBranch().get(0).getTree());
			treeWalk.setRecursive(false);
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					treeWalk.enterSubtree();
				} else {
					File file = new File(repository.getWorkTree(), treeWalk.getPathString());
					ChangedFile changedFile = new ChangedFile(file, gitClient.getRemoteUri());
					changedFile.setTreeWalkPath(treeWalk.getPathString());
					if (changedFile.isExistingJavaClass()) {
						codeClasses.add(changedFile);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Code classes could not be retrieved. " + e.getMessage());
		}
		treeWalk.close();
		return codeClasses;
	}

	/**
	 * @param changedFile
	 *            {@link ChangedFile} in the git repository, e.g. a Java class.
	 * @return a set of Jira issue keys associated to the file.
	 */
	public Set<String> getJiraIssueKeysForFile(ChangedFile changedFile) {
		if (changedFile == null || changedFile == null) {
			return new HashSet<>();
		}
		Set<String> jiraIssueKeysForFile = new LinkedHashSet<String>();
		BlameResult blameResult = getGitBlameForFile(changedFile);
		if (blameResult == null) {
			return jiraIssueKeysForFile;
		}

		int lines = countLines(changedFile);
		for (int line = 0; line < lines; line++) {
			RevCommit revCommit = blameResult.getSourceCommit(line);
			if (revCommit != null) {
				String commitMessageForLine = revCommit.getFullMessage();
				Set<String> jiraIssueKeysForLine = CommitMessageParser.getJiraIssueKeys(commitMessageForLine,
						projectKey);
				jiraIssueKeysForFile.addAll(jiraIssueKeysForLine);
			}
		}
		return jiraIssueKeysForFile;
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
			LOGGER.error("Lines of file could not be counted. " + e.getMessage());
		}
		return lineNumber;
	}

	/**
	 * Shows who (author) and what commit last modified each line of a file.
	 * 
	 * @issue Why does git blame return different commits than the commit view?
	 * 
	 * @param changedFile
	 *            {@link ChangedFile} in the git repository, e.g. a Java class.
	 * @return git blame result for the given file.
	 */
	private BlameResult getGitBlameForFile(ChangedFile changedFile) {
		BlameResult blameResult = null;
		if (changedFile == null || changedFile == null) {
			return blameResult;
		}
		try {
			String repoUri = changedFile.getRepoUri();
			if (repoUri == null) {
				return blameResult;
			}
			// TODO Remove getTreeWalkPath() method from ChangedFile class and replace it
			// with a method to calculate the relative path.
			blameResult = gitClient.getGitClientsForSingleRepo(repoUri).getGit().blame()
					.setFilePath(changedFile.getTreeWalkPath()).call();
		} catch (RevisionSyntaxException | GitAPIException e) {
			LOGGER.error("Git blame could not be called for the file. " + e.getMessage());
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
		element.setType(KnowledgeType.CODE);
		element.setDescription(keyString);
		return element;
	}

	public GitClient getGitClient() {
		return gitClient;
	}
}