package de.uhd.ifi.se.decision.management.jira.model.text.impl;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;

/**
 * Model class for textual parts (substrings) of natural language text. These
 * parts can either be relevant decision knowledge elements or irrelevant text.
 */
public abstract class AbstractPartOfText extends DecisionKnowledgeElementImpl implements PartOfText {

	private int startSubstringCount;
	private int endSubstringCount;
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
	public int getStartSubstringCount() {
		return this.startSubstringCount;
	}

	@Override
	public void setStartSubstringCount(int count) {
		this.startSubstringCount = count;
	}

	@Override
	public int getEndSubstringCount() {
		return this.endSubstringCount;
	}

	@Override
	public void setEndSubstringCount(int count) {
		this.endSubstringCount = count;
	}

	@Override
	public int getLength() {
		return this.endSubstringCount - this.startSubstringCount;
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
	public String getText() {
		return super.getSummary();
	}
}
