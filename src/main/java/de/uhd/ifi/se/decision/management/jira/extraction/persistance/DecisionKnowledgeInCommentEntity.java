package de.uhd.ifi.se.decision.management.jira.extraction.persistance;

import net.java.ao.OneToMany;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("Comment")
public interface DecisionKnowledgeInCommentEntity extends RawEntity<Integer> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	boolean getIsRelevant();

	void setIsRelevant(boolean isRelevant);

	boolean getIsTagged();

	void setIsTagged(boolean isTagged);

	long getCommentId();

    void setCommentId(long id);

	long getUserId();

	void setUserId(long id);

	int getStartSubstringCount();

	void setStartSubstringCount(int count);

	int getEndSubstringCount();

	void setEndSubstringCount(int count);

}
