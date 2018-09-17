package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("Sentence")
public interface DecisionKnowledgeInCommentEntity extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	boolean isRelevant();

	void setIsRelevant(boolean isRelevant);

	boolean isTagged();

	void setIsTagged(boolean isTagged);

	boolean isTaggedManually();

	void setIsTaggedManually(boolean isTaggedManually);

	boolean isTaggedFineGrained();

	void setIsTaggedFineGrained(boolean isTaggedFineGrained);

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
	
	void setArgument(String argument);
	
	String getArgument();
	
	String getProjectKey();
	
	void setProjectKey(String key);


}
