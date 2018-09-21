package de.uhd.ifi.se.decision.management.jira.extraction.model;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import net.java.ao.schema.Ignore;

public interface Sentence extends DecisionKnowledgeElement{

	boolean isRelevant();

	void setRelevant(boolean isRelevant);
	
	@Ignore
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
	
	@Ignore
	void setKnowledgeType(double[] prediction);
	
	void setArgument(String argument);
	
	String getArgument();
	
	String getProjectKey();
	
	void setProjectKey(String key);
	
	@Ignore
	boolean isPlainText();
	
	@Ignore
	String getBody();
	
	@Ignore 
	void setBody(String body);
	

}
