package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;

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
		gitClient = GitClient.getOrCreate(projectKey);
	}

	public List<ChangedFile> getCodeClasses() {
		if (gitClient == null) {
			LOGGER.error("GitClient null");
			return new ArrayList<>();
		}
		return gitClient.getDiffOfEntireDefaultBranch().getChangedFiles();
	}

	// TODO Add constructor to KnowledgeElement
	public KnowledgeElement createKnowledgeElementFromFile(ChangedFile file) {
		if (file == null || gitClient == null) {
			return null;
		}
		KnowledgeElement element = new KnowledgeElement();
		String keyString = "";
		for (String key : file.getJiraIssueKeys()) {
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