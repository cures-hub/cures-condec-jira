package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.atlassian.jira.issue.comments.MutableComment;

public interface Sentence extends DecisionKnowledgeElement {

	@JsonIgnore
	boolean isRelevant();

	@JsonIgnore
	void setRelevant(boolean isRelevant);

	@JsonIgnore
	void setRelevant(double prediction);

	@JsonIgnore
	boolean isValidated();

	@JsonIgnore
	void setValidated(boolean isValidated);

	@JsonIgnore
	boolean isTaggedFineGrained();

	@JsonIgnore
	long getCommentId();

	@JsonIgnore
	MutableComment getComment();

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

	int getLength();

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
