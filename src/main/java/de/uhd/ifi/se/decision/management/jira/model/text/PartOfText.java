package de.uhd.ifi.se.decision.management.jira.model.text;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;

/**
 * Models textual parts (substrings) of natural language text. These parts can
 * either be relevant decision knowledge elements or irrelevant text.
 */
public class PartOfText extends KnowledgeElementImpl {

	private int startPosition;
	private int endPosition;
	private boolean isRelevant;
	private boolean isValidated;
	private boolean isPlainText;
	private long jiraIssueId;

	public PartOfText() {

	}

	public PartOfText(int startPosition, int endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	/**
	 * @return true if the text is decision knowledge.
	 */
	public boolean isRelevant() {
		return this.isRelevant;
	}

	/**
	 * Sets whether the part of the text is decision knowledge, i.e., relevant.
	 * 
	 * @param isRelevant
	 *            true of the text is decision knowledge.
	 */
	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	/**
	 * @return true if the classification of the text within the Jira issue comment
	 *         is validated, i.e., is manually performed, updated, or checked by a
	 *         human beeing.
	 */
	public boolean isValidated() {
		return this.isValidated;
	}

	/**
	 * Sets whether the classification of the part of the text is manually
	 * performed, updated, or checked by a human beeing.
	 * 
	 * @param isValidated
	 *            true if the classification of the text within the Jira issue
	 *            comment is validated.
	 */
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}

	/**
	 * Determines whether the part of the text is relevant decision knowledge with a
	 * knowledge type different than KnowledgeType.OTHER. Then, the part of the text
	 * is tagged with the pattern: {knowledge type} text {knowledge type}.
	 * 
	 * @see KnowledgeType
	 * @return true if the part of the text is relevant decision knowledge with a
	 *         knowledge type different than KnowledgeType.OTHER.
	 */
	public boolean isTagged() {
		return this.getType() != KnowledgeType.OTHER;
	}

	/**
	 * @return start position (number of characters) of the decision knowledge
	 *         element or the irrelevant text within the entire text.
	 */
	public int getStartPosition() {
		return this.startPosition;
	}

	/**
	 * Set the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the entire text.
	 * 
	 * @param startPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text starts.
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return end position (number of characters) of the decision knowledge element
	 *         or the irrelevant text within the entire text.
	 */
	public int getEndPosition() {
		return this.endPosition;
	}

	/**
	 * Set the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the entire text.
	 * 
	 * @param endPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text ends.
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	/**
	 * Gets the length of the part of the text (substring). This is a derived
	 * method.
	 * 
	 * @return end position - start position.
	 */
	public int getLength() {
		return this.endPosition - this.startPosition;
	}

	/**
	 * @return true if the text of the decision knowledge element or irrelevant text
	 *         is plain, e.g., does not contain any code or logger ouput.
	 */
	public boolean isPlainText() {
		return isPlainText;
	}

	/**
	 * Sets whether the text of the decision knowledge element or irrelevant text is
	 * plain, e.g., does not contain any code or logger ouput.
	 * 
	 * @param isPlainText
	 *            true if the text of the decision knowledge element or irrelevant
	 *            text is plain, e.g., does not contain any code or logger ouput.
	 */
	public void setPlainText(boolean isPlainText) {
		this.isPlainText = isPlainText;
	}

	/**
	 * Sets the id of the Jira issue that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the Jira issue.
	 */
	public void setJiraIssueId(long issueId) {
		this.jiraIssueId = issueId;
	}

	/**
	 * @return id of the Jira issue that the decision knowledge element or
	 *         irrelevant text is part of.
	 */
	public long getJiraIssueId() {
		return this.jiraIssueId;
	}

	@Override
	public Issue getJiraIssue() {
		return ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
	}

	/**
	 * @return part of the text.
	 */
	public String getText() {
		return super.getSummary();
	}
}
