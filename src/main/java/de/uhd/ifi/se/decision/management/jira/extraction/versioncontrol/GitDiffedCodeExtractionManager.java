package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeCommentWithRange;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.JavaCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * purpose: extract decision knowledge elements from files
 * modified by a sequence of commits.
 *
 * Decision knowledge can be documented in code using
 * following syntax inside a source code comment:
 * """
 *
 * @decKnowledgeTag: knowledge summary text
 *
 * knowledge description text after empty line
 *
 *
 * """
 *
 * where [decKnowledgeTag] belongs to set of know Knowledge Types,
 * for example issue, alternative, decision etc.
 * and where empty two lines denote the end of the decision element,
 * Observation of another tag ends the element too.
 * Comment end ends also the decision element.
 *
 * This class will:
 * 1) fetch with gitClient necessary files from code repository,
 * 2) delegate comment extraction to specialized classes and
 * 3) extract evolution of knowledge elements in files.
 */
public class GitDiffedCodeExtractionManager {
	/*
	 * [issue]Modified files may add, modify or delete rationale.
	 * For extraction of rationale on modified files their contents
	 * from the base and changed versions are required.
	 * How should both versions of affected files be accessed?{/issue]
	 *
	 * [decision]Use two gitClientImpl instances each checked out
	 * at different commit of the diff range![/decision]
	 * [pro]no additional checkouts need to be performed by the gitClientImpl[/pro]
	 * [con]requires on more gitClientImpl object in the memory[/con]
	 *
	 * [alternative]Switch between commits using one gitClientImpl
	 * instance[/alternative]
	 * [con]frequent checkouts take time[/con]
	 * [con]requires implementation of another special mode in the
	 * client[/con]
	 *
	 */

	private final GitClient gitClientCheckedOutAtDiffStart;
	private final GitClient gitClientCheckedOutAtDiffEnd;
	private final Map<DiffEntry, EditList> diffEntries;

	private static final Logger LOGGER = LoggerFactory.getLogger(GitDiffedCodeExtractionManager.class);

	// may include null values for keys!
	private Map<DiffEntry, CodeExtractionResult> results = new HashMap<>();

	/*
	 * Passing only the git client checked out at the last commit of the diff
	 * limits knowledge extraction to output of new or partial extraction
	 * of modified elements. Noticing deletions of previously existing elements,
	 *  will not be possible to detect.
	 */

	public GitDiffedCodeExtractionManager(Map<DiffEntry, EditList> diffEntries
			, GitClient GitClientAtDiffEnd) {
		gitClientCheckedOutAtDiffStart = null;
		gitClientCheckedOutAtDiffEnd = GitClientAtDiffEnd;
		this.diffEntries = diffEntries;
		processEntries();
	}

	/*
	 * TODO: Below constructor will be used later, as the class will evolve.
	 */
	public GitDiffedCodeExtractionManager(Map<DiffEntry, EditList> diffEntries
			, GitClient GitClientAtDiffEnd
			, GitClient GitClientAtDiffStart) {
		gitClientCheckedOutAtDiffStart = GitClientAtDiffStart;
		gitClientCheckedOutAtDiffEnd = GitClientAtDiffEnd;
		this.diffEntries = diffEntries;
		//no actions implemented yet
	}

	public List<DecisionKnowledgeElement> getNewDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> resultValues = new ArrayList<>();

		if (results.size() > 0) {
			for (Map.Entry<DiffEntry, CodeExtractionResult> dEntry
					: results.entrySet()) {
				String newPath = dEntry.getKey().getNewPath();
				if (dEntry.getValue() != null) {
					Map<Edit, List<DecisionKnowledgeElement>> codeExtractionResult =
							dEntry.getValue().elementsInNewerVersion;
					if (codeExtractionResult.size() > 0) {
						for (Map.Entry<Edit, List<DecisionKnowledgeElement>> editListEntry
								: codeExtractionResult.entrySet()) {
							resultValues.addAll(editListEntry.getValue().stream().map(d -> {
										d.setKey(newPath + "_" + d.getKey());
										return d;
									}
							).collect(Collectors.toList()));
						}
					}
				}
			}
		}
		return resultValues;
	}

	private void processEntries() {
		for (Map.Entry<DiffEntry, EditList> entry : diffEntries.entrySet()) {
			results.put(entry.getKey(), processEntry(entry));
		}
	}

	private CodeExtractionResult processEntry(Map.Entry<DiffEntry, EditList> diffEntry) {
		switch (diffEntry.getKey().getChangeType()) {
			/* ADD and DELETE are easiest to implement
			 * others are more complex.
			 * Begin implementation with ADD.
			 * */
			case ADD:
				return processADDEntry(diffEntry);
			case MODIFY: // gitClientCheckedOutAtDiffStart must exist
			case DELETE: // gitClientCheckedOutAtDiffStart must exist
			case RENAME: // behaves like MODIFY ?
			case COPY: // ??
			default:
				// change type support not implemented
				break;
		}
		LOGGER.info("Diff change type is not implemented: "
				+ diffEntry.getKey().getChangeType().toString());
		return null;
	}

	/* ADD does not require gitClientCheckedOutAtDiffStart */
	private CodeExtractionResult processADDEntry(Map.Entry<DiffEntry, EditList> diffEntry) {
		CodeExtractionResult returnCodeExtractionResult = new CodeExtractionResult();

		// TODO: check if the call to adjustOSsPathSeparator is required?
		String fileRelativePath = adjustOSsPathSeparator(diffEntry.getKey().getNewPath());
		List<CodeCommentWithRange> commentsInFile = getCommentsFromFile(fileRelativePath);

		Map<Edit, List<DecisionKnowledgeElement>> elementsByEdit =
				getRationaleFromComments(true, commentsInFile, diffEntry);

		returnCodeExtractionResult.elementsInNewerVersion = elementsByEdit;

		return returnCodeExtractionResult;
	}

	private Map<Edit, List<DecisionKnowledgeElement>> getRationaleFromComments(
			boolean newerFile
			, List<CodeCommentWithRange> commentsInFile
			, Map.Entry<DiffEntry, EditList> diffEntry) {

		Map<Edit, List<DecisionKnowledgeElement>> returnMap = new HashMap<>();

		List<CodeCommentWithRange> commentsInNewerFile = new ArrayList<>();
		List<CodeCommentWithRange> commentsInOlderFile = new ArrayList<>();
		if (newerFile) {
			commentsInNewerFile = commentsInFile;
		} else {
			commentsInOlderFile = commentsInFile;
		}

		RationaleFromDiffCodeCommentExtractor rationaleFromDiffCodeCommentExtractor =
				new RationaleFromDiffCodeCommentExtractor(commentsInOlderFile, commentsInNewerFile, diffEntry.getValue());

		while (rationaleFromDiffCodeCommentExtractor.next(true)) {
			returnMap.putAll(rationaleFromDiffCodeCommentExtractor.getRationaleFromComment(true
					, returnMap));
		}

		return returnMap;
	}

	private List<CodeCommentWithRange> getCommentsFromFile(String inspectedFileRelativePath) {
		File resultingFile = getInspectedFileAbsolutePath(inspectedFileRelativePath);
		if (!resultingFile.isFile()) {
			LOGGER.error("Expected file "
					+ resultingFile.getAbsolutePath() + " to exist!");
			return null;
		} else if (!resultingFile.canRead()) {
			LOGGER.error("Expected file "
					+ resultingFile.getAbsolutePath() + " to be readable!");
			return null;
		} else {
			CodeCommentParser commentParser = getCodeCommentParser(inspectedFileRelativePath);
			if (commentParser == null) {
				LOGGER.info("No parser could be found for file "
						+ resultingFile.getName());
				return null;
			} else {
				return commentParser.getComments(resultingFile);
			}
		}
	}

	private File getInspectedFileAbsolutePath(String inspectedFileRelativePath) {
		String filePathRelativeOutOfGitFolder = ".." //.. gets us out of .git folder.
				+ File.separator
				+ inspectedFileRelativePath;
		return new File(gitClientCheckedOutAtDiffEnd.getDirectory()
				, filePathRelativeOutOfGitFolder);
	}

	/* Currently only Java parser is available. */
	private CodeCommentParser getCodeCommentParser(String resultingFileName) {
		if (resultingFileName.toLowerCase().endsWith(".java")) {
			return new JavaCodeCommentParser();
		} else {
			return null;
		}
	}

	/* Windows vs. Unix, is this method needed for diff entry paths?*/
	private String adjustOSsPathSeparator(String newPath) {
		if (newPath.indexOf("/") > -1 && !"/".equals(File.separator)) {
			return newPath.replaceAll("/", File.separator);
		}
		return newPath;
	}

	/* Stores old and new rationale elements in maps with edits as key,
	 * If the old and new rationale can be found under the same edit,
	 * possibility of conflict exists
	 */
	private class CodeExtractionResult {
		/* list of elements modified/created after diff */
		public Map<Edit, List<DecisionKnowledgeElement>> elementsInNewerVersion = new HashMap<>();
		/* list of old elements somehow affected by the diff */
		public Map<Edit, List<DecisionKnowledgeElement>> elementsInOlderVersion = new HashMap<>();
	}
}
