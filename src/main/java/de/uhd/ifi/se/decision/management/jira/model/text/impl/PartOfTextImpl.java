package de.uhd.ifi.se.decision.management.jira.model.text.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;

/**
 * Model class for textual parts (substrings) of natural language text. These
 * parts can either be relevant decision knowledge elements or irrelevant text.
 */
public class PartOfTextImpl extends DecisionKnowledgeElementImpl implements PartOfText {

	private int startPosition;
	private int endPosition;
	private boolean isRelevant;
	private boolean isValidated;
	private boolean isPlainText;
	private long jiraIssueId;

	@Override
	public boolean isRelevant() {
		return this.isRelevant;
	}

	@Override
	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	@Override
	public boolean isValidated() {
		return this.isValidated;
	}

	@Override
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}

	@Override
	public boolean isTagged() {
		return this.getType() != KnowledgeType.OTHER;
	}

	@Override
	public int getStartPosition() {
		return this.startPosition;
	}

	@Override
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	@Override
	public int getEndPosition() {
		return this.endPosition;
	}

	@Override
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	@Override
	public int getLength() {
		return this.endPosition - this.startPosition;
	}

	@Override
	public boolean isPlainText() {
		return isPlainText;
	}

	@Override
	public void setPlainText(boolean isPlainText) {
		this.isPlainText = isPlainText;
	}

	@Override
	public void setJiraIssueId(long issueId) {
		this.jiraIssueId = issueId;
	}

	@Override
	public long getJiraIssueId() {
		return this.jiraIssueId;
	}

	@Override
	public Issue getJiraIssue() {
		return ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
	}

	@Override
	public String getText() {
		return super.getSummary();
	}
}
