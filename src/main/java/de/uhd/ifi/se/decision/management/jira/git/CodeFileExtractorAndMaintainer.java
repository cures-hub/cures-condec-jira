package de.uhd.ifi.se.decision.management.jira.git;

import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.DecisionKnowledgeElementInCodeComment;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.git.model.DiffForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
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
 * 
 * Trace link creation and maintenance between code files and Jira issues works
 * as follows: 1) Initial trace link creation during git clone. 2) Manual link
 * improvement and maintenance by developers. Developers can manually change
 * links. 3) Automatic trace link maintenance during git fetch based on recent
 * changes.
 */
public class CodeFileExtractorAndMaintainer {

	private String projectKey;
	private CodeClassPersistenceManager codeFilePersistenceManager;

	public CodeFileExtractorAndMaintainer(String projectKey) {
		this.projectKey = projectKey;
		this.codeFilePersistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getCodeClassPersistenceTextManager();
	}

	/**
	 * Extracts all code files and the decision knowledge from code comments within
	 * the {@link DiffForSingleRepository}. Links the files to the respective Jira
	 * Jira issues (e.g., work items or requirements). Extracting means: 1) Adding
	 * code files to the {@link CodeClassInDatabase}, 2) adding links to the
	 * {@link LinkInDatabase}, 3) adding code files and links to the
	 * {@link KnowledgeGraph}.
	 * 
	 * @issue Which files should be integrated into the knowledge graph?
	 * @decision Integrate all Java files into the knowledge graph and link them to
	 *           the respective Jira issues (e.g., work items or requirements)!
	 */
	public void extractAllChangedFiles(Diff diff) {
		KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (!changedFile.isCodeFileToExtract()) {
				continue;
			}
			List<DecisionKnowledgeElementInCodeComment> decisionKnowledgeInCodeComments = changedFile
					.getRationaleElementsFromCodeComments();
			KnowledgeElement source = codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
			graph.updateElement(source);
			graph.addElementsNotInDatabase(source, decisionKnowledgeInCodeComments);
		}
	}

	/**
	 * Either inserts, updates, or deletes the code files in the diff in the
	 * database depending on its change type (see {@link ChangedFile#getDiffEntry()}
	 * and {@link DiffEntry#getChangeType()}.
	 * 
	 * @param diff
	 *            {@link Diff} object with recently added, updated, or deleted
	 *            {@link ChangedFile}s.
	 * 
	 * @issue How to maintain changed files and links extracted from git?
	 * @decision Iterate over all changed code files in the new diff and decide
	 *           based on their change type whether they are added, updated, or
	 *           deleted in the database and knowledge graph. Establish links
	 *           between Jira issues and code files.
	 */
	public void maintainChangedFilesInDatabase(Diff diff) {
		if (diff == null || diff.getChangedFiles().isEmpty()) {
			return;
		}

		for (ChangedFile changedFile : diff.getChangedFiles()) {
			addUpdateOrDeleteChangedFileInDatabase(changedFile);
		}
	}

	/**
	 * Either inserts, updates, or deletes a code file in database depending on its
	 * change type (see {@link ChangedFile#getDiffEntry()} and
	 * {@link DiffEntry#getChangeType()}.
	 * 
	 * @param changedFile
	 *            {@link ChangedFile} object.
	 */
	public void addUpdateOrDeleteChangedFileInDatabase(ChangedFile changedFile) {
		if (!changedFile.isCodeFileToExtract()) {
			return;
		}
		DiffEntry diffEntry = changedFile.getDiffEntry();
		switch (diffEntry.getChangeType()) {
		case ADD:
			codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
			break;
		case MODIFY:
			// new links could have been added
			// same as rename, thus, no break after add to fall through
		case RENAME:
			codeFilePersistenceManager.updateKnowledgeElement(changedFile, null);
			break;
		case DELETE:
			codeFilePersistenceManager.deleteKnowledgeElement(changedFile, null);
			break;
		default:
			break;
		}
	}
}
