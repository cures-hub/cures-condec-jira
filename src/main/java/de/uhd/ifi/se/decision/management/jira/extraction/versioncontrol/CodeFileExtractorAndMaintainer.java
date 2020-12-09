package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
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
		// Extracts Decision Knowledge from Code Comments
		GitDecXtract gitExtract = new GitDecXtract(projectKey);
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			if (changedFile.isCodeFile()) {
				KnowledgeElement source = codeFilePersistenceManager.insertKnowledgeElement(changedFile, null);
				if (!changedFile.isJavaFile()) {
					continue;
				}
				Diff diffForFile = new Diff();
				diffForFile.addChangedFile(changedFile);
				KnowledgeElement currentIssue = null;
				KnowledgeElement currentAlternativeOrDecision = null;
				List<KnowledgeElement> elements = gitExtract.getElementsFromCode(diffForFile);

				for (KnowledgeElement element : elements) {
					KnowledgeElement elementInGraph = KnowledgeGraph.getOrCreate(projectKey)
							.addVertexNotBeingInDatabase(element);
					Link link = new Link();
					if (element.getType() == KnowledgeType.ISSUE) { // An issue is linked directly to the code file.
						link = new Link(source, elementInGraph);
						currentIssue = elementInGraph;
					} else if (element.getType() == KnowledgeType.ALTERNATIVE
							|| element.getType() == KnowledgeType.DECISION) { // Alternatives and decisions are linked
																				// to an issue.
						if (currentIssue == null) { // We have no issue – something went wrong or somebody violated
													// structure
							link = new Link(source, elementInGraph);
							currentAlternativeOrDecision = null;
						} else {
							link = new Link(currentIssue, elementInGraph);
							currentAlternativeOrDecision = elementInGraph;
						}
					} else if (element.getType() == KnowledgeType.PRO || element.getType() == KnowledgeType.CON
							|| element.getType() == KnowledgeType.ARGUMENT) { // Arguments are linked to alternatives
																				// and decisions.
						if (currentIssue == null) { // We have no issue – something went wrong or somebody violated
													// structure
							link = new Link(source, elementInGraph);
							currentAlternativeOrDecision = null;
						} else if (currentAlternativeOrDecision == null) { // We have an issue, but no alternative or
																		   // decision – something still went wrong
																		   // or somebody still violated structure
							link = new Link(currentIssue, elementInGraph);
						} else {
							link = new Link(currentAlternativeOrDecision, elementInGraph);
						}
					} else {
						if (currentIssue == null || element.getType() == KnowledgeType.GOAL
								|| element.getType() == KnowledgeType.PROBLEM) { // Goals and problems are linked
																					// directly to the code file.
							link = new Link(source, elementInGraph);
						} else { // Everything else is linked to an issue.
							link = new Link(currentIssue, elementInGraph);
						}
					}
					KnowledgeGraph.getOrCreate(projectKey).addEdgeNotBeingInDatabase(link);
				}
			}
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
		if (!changedFile.isCodeFile()) {
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
