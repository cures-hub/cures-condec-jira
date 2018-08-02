package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

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

	boolean getIsIssue();

	void setIsIssue(boolean isIssue);

	boolean getIsDecision();

	void setIsDecision(boolean isDecision);

	boolean getIsAlternative();

	void setIsAlternative(boolean isAlternative);

	boolean getIsPro();

	void setIsPro(boolean isPro);

	boolean getIsCon();

	void setIsCon(boolean isCon);

	boolean getIsTagged();

	void setIsTagged(boolean isTagged);

	boolean getIsTaggedManually();

	void setIsTaggedMannually(boolean isTaggedManually);

	boolean getIsTaggedFineGrained();

	void setIsTaggedFineGrained(boolean isTagged);

	long getCommentId();

    void setCommentId(long id);

	long getUserId();

	void setUserId(long id);

	int getStartSubstringCount();

	void setStartSubstringCount(int count);

	int getEndSubstringCount();

	void setEndSubstringCount(int count);

}
