package de.uhd.ifi.se.decision.management.jira.model.text;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;

/**
 * Interface for textual parts (substrings) of Jira issue comments or the
 * description. These parts can either be relevant decision knowledge elements
 * or irrelevant text.
 */
@JsonDeserialize(as = PartOfJiraIssueTextImpl.class)
public interface PartOfJiraIssueText extends PartOfText {

	/**
	 * Get the Jira issue description that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @see MutableIssue
	 * @return Jira issue description as a String.
	 */
	String getJiraIssueDescription();

	/**
	 * Set the id of the Jira issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the Jira issue comment.
	 */
	void setCommentId(long id);

	/**
	 * Get the id of the Jira issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @return id of the Jira issue comment. Returns 0 if the part of text is within
	 *         the description.
	 */
	long getCommentId();

	/**
	 * Get the Jira issue comment that the decision knowledge element or irrelevant
	 * text is part of.
	 * 
	 * @see MutableComment
	 * @return Jira issue comment. Returns null if the part of text is within the
	 *         description.
	 */
	MutableComment getComment();

	/**
	 * Determines whether the textual parts (substrings) of Jira issue comments or
	 * the description is valid.
	 * 
	 * @return true if the object is valid.
	 */
	boolean isValid();
}
