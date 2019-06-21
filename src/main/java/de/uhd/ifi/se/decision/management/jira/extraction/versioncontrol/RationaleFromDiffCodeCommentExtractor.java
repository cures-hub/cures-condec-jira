package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.CodeCommentWithRange;
import de.uhd.ifi.se.decision.management.jira.extraction.RationaleFromCodeCommentExtractor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * purpose: extract decision knowledge elements in the list of comments
 * of one source file associated with the diff change.
 * Extracted decision knowledge elements contain
 * the notion about their source within the source file.
 * Extracted rationale from comments is matched against diff entries.
 */
public class RationaleFromDiffCodeCommentExtractor {

	private EditList editList;
	private List<CodeCommentWithRange> commentsInNewerFile;
	private List<CodeCommentWithRange> commentsInOlderFile;
	private int cursorNewerFile = -1;
	private int cursorOlderFile = -1;

	public RationaleFromDiffCodeCommentExtractor(List<CodeCommentWithRange> commentsInOlderFile
			, List<CodeCommentWithRange> commentsInNewerFile
			, EditList editList) {
		this.commentsInNewerFile = commentsInNewerFile;
		this.commentsInOlderFile = commentsInOlderFile;
		this.editList = editList;
	}

	/**
	 * Moves comment cursor forward for the newer or older version file comments.
	 *
	 * @param: move newer instead of older file cursor?
	 * @return: success if cursor at nextInNewerFile comment exists.
	 */
	public boolean next(boolean forNewerFile) {
		if (forNewerFile) {
			cursorNewerFile++;
			return commentsInNewerFile != null
					&& (cursorNewerFile + 1) <= commentsInNewerFile.size();
		} else {
			cursorOlderFile++;
			return commentsInOlderFile != null
					&& (cursorOlderFile + 1) <= commentsInOlderFile.size();
		}
	}

	/**
	 * Extracts rationale from current comment.
	 *
	 * @param: comes the comment from the newer file version instead of older?
	 * @return: list of decision knowledge elements found in a comment
	 */
	public Map<Edit, List<DecisionKnowledgeElement>> getRationaleFromComment(boolean newerFile
		, Map<Edit, List<DecisionKnowledgeElement>> elementsInSingleComment) {

		int cursor = cursorOlderFile;
		List<CodeCommentWithRange> comments = commentsInOlderFile;
		if (newerFile) {
			cursor = cursorNewerFile;
			comments = commentsInNewerFile;
		}
		if ((cursor + 1) <= comments.size()) {
			CodeCommentWithRange currentComment = comments.get(cursor);

			// inspect comment only if it was within diff range
			List<Edit> commentEdits = getEditsOnComment(currentComment, newerFile);
			if (commentEdits.size() > 0) {
				RationaleFromCodeCommentExtractor rationaleFromCodeComment =
						new RationaleFromCodeCommentExtractor(currentComment);
				List<DecisionKnowledgeElement> rationaleElements =
						rationaleFromCodeComment.getElements();
				for (Edit edit : commentEdits) {
					List<DecisionKnowledgeElement> rationale = getRationaleIntersectingEdit(edit
							, rationaleElements, newerFile);
					if ( elementsInSingleComment.containsKey(edit)) {
						rationale.addAll(elementsInSingleComment.get(edit));
					}
					elementsInSingleComment.put(edit, rationale);
				}
			}
		}
		return elementsInSingleComment;
	}

	private List<DecisionKnowledgeElement> getRationaleIntersectingEdit(Edit edit
			, List<DecisionKnowledgeElement> rationaleElements, boolean newerFile) {
		List<DecisionKnowledgeElement> filteredRationaleElements = new ArrayList<>();
		for (DecisionKnowledgeElement rationaleElement : rationaleElements) {
			if (doesRationaleIntersectWithEdit(rationaleElement, edit, newerFile)) {
				filteredRationaleElements.add(rationaleElement);
			}
		}
		return filteredRationaleElements;
	}

	private boolean doesRationaleIntersectWithEdit(DecisionKnowledgeElement rationaleElement, Edit edit, boolean newerFile) {
		int rationaleStart = RationaleFromCodeCommentExtractor.getRationaleStartLineInCode(rationaleElement);
		int rationaleEnd = RationaleFromCodeCommentExtractor.getRationaleEndLineInCode(rationaleElement);
		int editBegin = edit.getBeginA();
		int editEnd = edit.getEndA();

		if (newerFile) {
			editBegin = edit.getBeginB();
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
		if (editBegin <= rationaleStart && editEnd >= rationaleEnd) {
			return true;
		}

		return false;
	}

	/* fetches list of edits which affected the comment */
	private List<Edit> getEditsOnComment(CodeCommentWithRange comment, boolean newerFile) {
		return editList.stream().filter(edit -> {
			int begin = edit.getBeginA();
			int end = edit.getEndA();
			if (newerFile) {
				begin = edit.getBeginB();
				end = edit.getEndB();
			}
			return
			// change's end within the comment
			(end >= comment.beginLine
					&& end <= comment.endLine)
			||
			// change's begin within the comment
			(begin >= comment.beginLine
					&& begin <= comment.endLine)
			||
			// change overlaps comment
			(begin <= comment.beginLine
					&& end >= comment.endLine);

				}
		).collect(Collectors.toList());
	}

	/**
	 *  commentsInNewer/OlderFile contains a list of comments sorted by their
	 *  appearance order in source file, therefore already visited items in editList
	 *  could be removed from that list to improve algorithm runtime for files with
	 *  many small changes.
	 */
	/*private void removeNotNeededEdits(CodeCommentWithRange currentComment) {

		editList.removeIf(edit ->
				edit.getEndB() < currentComment.beginLine);
	}*/
}
