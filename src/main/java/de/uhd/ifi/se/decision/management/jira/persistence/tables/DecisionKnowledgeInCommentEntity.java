package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("CondecInComment")
public interface DecisionKnowledgeInCommentEntity extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	long getCommentId();

	void setCommentId(long id);

	int getEndSubstringCount();

	void setEndSubstringCount(int count);

	long getIssueId();

	void setIssueId(long id);

	String getKnowledgeTypeString();

	void setKnowledgeTypeString(String type);

	String getProjectKey();

	void setProjectKey(String projectKey);

	Boolean isRelevant();

	void setRelevant(Boolean isRelevant);

	int getStartSubstringCount();

	void setStartSubstringCount(int count);

	Boolean isTagged();

	void setTagged(Boolean tagged);

	Boolean isTaggedFineGrained();

	void setTaggedFineGrained(Boolean tagged);

	Boolean isTaggedManually();

	void setTaggedManually(Boolean tagged);

	long getUserId();

	void setUserId(long id);

	static boolean deleteElement(DecisionKnowledgeInCommentEntity elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
