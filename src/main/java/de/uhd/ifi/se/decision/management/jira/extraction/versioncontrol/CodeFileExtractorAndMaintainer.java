package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import org.eclipse.jgit.diff.DiffEntry;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

public class CodeFileExtractorAndMaintainer {

	private String projectKey;
	private CodeClassPersistenceManager codeFilePersistenceManager;

	public CodeFileExtractorAndMaintainer(String projectKey) {
		this.projectKey = projectKey;
		this.codeFilePersistenceManager = new CodeClassPersistenceManager(projectKey);
	}

	/**
	 * @issue Which files should be integrated into the knowledge graph?
	 */
	public void extractAllChangedFiles() {
		GitClient gitClient = GitClient.getOrCreate(projectKey);
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		extractAllChangedFiles(diff);
	}

	private void extractAllChangedFiles(Diff diff) {
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.isJavaClass()) {
				codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
			}
		}
	}

	/**
	 * @issue How to maintain changed files and links extracted from git?
	 */
	public void maintainChangedFilesInDatabase(Diff diff) {
		if (diff == null || diff.getChangedFiles().isEmpty()) {
			return;
		}

		for (ChangedFile changedFile : diff.getChangedFiles()) {
			updateChangedFileInDatabase(changedFile);
		}
	}

	public void updateChangedFileInDatabase(ChangedFile changedFile) {
		if (!changedFile.isJavaClass()) {
			return;
		}
		DiffEntry diffEntry = changedFile.getDiffEntry();
		switch (diffEntry.getChangeType()) {
		case ADD:
			// same as modify, thus, no break after add to fall through
		case MODIFY:
			// new links could have been added
			handleAdd(changedFile);
			break;
		case RENAME:
			handleRename(changedFile);
			break;
		case DELETE:
			handleDelete(changedFile);
			break;
		default:
			break;
		}
	}

	private void handleAdd(ChangedFile changedFile) {
		codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
	}

	private void handleDelete(ChangedFile changedFile) {
		KnowledgeElement fileToBeDeleted = codeFilePersistenceManager
				.getKnowledgeElementByName(changedFile.getOldName());
		codeFilePersistenceManager.deleteKnowledgeElement(fileToBeDeleted, null);
	}

	private void handleRename(ChangedFile changedFile) {
		handleDelete(changedFile);
		handleAdd(changedFile);
	}

}
