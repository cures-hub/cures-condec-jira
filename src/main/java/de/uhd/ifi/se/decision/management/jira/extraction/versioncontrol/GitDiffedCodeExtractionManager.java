package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.GenericCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Extracts decision knowledge elements from files modified by a sequence of
 * commits.
 *
 * Decision knowledge can be documented in code using following syntax inside a
 * source code comment:
 *
 * <p>
 * <b>@decKnowledgeTag knowledge summary text</b>
 * <p>
 * where [decKnowledgeTag] belongs to set of {@link KnowledgeType}s, for example
 * issue, alternative, decision, pro, and con. Empty two lines denote the end of
 * the decision knowledge element. The observation of another tag ends the
 * element, too. Comment end ends also the decision knowledge element.
 *
 * This class will: 1) fetch with gitClient necessary files from code
 * repository, 2) delegate comment extraction to specialized classes, and 3)
 * extract evolution of knowledge elements in files.
 */
public class GitDiffedCodeExtractionManager {
	private static final String OLD_FILE_SYMBOL_PREPENDER = "~";

	/**
	 * @issue Modified files may add, modify, or delete rationale. For extraction of
	 *        rationale on modified files, their contents from the base and changed
	 *        versions are required. How should both versions of affected files be
	 *        accessed?
	 * @decision Work with ChangedFile objects!
	 * @alternative Use two GitClient instances each checked out at different commit
	 *              of the diff range!
	 * @pro no additional checkouts need to be performed by the GitClient
	 * @con requires on more GitClient object in the memory
	 * @alternative Switch between commits using one gitClientImpl instance!
	 * @con frequent checkouts take time
	 * @con requires implementation of another special mode in the client
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GitDiffedCodeExtractionManager.class);

	// may include null values for keys!
	private Map<ChangedFile, CodeExtractionResult> changedElementsPerFiles = new HashMap<>();

	public GitDiffedCodeExtractionManager(Diff diff) {
		if (diff == null) {
			return;
		}
		int entrySequenceNumber = 0;
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			CodeExtractionResult entryResults = processEntry(changedFile);
			entryResults.sequence = entrySequenceNumber;
			changedElementsPerFiles.put(changedFile, entryResults);
			entrySequenceNumber++;
		}
	}

	private CodeExtractionResult processEntry(ChangedFile changedFile) {
		CodeExtractionResult codeExtractionResult = new CodeExtractionResult();

		switch (changedFile.getDiffEntry().getChangeType()) {
		/**
		 * ADD and DELETE are easiest to implement others are more complex.
		 */
		case ADD:
			codeExtractionResult = processAddEntryEdits(changedFile);
			break;
		case MODIFY:
			codeExtractionResult = processModifyEntryEdits(changedFile);
			break;
		case DELETE:
			codeExtractionResult = processDeleteEntryEdits(changedFile);
			break;
		case RENAME: // behaves like MODIFY ?
		case COPY: // ??
		default:
			LOGGER.info(
					"Diff change type is not implemented: " + changedFile.getDiffEntry().getChangeType().toString());
		}
		return codeExtractionResult;
	}

	public List<KnowledgeElement> getNewDecisionKnowledgeElements() {
		if (changedElementsPerFiles.isEmpty()) {
			return new ArrayList<>();
		}
		List<KnowledgeElement> resultValues = new ArrayList<>();

		for (Map.Entry<ChangedFile, CodeExtractionResult> changedKnowledgeElementsPerFile : changedElementsPerFiles
				.entrySet()) {
			CodeExtractionResult codeExtractionResult = changedKnowledgeElementsPerFile.getValue();
			DiffEntry diffEntry = changedKnowledgeElementsPerFile.getKey().getDiffEntry();
			String newPath = diffEntry.getNewPath();
			Map<Edit, List<KnowledgeElement>> elementsPerEdit = codeExtractionResult.elementsInNewerVersion;
			if (elementsPerEdit.isEmpty()) {
				continue;
			}
			resultValues.addAll(getKnowledgeElements(elementsPerEdit, newPath, codeExtractionResult.sequence));
		}
		return resultValues;
	}

	public List<KnowledgeElement> getOldDecisionKnowledgeElements() {
		if (changedElementsPerFiles.isEmpty()) {
			return new ArrayList<>();
		}
		List<KnowledgeElement> resultValues = new ArrayList<>();
		for (Map.Entry<ChangedFile, CodeExtractionResult> changedKnowledgeElementsPerFile : changedElementsPerFiles
				.entrySet()) {
			CodeExtractionResult codeExtractionResult = changedKnowledgeElementsPerFile.getValue();
			DiffEntry diffEntry = changedKnowledgeElementsPerFile.getKey().getDiffEntry();
			String newPath = OLD_FILE_SYMBOL_PREPENDER + diffEntry.getOldPath();
			Map<Edit, List<KnowledgeElement>> elementsPerEdit = codeExtractionResult.elementsInOlderVersion;
			if (elementsPerEdit.isEmpty()) {
				continue;
			}
			resultValues.addAll(getKnowledgeElements(elementsPerEdit, newPath, codeExtractionResult.sequence));
		}
		return resultValues;
	}

	private List<KnowledgeElement> getKnowledgeElements(Map<Edit, List<KnowledgeElement>> codeExtractionResult,
			String path, int sequence) {
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();
		for (Map.Entry<Edit, List<KnowledgeElement>> editListEntry : codeExtractionResult.entrySet()) {
			knowledgeElements.addAll(getKnowledgeElements(editListEntry, path, sequence));
		}
		return knowledgeElements;
	}

	private List<KnowledgeElement> getKnowledgeElements(Map.Entry<Edit, List<KnowledgeElement>> editListEntry,
			String path, int sequence) {
		List<KnowledgeElement> knowledgeElements = new ArrayList<>();

		knowledgeElements.addAll(editListEntry.getValue().stream().map(element -> {
			String newKey = path + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + sequence
					+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR;

			if (editListEntry.getKey() != null) {
				newKey += editListEntry.getKey().toString() + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR
						+ element.getKey();
			} else {
				newKey += GitDecXtract.RAT_KEY_NOEDIT + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + element.getKey();
			}
			element.setKey(newKey);
			return element;
		}).collect(Collectors.toList()));

		return knowledgeElements;
	}

	private CodeExtractionResult processAddEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult codeExtractionResult = new CodeExtractionResult();
		boolean fromNewerFile = true;
		List<CodeComment> commentsInFile = getCommentsFromFile(changedFile, fromNewerFile);

		Map<Edit, List<KnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile, commentsInFile,
				changedFile);

		codeExtractionResult.elementsInNewerVersion = elementsByEdit;

		return codeExtractionResult;
	}

	private CodeExtractionResult processDeleteEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult codeExtractionResult = new CodeExtractionResult();
		boolean fromNewerFile = false;
		List<CodeComment> commentsInFile = getCommentsFromFile(changedFile, fromNewerFile);

		Map<Edit, List<KnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile, commentsInFile,
				changedFile);

		codeExtractionResult.elementsInOlderVersion = elementsByEdit;

		return codeExtractionResult;
	}

	private CodeExtractionResult processModifyEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult codeExtractionResult = new CodeExtractionResult();

		List<CodeComment> commentsInFileA = getCommentsFromFile(changedFile, false);
		// TODO Get deleted file from history
		List<CodeComment> commentsInFileB = getCommentsFromFile(changedFile, true);

		Map<Edit, List<KnowledgeElement>> elementsByEditNew = getRationaleFromComments(true, commentsInFileB,
				changedFile);

		Map<Edit, List<KnowledgeElement>> elementsByEditOld = getRationaleFromComments(false, commentsInFileA,
				changedFile);

		codeExtractionResult.elementsInNewerVersion = elementsByEditNew;
		codeExtractionResult.elementsInOlderVersion = elementsByEditOld;

		return codeExtractionResult;
	}

	private Map<Edit, List<KnowledgeElement>> getRationaleFromComments(boolean newerFile,
			List<CodeComment> commentsInFile, ChangedFile changedFile) {
		Map<Edit, List<KnowledgeElement>> knowledgeElementsInComments = new HashMap<>();

		RationaleFromDiffCodeCommentExtractor rationaleFromDiffCodeCommentExtractor = new RationaleFromDiffCodeCommentExtractor(
				commentsInFile, changedFile.getEditList());

		while (rationaleFromDiffCodeCommentExtractor.next()) {
			knowledgeElementsInComments.putAll(rationaleFromDiffCodeCommentExtractor.getRationaleFromComment(newerFile,
					knowledgeElementsInComments));
		}

		return knowledgeElementsInComments;
	}

	private List<CodeComment> getCommentsFromFile(ChangedFile changedFile, boolean fromNewerFile) {
		if (changedFile.exists()) {
			GenericCodeCommentParser commentParser = getCodeCommentParser(changedFile.getTreeWalkPath());
			if (commentParser != null) {
				return commentParser.getComments(changedFile);
			}
		}
		LOGGER.info("File or parser could not be found for file name " + changedFile.getName());
		// TODO Replace returning null with Optional<> everywhere to avoid
		// NullPointerExceptions
		return null;
	}

	/* Currently only Java parser is available. */
	private GenericCodeCommentParser getCodeCommentParser(String resultingFileName) {
		if (resultingFileName.toLowerCase().endsWith(".java")) {
			return new GenericCodeCommentParser();
		}
		// TODO Replace returning null with Optional<> everywhere to avoid
		// NullPointerExceptions
		return null;
	}

	/**
	 * Stores old and new rationale elements in maps with edits as key. If the old
	 * and new rationale element can be found under the same edit, conflicts may
	 * exist.
	 * 
	 * @issue One rationale element can be modified by more than one edit line.
	 *        Problem was found in
	 *        refs/remotes/origin/CONDEC-534.branch.filtering.improvements.RC2 An
	 *        old rationale located at(108:112:13) in file
	 *        ..extraction/versioncontrol/GitRepositoryFSManager.java was linked
	 *        with two change entries REPLACE(106-108,106-108) and
	 *        REPLACE(109-114,109-120)
	 * 
	 *        Such rationale would be touched twice in below streams, making its key
	 *        unusable. How to deal with this?
	 * @alternative Streams will return reference rationale, but should some how try
	 *              not to modify the rationale key more than once!
	 * @con It would not be possible to see that many edits changed rationale.
	 * 
	 * @alternative Streams will return new origin objects in case of detected
	 *              repetition new object will be created!
	 * @con More code needs to be changed.
	 * @pro Information about change by more than one edit will be preserved.
	 * 
	 * @decision Streams will return new rationale objects and never use origin
	 *           references!
	 * @pro Information about change by more than one edit will be preserved.
	 */
	private class CodeExtractionResult {
		public int sequence = -1;

		/* list of elements modified/created with diff in a file */
		public Map<Edit, List<KnowledgeElement>> elementsInNewerVersion = new HashMap<>();

		/* list of old elements affected by the diff in a file */
		public Map<Edit, List<KnowledgeElement>> elementsInOlderVersion = new HashMap<>();
	}
}
