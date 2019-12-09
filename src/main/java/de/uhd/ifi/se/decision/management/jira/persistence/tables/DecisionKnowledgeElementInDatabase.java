package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for decision knowledge elements used in the active object
 * persistence strategy. Determines which table columns are used for object
 * relational mapping of decision knowledge elements to the database.
 * 
 * @see DecisionKnowledgeElement
 */
@Table("CondecElement")
public interface DecisionKnowledgeElementInDatabase extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getSummary();

	void setSummary(String summary);

	String getDescription();

	void setDescription(String description);

	String getType();

	void setType(String type);

	String getProjectKey();

	void setProjectKey(String projectKey);

	String getKey();

	void setKey(String key);

	long getCreated();

	void setCreated(long created);

	long getClosed();

	void setClosed(long closed);

	String getStatus();

	void setStatus(String status);

	static boolean deleteElement(DecisionKnowledgeElementInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}