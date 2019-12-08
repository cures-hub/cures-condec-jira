package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for the status of decision knowledge elements of various
 * documentation locations, e.g. JIRA issue comments and commit messages.
 * Determines which table columns are used for object relational mapping of
 * status objects to the database.
 * 
 * @see Link
 */
@Table("CondecStatus")
public interface KnowledgeStatusInDatabase extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getStatus();

	void setStatus(String status);

	long getElementId();

	void setElementId(long id);

	String getDocumentationLocation();

	void setDocumentationLocation(String documentationLocation);

	/**
	 * Deletes the {@link KnowledgeStatusInDatabase} object, i.e., removes it from
	 * database.
	 * 
	 * @param statusInDatabase
	 *            status for a single {@link DecisionKnowledgeElement}.
	 * @return true if deletion was successful, false otherwise.
	 * @see KnowledgeStatus
	 */
	static boolean deleteStatus(KnowledgeStatusInDatabase statusInDatabase) {
		try {
			statusInDatabase.getEntityManager().delete(statusInDatabase);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
