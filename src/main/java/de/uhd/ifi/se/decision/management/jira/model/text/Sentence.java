package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;

/**
 * Interface for textual parts (substrings) of JIRA issue comments. These parts
 * can either be relevant decision knowledge elements or irrelevant text.
 */
@JsonDeserialize(as = SentenceImpl.class)
public interface Sentence extends DecisionKnowledgeElement {

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

	/**
	 * Set the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the JIRA issue comment.
	 * 
	 * @param startPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text starts.
	 */
	void setStartSubstringCount(int startPosition);

	/**
	 * Get the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the JIRA issue comment.
	 * 
	 * @return number of characters after that the decision knowledge element or the
	 *         irrelevant text starts.
	 */
	int getStartSubstringCount();

	/**
	 * Set the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the JIRA issue comment.
	 * 
	 * @param endPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text ends.
	 */
	void setEndSubstringCount(int endPosition);

	/**
	 * Get the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the JIRA issue comment.
	 * 
	 * @return number of characters after that the decision knowledge element or the
	 *         irrelevant text ends.
	 */
	int getEndSubstringCount();

	/**
	 * Set whether the text within the JIRA issue comment is decision knowledge,
	 * i.e., relevant.
	 * 
	 * @param isRelevant
	 *            true of the text is decision knowledge.
	 */
	void setRelevant(boolean isRelevant);

	/**
	 * Determine whether the text within the JIRA issue comment is decision
	 * knowledge, i.e., relevant.
	 * 
	 * @return true of the text is decision knowledge.
	 */
	boolean isRelevant();

	/**
	 * Set whether the classification of the text within the JIRA issue comment is
	 * manually performed, updated, or checked by a human beeing.
	 * 
	 * @param isValidated
	 *            true if the classification of the text within the JIRA issue
	 *            comment is validated.
	 */
	void setValidated(boolean isValidated);

	/**
	 * Determine whether the classification of the text within the JIRA issue
	 * comment is manually performed, updated, or checked by a human beeing.
	 * 
	 * @return true if the classification of the text within the JIRA issue comment
	 *         is validated.
	 */
	boolean isValidated();

	/**
	 * Get the length of the text (substring) within the JIRA issue comment. This is
	 * a derived method.
	 * 
	 * @return end position - start position
	 */
	int getLength();

	/**
	 * Determine whether the text within the JIRA issue comment is relevant decision
	 * knowledge with a knowledge type different than KnowledgeType.OTHER. Then, the
	 * text is tagged within the JIRA issue comment with the pattern: {knowledge
	 * type} text {knowledge type}.
	 * 
	 * @see KnowledgeType
	 * @return true if the text within the JIRA issue comment is relevant decision
	 *         knowledge with a knowledge type different than KnowledgeType.OTHER.
	 */
	boolean isTagged();

	/**
	 * Set the id of the JIRA issue that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the JIRA issue.
	 */
	void setJiraIssueId(long jiraIssueId);

	/**
	 * Get the id of the JIRA issue that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @return id of the JIRA issue.
	 */
	long getJiraIssueId();

	/**
	 * Set whether the text of the decision knowledge element or irrelevant text is
	 * plain, e.g., does not contain any code or logger ouput.
	 * 
	 * @param isPlainText
	 *            true if the text of the decision knowledge element or irrelevant
	 *            text is plain, e.g., does not contain any code or logger ouput.
	 */
	void setPlainText(boolean isPlainText);

	/**
	 * Determine whether the text of the decision knowledge element or irrelevant
	 * text is plain, e.g., does not contain any code or logger ouput.
	 * 
	 * @return true if the text of the decision knowledge element or irrelevant text
	 *         is plain, e.g., does not contain any code or logger ouput.
	 */
	boolean isPlainText();

	/**
	 * Receive the text from the JIRA issue comment.
	 * 
	 * @return text from the JIRA issue comment
	 */
	String getTextFromComment();
}
