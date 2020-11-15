package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(GitCodeClassExtractor.class);
	private GitClient gitClient;

	public GitCodeClassExtractor(String projectKey) {
		if (projectKey == null) {
			return;
		}
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
		if (gitClient.getDefaultBranchCommits().isEmpty()) {
			return codeClasses;
		}
		TreeWalk treeWalk = new TreeWalk(repository);
		try {
			treeWalk.addTree(gitClient.getDefaultBranchCommits().get(0).getTree());
			treeWalk.setRecursive(false);
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					treeWalk.enterSubtree();
				} else {
					ChangedFile changedFile = new ChangedFile(repository, treeWalk, gitClient.getRemoteUri());
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

	public KnowledgeElement createKnowledgeElementFromFile(ChangedFile file, Set<String> issueKeys) {
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