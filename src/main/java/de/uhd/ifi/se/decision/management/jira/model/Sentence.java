package de.uhd.ifi.se.decision.management.jira.model;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;

/**
 * Interface for textual parts of JIRA issue comments. These parts can either be
 * relevant decision knowledge elements or irrelevant text.
 */
@JsonDeserialize(as = SentenceImpl.class)
public interface Sentence extends DecisionKnowledgeElement {

	/**
	 * Set the id of the JIRA issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id of the JIRA issue comment.
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
	 * Get the JIRA issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @see MutableComment
	 * @return JIRA issue comment.
	 */
	MutableComment getComment();

	void setStartSubstringCount(int count);

	int getStartSubstringCount();
	
	void setEndSubstringCount(int count);

	int getEndSubstringCount();

	boolean isRelevant();

	void setRelevant(boolean isRelevant);

	void setRelevant(double prediction);

	boolean isValidated();

	void setValidated(boolean isValidated);

	boolean isTaggedFineGrained();

	int getLength();

	void setType(double[] prediction);

	void setIssueId(long issueid);

	long getIssueId();

	boolean isPlainText();

	void setPlainText(boolean isPlainText);

	String getBody();

	void setBody(String body);
}
