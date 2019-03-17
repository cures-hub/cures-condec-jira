package de.uhd.ifi.se.decision.management.jira.model.text;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.text.impl.SentenceImpl;

/**
 * Interface for textual parts (substrings) of JIRA issue comments. These parts
 * can either be relevant decision knowledge elements or irrelevant text.
 */
@JsonDeserialize(as = SentenceImpl.class)
public interface Sentence extends PartOfText {

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
	 * @return id of the JIRA issue comment.
	 */
	long getCommentId();

	/**
	 * Get the JIRA issue comment that the decision knowledge element or irrelevant
	 * text is part of.
	 * 
	 * @see MutableComment
	 * @return JIRA issue comment.
	 */
	MutableComment getComment();
}
