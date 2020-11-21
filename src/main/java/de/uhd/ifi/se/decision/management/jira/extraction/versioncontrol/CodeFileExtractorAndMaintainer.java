package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import org.eclipse.jgit.diff.DiffEntry;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.CodeClassInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;

/**
 * Extracts and links code files when cloning the repo. Maintains links on git
 * fetch.
 * 
 * Extracting means: 1) Adding code files to the {@link CodeClassInDatabase}, 2)
 * adding links to the {@link LinkInDatabase}, 3) adding code files and links to
 * the {@link KnowledgeGraph}.
 */
public class CodeFileExtractorAndMaintainer {

	private String projectKey;
	private CodeClassPersistenceManager codeFilePersistenceManager;

	public CodeFileExtractorAndMaintainer(String projectKey) {
		this.projectKey = projectKey;
		this.codeFilePersistenceManager = new CodeClassPersistenceManager(projectKey);
	}

	/**
	 * Extracts all code files from the default branch (only the files that are
	 * present in the last version). Links the files to the respective Jira Jira
	 * issues (e.g., work items or requirements). Extracting means: 1) Adding code
	 * files to the {@link CodeClassInDatabase}, 2) adding links to the
	 * {@link LinkInDatabase}, 3) adding code files and links to the
	 * {@link KnowledgeGraph}.
	 * 
	 * @issue Which files should be integrated into the knowledge graph?
	 * @decision Integrate all Java files into the knowledge graph and link them to
	 *           the respective Jira issues (e.g., work items or requirements)!
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
			addDeleteOrUpdateChangedFileInDatabase(changedFile);
		}
	}

	public void addDeleteOrUpdateChangedFileInDatabase(ChangedFile changedFile) {
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
		KnowledgeElement fileToBeUpdated = codeFilePersistenceManager
				.getKnowledgeElementByName(changedFile.getOldName());
		changedFile.setId(fileToBeUpdated.getId());
		codeFilePersistenceManager.updateKnowledgeElement(changedFile, null);
	}

}
