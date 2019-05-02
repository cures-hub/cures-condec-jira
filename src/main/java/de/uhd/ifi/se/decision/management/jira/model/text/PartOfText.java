package de.uhd.ifi.se.decision.management.jira.model.text;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * Interface for textual parts (substrings) of natural language text. These
 * parts can either be relevant decision knowledge elements or irrelevant text.
 */
public interface PartOfText extends DecisionKnowledgeElement {

	/**
	 * Set the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the entire text.
	 * 
	 * @param startPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text starts.
	 */
	void setStartPosition(int startPosition);

	/**
	 * Get the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the entire text.
	 * 
	 * @return number of characters after that the decision knowledge element or the
	 *         irrelevant text starts.
	 */
	int getStartPosition();

	/**
	 * Set the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the entire text.
	 * 
	 * @param endPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text ends.
	 */
	void setEndPosition(int endPosition);

	/**
	 * Get the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the entire text.
	 * 
	 * @return number of characters after that the decision knowledge element or the
	 *         irrelevant text ends.
	 */
	int getEndPosition();

	/**
	 * Set whether the part of the text is decision knowledge, i.e., relevant.
	 * 
	 * @param isRelevant
	 *            true of the text is decision knowledge.
	 */
	void setRelevant(boolean isRelevant);

	/**
	 * Determine whether the part of the text is decision knowledge, i.e., relevant.
	 * 
	 * @return true of the text is decision knowledge.
	 */
	boolean isRelevant();

	/**
	 * Set whether the classification of the part of the text is manually performed,
	 * updated, or checked by a human beeing.
	 * 
	 * @param isValidated
	 *            true if the classification of the text within the JIRA issue
	 *            comment is validated.
	 */
	void setValidated(boolean isValidated);

	/**
	 * Determine whether the classification of the part of the text is manually
	 * performed, updated, or checked by a human beeing.
	 * 
	 * @return true if the classification of the text within the JIRA issue comment
	 *         is validated.
	 */
	boolean isValidated();

	/**
	 * Get the length of the part of the text (substring). This is a derived method.
	 * 
	 * @return end position - start position
	 */
	int getLength();

	/**
	 * Determine whether the part of the text is relevant decision knowledge with a
	 * knowledge type different than KnowledgeType.OTHER. Then, the part of the text
	 * is tagged with the pattern: {knowledge type} text {knowledge type}.
	 * 
	 * @see KnowledgeType
	 * @return true if the part of the text is relevant decision knowledge with a
	 *         knowledge type different than KnowledgeType.OTHER.
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
	 * Get the JIRA issue that the decision knowledge element or irrelevant text is
	 * part of.
	 * 
	 * @return JIRA issue.
	 */
	Issue getJiraIssue();

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
	 * Return the part of the text.
	 * 
	 * @return part of the text.
	 */
	String getText();
}
