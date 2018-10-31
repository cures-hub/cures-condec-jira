package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;

@JsonSerialize(as = DecisionKnowledgeElementImpl.class)
public interface Sentence extends DecisionKnowledgeElement {

	boolean isRelevant();

	void setRelevant(boolean isRelevant);

	void setRelevant(Double prediction);

	boolean isTagged();

	void setTagged(boolean isTagged);

	boolean isTaggedManually();

	void setTaggedManually(boolean isTaggedManually);

	boolean isTaggedFineGrained();

	void setTaggedFineGrained(boolean isTaggedFineGrained);

	long getCommentId();

	void setCommentId(long id);

	long getUserId();

	void setUserId(long id);

	int getStartSubstringCount();

	void setStartSubstringCount(int count);

	int getEndSubstringCount();

	void setEndSubstringCount(int count);

	String getKnowledgeTypeString();

	void setKnowledgeTypeString(String type);

	void setKnowledgeType(double[] prediction);

	void setArgument(String argument);

	String getArgument();

	String getProjectKey();

	void setProjectKey(String key);

	void setIssueId(long issueid);

	long getIssueId();

	boolean isPlainText();

	void setPlainText(boolean isPlainText);

	String getBody();

	void setBody(String body);

	Date getCreated();

	void setCreated(Date date);
}
