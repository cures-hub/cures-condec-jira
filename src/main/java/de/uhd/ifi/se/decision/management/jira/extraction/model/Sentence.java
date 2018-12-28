package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

public interface Sentence extends DecisionKnowledgeElement {

	@JsonIgnore
	boolean isRelevant();

	@JsonIgnore
	void setRelevant(boolean isRelevant);

	@JsonIgnore
	void setRelevant(double prediction);

	@JsonIgnore
	boolean isTagged();

	@JsonIgnore
	void setTagged(boolean isTagged);

	@JsonIgnore
	boolean isTaggedManually();

	@JsonIgnore
	void setTaggedManually(boolean isTaggedManually);

	@JsonIgnore
	boolean isTaggedFineGrained();

	@JsonIgnore
	long getCommentId();

	@JsonIgnore
	void setCommentId(long id);

	@JsonIgnore
	long getAuthorId();

	@JsonIgnore
	int getStartSubstringCount();

	@JsonIgnore
	void setStartSubstringCount(int count);

	@JsonIgnore
	int getEndSubstringCount();

	@JsonIgnore
	void setEndSubstringCount(int count);

	@JsonIgnore
	void setType(double[] prediction);

	@JsonIgnore
	void setIssueId(long issueid);

	@JsonIgnore
	long getIssueId();

	@JsonIgnore
	boolean isPlainText();

	@JsonIgnore
	void setPlainText(boolean isPlainText);

	@JsonIgnore
	String getBody();

	@JsonIgnore
	void setBody(String body);

	@JsonIgnore
	Date getCreated();

	@JsonIgnore
	void setCreated(Date date);
}
