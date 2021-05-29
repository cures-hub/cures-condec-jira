package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import de.uhd.ifi.se.decision.management.jira.extraction.RationaleFromCodeCommentExtractor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;

/**
 * Extracts decision knowledge elements in the list of comments of one
 * {@link ChangedFile} associated with the diff change. Extracted decision
 * knowledge elements contain the notion about their source within the source
 * file. Extracted rationale from comments is matched against diff entries.
 */
public class RationaleFromDiffCodeCommentExtractor {

	private EditList editList;
	private List<CodeComment> commentsInFile;
	private int fileCursor = -1;

	public RationaleFromDiffCodeCommentExtractor(List<CodeComment> comments, EditList editList) {
		this.commentsInFile = comments;
		this.editList = editList;
	}

	/**
	 * Moves comment cursor forward for the newer or older version file comments.
	 *
	 * @return: success if cursor at commentsInFile comment exists.
	 */
	public boolean next() {
		fileCursor++;
		return commentsInFile != null && (fileCursor + 1) <= commentsInFile.size();
	}

	/**
	 * Extracts rationale from current comment. Makes a distinction if rationale was
	 * found within or outside of an edit.
	 *
	 * @param: comes
	 *             the comment from the newer file version instead of older?
	 * @return: list of decision knowledge elements found in a comment.
	 */
	public Map<Edit, List<KnowledgeElement>> getRationaleFromComment(boolean newerFile,
			Map<Edit, List<KnowledgeElement>> elementsInSingleComment) {

		int cursor = fileCursor;
		List<CodeComment> comments = commentsInFile;
		if ((cursor + 1) <= comments.size()) {
			CodeComment currentComment = comments.get(cursor);
			/**
			 * @issue A problem was observed within changes of branch
			 *        refs/remotes/origin/CONDEC-503.rest.API.feature.branch.rationale for
			 *        change on old JAVA file
			 *        src/main/java/de/uhd/../jira/extraction/impl/GitClient.java
			 *        https://github.com/cures-hub/cures-condec-jira/pull/147/
			 *        commits/847c56aaa0e71ee4c2bdf9d8e674f9dd92bf77b9
			 *        #diff-1e393b83bbc1e0b69baddee0f2897586L473 at lines 472 and 473. The
			 *        DECISION rationale was written on two single line comments, but only
			 *        text on the 1st line was taken over. 2nd line will not be classified
			 *        as part of the rationale.
			 * 
			 * @alternative Merge neighboured single line comments into commit blocks! Only
			 *              when they start at the same column and their line distance is 1.
			 * @pro less intrusive, tolerates developers' "mistakes" in comment usage *
			 * @con propagates bad habits
			 * 
			 * @decision It is expected that multi line comments should be used for storing
			 *           rationale with multi line texts! No actions should be taken. *
			 * @pro teaches developers a lesson to use comments correctly. *
			 * @con not user friendly. Cannot assume every developer is using the comment
			 *      options of a language as intended.
			 */
			RationaleFromCodeCommentExtractor rationaleFromCodeComment = new RationaleFromCodeCommentExtractor(
					currentComment);
			List<KnowledgeElement> commentRationaleElements = rationaleFromCodeComment.getElements();

			// distinct rationale between changed and unchanged, only for newer version
			List<Edit> commentEdits = getEditsOnComment(currentComment, newerFile);
			// comment parts within diff
			if (commentEdits.size() > 0) {
				for (Edit edit : commentEdits) {
					List<KnowledgeElement> rationaleWithinEdit = getRationaleIntersectingEdit(edit,
							commentRationaleElements, newerFile);
					if (elementsInSingleComment.containsKey(edit)) {
						rationaleWithinEdit.addAll(elementsInSingleComment.get(edit));
					}
					elementsInSingleComment.put(edit, rationaleWithinEdit);

					// subtract edit-intersecting rationale from list of all rationale in a comment
					if (newerFile) {
						commentRationaleElements.removeAll(rationaleWithinEdit);
					}
				}
			}

			// return non-interseting elements
			if (newerFile && commentRationaleElements.size() > 0) {

				if (elementsInSingleComment.containsKey(null)) {
					commentRationaleElements.addAll(elementsInSingleComment.get(null));
				}
				elementsInSingleComment.put(null, commentRationaleElements);
			}

		}
		return elementsInSingleComment;
	}

	private List<KnowledgeElement> getRationaleIntersectingEdit(Edit edit, List<KnowledgeElement> rationaleElements,
			boolean newerFile) {
		List<KnowledgeElement> filteredRationaleElements = new ArrayList<>();
		for (KnowledgeElement rationaleElement : rationaleElements) {
			if (doesRationaleIntersectWithEdit(rationaleElement, edit, newerFile)) {
				filteredRationaleElements.add(rationaleElement);
			}
		}
		return filteredRationaleElements;
	}

	private boolean doesRationaleIntersectWithEdit(KnowledgeElement rationaleElement, Edit edit, boolean newerFile) {
		int rationaleStart = RationaleFromCodeCommentExtractor.getRationaleStartLineInCode(rationaleElement);
		int rationaleEnd = RationaleFromCodeCommentExtractor.getRationaleEndLineInCode(rationaleElement);

		/*
		 * if only line 10 changes in the old file the Edit object will then have
		 * getBeginA() == 9 ; getEndA() == 10
		 * 
		 * Edit counts lines starting by 0, DecisionKnowledgeElement stores line
		 * information beginning from 1. To adapt to this fact, getBeginA() will be
		 * increased by one and below calculation should be fine.
		 */

		int editBegin = edit.getBeginA() + 1;
		int editEnd = edit.getEndA();

		if (newerFile) {
			editBegin = edit.getBeginB() + 1;
			editEnd = edit.getEndB();
		}

		// begin border inside rationale
		if (editBegin >= rationaleStart && editBegin <= rationaleEnd) {
			return true;
		}
		// end border inside rationale
		if (editEnd >= rationaleStart && editEnd <= rationaleEnd) {
			return true;
		}
		// edit overlaps rationale
		return editBegin <= rationaleStart && editEnd >= rationaleEnd;
	}

	/* fetches list of edits which affected the comment */
	private List<Edit> getEditsOnComment(CodeComment comment, boolean newerFile) {
		return editList.stream().filter(edit -> {
			int begin = edit.getBeginA();
			int end = edit.getEndA();
			if (newerFile) {
				begin = edit.getBeginB();
				end = edit.getEndB();
			}
			return
			// change's end within the comment
			(end >= comment.getBeginLine() && end <= comment.getEndLine()) ||
			// change's begin within the comment
			(begin >= comment.getBeginLine() && begin <= comment.getEndLine()) ||
			// change overlaps comment
			(begin <= comment.getBeginLine() && end >= comment.getEndLine());

		}).collect(Collectors.toList());
	}

	/**
	 * commentsInNewer/OlderFile contains a list of comments sorted by their
	 * appearance order in source file, therefore already visited items in editList
	 * could be removed from that list to improve algorithm runtime for files with
	 * many small changes.
	 */
	/*
	 * private void removeNotNeededEdits(CodeComment currentComment) {
	 * 
	 * editList.removeIf(edit -> edit.getEndB() < currentComment.beginLine); }
	 */
}
