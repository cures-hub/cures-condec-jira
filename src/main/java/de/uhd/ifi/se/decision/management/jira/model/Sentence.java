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
	 * Set whether the text within the JIRA issue comment is decision knowledge,
	 * i.e., relevant. Uses an estimated value for relevance.
	 * 
	 * @param estimatedRelevance
	 *            1.0 if the text is decision knowledge. Values less than 1
	 *            represent irrelevant text.
	 */
	void setRelevant(double estimatedRelevance);

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
	 * Set the knowledge type of the text within the JIRA issue comment if it is
	 * relevant decision knowledge. Uses an array of estimated values for relevance.
	 * For example: double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 } for
	 * alternative. The order is important: alternative, decision, issue, pro, and
	 * con.
	 * 
	 * @see KnowledgeType
	 * @param prediction
	 *            1.0 if the text is decision knowledge with a certain type. Values
	 *            less than 1 represent irrelevant text.
	 */
	void setType(double[] prediction);

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
	boolean isTaggedFineGrained();

	void setIssueId(long issueid);

	long getIssueId();

	boolean isPlainText();

	void setPlainText(boolean isPlainText);

	String getBody();

	void setBody(String body);
}
