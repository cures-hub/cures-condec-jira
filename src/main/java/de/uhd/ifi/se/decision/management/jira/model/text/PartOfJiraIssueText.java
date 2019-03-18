package de.uhd.ifi.se.decision.management.jira.model.text;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;

/**
 * Interface for textual parts (substrings) of JIRA issue comments or the
 * description. These parts can either be relevant decision knowledge elements
 * or irrelevant text.
 */
@JsonDeserialize(as = PartOfJiraIssueTextImpl.class)
public interface PartOfJiraIssueText extends PartOfText {

	/**
	 * Get the JIRA issue description that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @see MutableIssue
	 * @return JIRA issue description as a String.
	 */
	String getJiraIssueDescription();

	/**
	 * Set the id of the JIRA issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the JIRA issue comment.
	 */
	void setCommentId(long id);

	/**
	 * Get the id of the JIRA issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @return id of the JIRA issue comment. Returns 0 if the part of text is within
	 *         the description.
	 */
	long getCommentId();

	/**
	 * Get the JIRA issue comment that the decision knowledge element or irrelevant
	 * text is part of.
	 * 
	 * @see MutableComment
	 * @return JIRA issue comment. Returns null if the part of text is within the
	 *         description.
	 */
	MutableComment getComment();
}
