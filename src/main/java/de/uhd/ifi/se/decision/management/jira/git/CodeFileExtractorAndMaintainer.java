package de.uhd.ifi.se.decision.management.jira.git;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.DecisionKnowledgeElementInCodeComment;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
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
 * 
 * @issue Which files should be integrated into the knowledge graph?
 * @decision Integrate all Java files into the knowledge graph and link them to
 *           the respective Jira issues (e.g., work items or requirements)!
 */
public class CodeFileExtractorAndMaintainer {

	private CodeClassPersistenceManager codeFilePersistenceManager;
	private KnowledgeGraph graph;

	public CodeFileExtractorAndMaintainer(String projectKey) {
		this.codeFilePersistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getCodeClassPersistenceTextManager();
		this.graph = KnowledgeGraph.getInstance(projectKey);
	}

	/**
	 * Either inserts, updates, or deletes the code files in the diff in the
	 * database depending on its change type (see {@link ChangedFile#getDiffEntry()}
	 * and {@link DiffEntry#getChangeType()}. Also extracts the decision knowledge
	 * from code comments within the {@link Diff}. Links the files to the respective
	 * Jira issues (e.g., work items or requirements).
	 * 
	 * Code extraction means: 1) Adding code files to the
	 * {@link CodeClassInDatabase}, 2) adding links to the {@link LinkInDatabase},
	 * 3) adding code files and links to the {@link KnowledgeGraph}.
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
		case DELETE:
			codeFilePersistenceManager.deleteKnowledgeElement(changedFile, null);
			break;
		case RENAME:
			codeFilePersistenceManager.updateKnowledgeElement(changedFile, null);
			break;
		default:
		case MODIFY:
			// rationale elements in code comments could have been added
			// no break after modify to fall through
		case ADD:
			List<DecisionKnowledgeElementInCodeComment> decisionKnowledgeInCodeComments = changedFile
					.getRationaleElementsFromCodeComments();
			KnowledgeElement source = codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
			if (!graph.updateElement(source)) {
				graph.addVertex(source);
			}
			graph.addElementsNotInDatabase(source, decisionKnowledgeInCodeComments);
			break;
		}
	}

	public static boolean deleteOldFiles(String projectKey) {
		GitClient gitClient = GitClient.getInstance(projectKey);
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		return new CodeFileExtractorAndMaintainer(projectKey).deleteOldFiles(diff);
	}

	public boolean deleteOldFiles(Diff diff) {
		List<String> fileNamesInDiff = diff.getChangedFiles().stream().map(file -> file.getName())
				.collect(Collectors.toList());
		for (KnowledgeElement codeFileInDatabase : codeFilePersistenceManager.getKnowledgeElements()) {
			if (!fileNamesInDiff.contains(codeFileInDatabase.getSummary())) {
				codeFilePersistenceManager.deleteKnowledgeElement(codeFileInDatabase, null);
			}
		}
		return false;
	}
}
