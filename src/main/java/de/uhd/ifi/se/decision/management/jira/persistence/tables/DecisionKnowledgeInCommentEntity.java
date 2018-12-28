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

	String getType();

	void setType(String type);

	String getProjectKey();

	void setProjectKey(String projectKey);

	boolean isRelevant();

	void setRelevant(boolean isRelevant);

	int getStartSubstringCount();

	void setStartSubstringCount(int count);

	boolean isTagged();

	void setTagged(boolean tagged);

	boolean isTaggedFineGrained();

	void setTaggedFineGrained(boolean tagged);

	boolean isTaggedManually();

	void setTaggedManually(boolean tagged);

	static boolean deleteElement(DecisionKnowledgeInCommentEntity elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
