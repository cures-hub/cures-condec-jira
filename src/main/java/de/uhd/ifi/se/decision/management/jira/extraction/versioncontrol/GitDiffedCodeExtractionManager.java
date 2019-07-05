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

import de.uhd.ifi.se.decision.management.jira.extraction.CodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.JavaCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * purpose: extract decision knowledge elements from files modified by a
 * sequence of commits.
 *
 * Decision knowledge can be documented in code using following syntax inside a
 * source code comment: """
 *
 * @decKnowledgeTag: knowledge summary text
 *
 *                   knowledge description text after empty line
 *
 *
 *                   """
 *
 *                   where [decKnowledgeTag] belongs to set of
 *                   {@link KnowledgeType}s, for example issue, alternative,
 *                   decision etc. and where empty two lines denote the end of
 *                   the decision knowledge element. The observation of another
 *                   tag ends the element too. Comment end ends also the
 *                   decision knowledge element.
 *
 *                   This class will: 1) fetch with gitClient necessary files
 *                   from code repository, 2) delegate comment extraction to
 *                   specialized classes and 3) extract evolution of knowledge
 *                   elements in files.
 */
public class GitDiffedCodeExtractionManager {
	private static final String OLD_FILE_SYMBOL_PREPENDER = "~";
	/*
	 * @issue: Modified files may add, modify or delete rationale. For extraction of
	 * rationale on modified files their contents from the base and changed versions
	 * are required. How should both versions of affected files be accessed?
	 *
	 * @decision: Use two gitClientImpl instances each checked out at different
	 * commit of the diff range!
	 * 
	 * @pro: no additional checkouts need to be performed by the gitClientImpl
	 * 
	 * @con: requires on more gitClientImpl object in the memory
	 *
	 * @alternative: Switch between commits using one gitClientImpl instance!
	 * 
	 * @con: frequent checkouts take time
	 * 
	 * @con: requires implementation of another special mode in the client
	 *
	 */

	private final GitClient gitClientCheckedOutAtDiffStart;
	private final GitClient gitClientCheckedOutAtDiffEnd;
	private final Diff diff;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitDiffedCodeExtractionManager.class);

	// may include null values for keys!
	// TODO Consider changing to Map<ChangedFile, CodeExtractionResult> or add
	// CodeExtractionResult as an attribute to the ChangedFile class
	private Map<DiffEntry, CodeExtractionResult> results = new HashMap<>();

	public GitDiffedCodeExtractionManager(Diff diff, GitClient GitClientAtDiffEnd, GitClient GitClientAtDiffStart) {
		gitClientCheckedOutAtDiffStart = GitClientAtDiffStart;
		gitClientCheckedOutAtDiffEnd = GitClientAtDiffEnd;
		this.diff = diff;
		processEntries();
	}

	public List<DecisionKnowledgeElement> getNewDecisionKnowledgeElements() {
		return getNewOrOldDecisionKnowledgeElements(true);
	}

	public List<DecisionKnowledgeElement> getOldDecisionKnowledgeElements() {
		return getNewOrOldDecisionKnowledgeElements(false);
	}

	private List<DecisionKnowledgeElement> getNewOrOldDecisionKnowledgeElements(boolean getNew) {
		List<DecisionKnowledgeElement> resultValues = new ArrayList<>();

		if (results.size() > 0) {
			for (Map.Entry<DiffEntry, CodeExtractionResult> dEntry : results.entrySet()) {
				String newPath;
				if (getNew) {
					newPath = dEntry.getKey().getNewPath();
				} else {
					newPath = OLD_FILE_SYMBOL_PREPENDER + dEntry.getKey().getOldPath();
				}
				if (dEntry.getValue() != null) {
					Map<Edit, List<DecisionKnowledgeElement>> codeExtractionResult;
					if (getNew) {
						codeExtractionResult = dEntry.getValue().diffedElementsInNewerVersion;
					} else {
						codeExtractionResult = dEntry.getValue().diffedElementsInOlderVersion;
					}
					if (codeExtractionResult.size() > 0) {
						for (Map.Entry<Edit, List<DecisionKnowledgeElement>> editListEntry : codeExtractionResult
								.entrySet()) {
							resultValues.addAll(editListEntry.getValue().stream().map(d -> {
								d.setKey(newPath + GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR
										+ String.valueOf(dEntry.getValue().sequence)
										+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + editListEntry.getKey().toString()
										+ GitDecXtract.RAT_KEY_COMPONENTS_SEPARATOR + d.getKey());
								return d;
							}).collect(Collectors.toList()));
						}
					}
				}
			}
		}
		return resultValues;
	}

	private void processEntries() {
		int entrySequenceNumber = 0;
		for (ChangedFile changedFile : diff.getChangedFiles()) {
			CodeExtractionResult entryResults = processEntry(changedFile);

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

		Map<Edit, List<DecisionKnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile,
				commentsInFile, changedFile);

		returnCodeExtractionResult.diffedElementsInNewerVersion = elementsByEdit;

		return returnCodeExtractionResult;
	}

	/* DELETE does not require gitClientCheckedOutAtDiffEnd */
	private CodeExtractionResult processDeleteEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();
		boolean fromNewerFile = false;

		String fileARelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getNewPath());
		List<CodeComment> commentsInFile = getCommentsFromFile(fileARelativePath, fromNewerFile);

		Map<Edit, List<DecisionKnowledgeElement>> elementsByEdit = getRationaleFromComments(fromNewerFile,
				commentsInFile, changedFile);

		returnCodeExtractionResult.diffedElementsInOlderVersion = elementsByEdit;

		return returnCodeExtractionResult;
	}

	private CodeExtractionResult processModifyEntryEdits(ChangedFile changedFile) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();

		String fileARelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getOldPath());
		List<CodeComment> commentsInFileA = getCommentsFromFile(fileARelativePath, false);

		String fileBRelativePath = adjustOSsPathSeparator(changedFile.getDiffEntry().getNewPath());
		List<CodeComment> commentsInFileB = getCommentsFromFile(fileBRelativePath, true);

		Map<Edit, List<DecisionKnowledgeElement>> elementsByEditNew = getRationaleFromComments(true, commentsInFileB,
				changedFile);

		Map<Edit, List<DecisionKnowledgeElement>> elementsByEditOld = getRationaleFromComments(false, commentsInFileA,
				changedFile);

		returnCodeExtractionResult.diffedElementsInNewerVersion = elementsByEditNew;
		returnCodeExtractionResult.diffedElementsInOlderVersion = elementsByEditOld;

		return returnCodeExtractionResult;
	}

	private Map<Edit, List<DecisionKnowledgeElement>> getRationaleFromComments(boolean newerFile,
			List<CodeComment> commentsInFile, ChangedFile changedFile) {

		Map<Edit, List<DecisionKnowledgeElement>> returnMap = new HashMap<>();

		List<CodeComment> commentsInNewerFile = new ArrayList<>();
		List<CodeComment> commentsInOlderFile = new ArrayList<>();
		if (newerFile) {
			commentsInNewerFile = commentsInFile;
		} else {
			commentsInOlderFile = commentsInFile;
		}

		RationaleFromDiffCodeCommentExtractor rationaleFromDiffCodeCommentExtractor = new RationaleFromDiffCodeCommentExtractor(
				commentsInOlderFile, commentsInNewerFile, changedFile.getEditList());

		while (rationaleFromDiffCodeCommentExtractor.next(newerFile)) {
			returnMap.putAll(rationaleFromDiffCodeCommentExtractor.getRationaleFromComment(newerFile, returnMap));
		}

		return returnMap;
	}

	private List<CodeComment> getCommentsFromFile(String inspectedFileRelativePath, boolean fromNewerFile) {
		File resultingFile = getInspectedFileAbsolutePath(inspectedFileRelativePath, fromNewerFile);
		if (!resultingFile.isFile()) {
			LOGGER.error("Expected file " + resultingFile.getAbsolutePath() + " to exist!");
			return null;
		} else if (!resultingFile.canRead()) {
			LOGGER.error("Expected file " + resultingFile.getAbsolutePath() + " to be readable!");
			return null;
		} else {
			CodeCommentParser commentParser = getCodeCommentParser(inspectedFileRelativePath);
			if (commentParser == null) {
				LOGGER.info("No parser could be found for file " + resultingFile.getName());
				return null;
			} else {
				return commentParser.getComments(resultingFile);
			}
		}
	}

	private File getInspectedFileAbsolutePath(String inspectedFileRelativePath, boolean fromNewerFile) {

		String filePathRelativeOutOfGitFolder = ".." // .. gets us out of .git folder.
				+ File.separator + inspectedFileRelativePath;

		if (fromNewerFile) {
			return new File(gitClientCheckedOutAtDiffEnd.getDirectory(), filePathRelativeOutOfGitFolder);
		} else {
			return new File(gitClientCheckedOutAtDiffStart.getDirectory(), filePathRelativeOutOfGitFolder);
		}
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

	/*
	 * Stores old and new rationale elements in maps with edits as key, If the old
	 * and new rationale can be found under the same edit, possibility of conflict
	 * exists
	 */
	private class CodeExtractionResult {
		public int sequence = -1;
		/* list of elements modified/created with diff in a file */
		public Map<Edit, List<DecisionKnowledgeElement>> diffedElementsInNewerVersion = new HashMap<>();
		/* list of all elements present in file after diff */
		public Map<DiffEntry, List<DecisionKnowledgeElement>> allElementsInNewerVersion = new HashMap<>();
		/* list of old elements somehow affected by the diff in a file */
		public Map<Edit, List<DecisionKnowledgeElement>> diffedElementsInOlderVersion = new HashMap<>();
	}
}
