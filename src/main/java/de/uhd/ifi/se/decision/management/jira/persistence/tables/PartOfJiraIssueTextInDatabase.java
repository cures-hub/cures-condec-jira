package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for decision knowledge elements stored in JIRA issue comments or
 * the description. Determines which table columns are used for object
 * relational mapping to the database.
 * 
 * @see DecisionKnowledgeElement
 */
@Table("CondecInComment")
public interface PartOfJiraIssueTextInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	long getCommentId();

	void setCommentId(long id);

	int getEndPosition();

	void setEndPosition(int endPosition);

	long getJiraIssueId();

	void setJiraIssueId(long id);

	String getType();

	void setType(String type);

	String getProjectKey();

	void setProjectKey(String projectKey);

	boolean isRelevant();

	void setRelevant(boolean isRelevant);

	int getStartPosition();

	void setStartPosition(int startPosition);

	boolean isValidated();

	void setValidated(boolean validated);

	static boolean deleteElement(PartOfJiraIssueTextInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
