package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for code classes. Determines which table columns are used for
 * object relational mapping to the database.
 *
 * @see KnowledgeElement
 */
@Table("CondecCodeClass")
public interface CodeClassInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getFileName();

	void setFileName(String fileName);

	String getProjectKey();

	void setProjectKey(String projectKey);

	static boolean deleteElement(CodeClassInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
