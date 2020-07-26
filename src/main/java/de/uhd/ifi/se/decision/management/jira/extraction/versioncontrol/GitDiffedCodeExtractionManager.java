package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.CodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.JavaCodeCommentParser;
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
	 * @decision Work with ChangedFile objects.
	 * @alternative Use two GitClient instances each checked out at different commit
	 *              of the diff range!
	 * @pro no additional checkouts need to be performed by the GitClient
	 * @con requires on more GitClient object in the memory
	 * @alternative Switch between commits using one gitClientImpl instance!
	 * @con frequent checkouts take time
	 * @con requires implementation of another special mode in the client
	 */
	private Diff diff;
	private static final Logger LOGGER = LoggerFactory.getLogger(GitDiffedCodeExtractionManager.class);

	// may include null values for keys!
	// TODO Consider changing to Map<ChangedFile, CodeExtractionResult> or add
	// CodeExtractionResult as an attribute to the ChangedFile class
	private Map<DiffEntry, CodeExtractionResult> results = new HashMap<>();

	public GitDiffedCodeExtractionManager(Diff diff) {
		this.diff = diff;
		processEntries();
	}

	public List<KnowledgeElement> getNewDecisionKnowledgeElements() {
		if (results.isEmpty()) {
			return new ArrayList<>();
		}
		List<KnowledgeElement> resultValues = new ArrayList<>();

		for (Map.Entry<DiffEntry, CodeExtractionResult> dEntry : results.entrySet()) {
			if (dEntry.getValue() == null) {
				continue;
			}
			String newPath = dEntry.getKey().getNewPath();
			Map<Edit, List<KnowledgeElement>> codeExtractionResult;
			codeExtractionResult = dEntry.getValue().diffedElementsInNewerVersion;
			if (codeExtractionResult.isEmpty()) {
				continue;
			}
			resultValues.addAll(getKnowledgeElements(codeExtractionResult, newPath, dEntry.getValue().sequence));
		}
		return resultValues;
	}

	public List<KnowledgeElement> getOldDecisionKnowledgeElements() {
		if (results.isEmpty()) {
			return new ArrayList<>();
		}
		List<KnowledgeElement> resultValues = new ArrayList<>();
		for (Map.Entry<DiffEntry, CodeExtractionResult> dEntry : results.entrySet()) {
			if (dEntry.getValue() == null) {
				continue;
			}
			String newPath = OLD_FILE_SYMBOL_PREPENDER + dEntry.getKey().getOldPath();
			Map<Edit, List<KnowledgeElement>> codeExtractionResult = dEntry.getValue().diffedElementsInOlderVersion;
			if (codeExtractionResult.isEmpty()) {
				continue;
			}
			resultValues.addAll(getKnowledgeElements(codeExtractionResult, newPath, dEntry.getValue().sequence));
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
		if (editListEntry.getKey() != null) {
			knowledgeElements.addAll(editListEntry.getValue().stream().map(d -> {
				KnowledgeElement element = d;
				String newKey = path + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + sequence
						+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + editListEntry.getKey().toString()
						+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + d.getKey();
				element.setKey(newKey);
				return element;
			}).collect(Collectors.toList()));
		} else {
			knowledgeElements.addAll(editListEntry.getValue().stream().map(d -> {
				KnowledgeElement element = d;
				String newKey = path + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + sequence
						+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + GitDecXtract.RAT_KEY_NOEDIT
						+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + d.getKey();

				element.setKey(newKey);
				return element;
			}).collect(Collectors.toList()));
		}
		return knowledgeElements;
	}

	private void processEntries() {
		int entrySequenceNumber = 0;
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			CodeExtractionResult entryResults = processEntry(changedFile);
			if (entryResults == null) {
				return;
			}
			entryResults.sequence = entrySequenceNumber;
			results.put(changedFile.getDiffEntry(), entryResults);
			entrySequenceNumber++;
		}
	}

	private CodeExtractionResult processEntry(ChangedFile changedFile) {
		CodeExtractionResult returnResult = null;

		switch (changedFile.getDiffEntry().getChangeType()) {
		/*
		 * ADD and DELETE are easiest to implement others are more complex. Begin
		 * implementation with ADD.
		 */
		case ADD:
			returnResult = processAddEntryEdits(changedFile);
			break;
		case MODIFY:
			returnResult = processModifyEntryEdits(changedFile);
			break;
		case DELETE:
			returnResult = processDeleteEntryEdits(changedFile);
			break;
		case RENAME: // behaves like MODIFY ?
		case COPY: // ??
		default:
			LOGGER.info(
					"Diff change type is not implemented: " + changedFile.getDiffEntry().getChangeType().toString());
			return returnResult;
		}
		// TODO: gather all elements in newer version of the file

		return returnResult;
	}

	/* ADD does not require gitClientCheckedOutAtDiffStart */
	private CodeExtractionResult processAddEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();
		boolean fromNewerFile = true;

		String fileBRelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getNewPath());
		List<CodeComment> commentsInFile = getCommentsFromFile(fileBRelativePath, fromNewerFile);

		Map<Edit, List<KnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile, commentsInFile,
				changedFile);

		returnCodeExtractionResult.diffedElementsInNewerVersion = elementsByEdit;

		return returnCodeExtractionResult;
	}

	/* DELETE does not require gitClientCheckedOutAtDiffEnd */
	private CodeExtractionResult processDeleteEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();
		boolean fromNewerFile = false;

		String fileARelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getNewPath());
		List<CodeComment> commentsInFile = getCommentsFromFile(fileARelativePath, fromNewerFile);

		Map<Edit, List<KnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile, commentsInFile,
				changedFile);

		returnCodeExtractionResult.diffedElementsInOlderVersion = elementsByEdit;

		return returnCodeExtractionResult;
	}

	private CodeExtractionResult processModifyEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();

		String fileARelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getOldPath());
		List<CodeComment> commentsInFileA = getCommentsFromFile(fileARelativePath, false);

		String fileBRelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getNewPath());
		List<CodeComment> commentsInFileB = getCommentsFromFile(fileBRelativePath, true);

		Map<Edit, List<KnowledgeElement>> elementsByEditNew = getRationaleFromComments(true, commentsInFileB,
				changedFile);

		Map<Edit, List<KnowledgeElement>> elementsByEditOld = getRationaleFromComments(false, commentsInFileA,
				changedFile);

		returnCodeExtractionResult.diffedElementsInNewerVersion = elementsByEditNew;
		returnCodeExtractionResult.diffedElementsInOlderVersion = elementsByEditOld;

		return returnCodeExtractionResult;
	}

	private Map<Edit, List<KnowledgeElement>> getRationaleFromComments(boolean newerFile,
			List<CodeComment> commentsInFile, ChangedFile changedFile) {

		Map<Edit, List<KnowledgeElement>> returnMap = new HashMap<>();

		RationaleFromDiffCodeCommentExtractor rationaleFromDiffCodeCommentExtractor = new RationaleFromDiffCodeCommentExtractor(
				commentsInFile, changedFile.getEditList());

		while (rationaleFromDiffCodeCommentExtractor.next()) {
			returnMap.putAll(rationaleFromDiffCodeCommentExtractor.getRationaleFromComment(newerFile, returnMap));
		}

		return returnMap;
	}

	private List<CodeComment> getCommentsFromFile(String inspectedFileRelativePath, boolean fromNewerFile) {
		File resultingFile = getInspectedFileAbsolutePath(inspectedFileRelativePath, fromNewerFile);
		if (resultingFile.isFile() && resultingFile.canRead()) {
			CodeCommentParser commentParser = getCodeCommentParser(inspectedFileRelativePath);
			if (commentParser != null) {
				return commentParser.getComments(resultingFile);
			}
		}
		LOGGER.info("File or parser could not be found for file name " + resultingFile.getName());
		// TODO Replace returning null with Optional<> everywhere to avoid
		// NullPointerExceptions
		return null;
	}

	private File getInspectedFileAbsolutePath(String inspectedFileRelativePath, boolean fromNewerFile) {

		String filePathRelativeOutOfGitFolder = ".." // .. gets us out of .git folder.
				+ File.separator + inspectedFileRelativePath;

		return new File(filePathRelativeOutOfGitFolder);
	}

	/* Currently only Java parser is available. */
	private CodeCommentParser getCodeCommentParser(String resultingFileName) {
		if (resultingFileName.toLowerCase().endsWith(".java")) {
			return new JavaCodeCommentParser();
		} else {
			return null;
		}
	}

	/* Windows vs. Unix, is this method needed for diff entry paths? */
	private String adjustOSsPathSeparator(String filePath) {
		if (!"/".equals(File.separator) && filePath.indexOf("/") > -1) {
			return filePath.replaceAll("/", "\\\\");
		}
		return filePath;
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
		public Map<Edit, List<KnowledgeElement>> diffedElementsInNewerVersion = new HashMap<>();
		/* list of old elements somehow affected by the diff in a file */
		public Map<Edit, List<KnowledgeElement>> diffedElementsInOlderVersion = new HashMap<>();
	}
}
