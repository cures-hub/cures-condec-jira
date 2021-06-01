package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for code classes. Determines which table columns are used for
 * object relational mapping to the database.
 * 
 * @issue Is it necessary to store code classes in database?
 * @decision Storing code classes is necessary because then they have a unique
 *           id and can be linked to Jira issues such as work items.
 *
 * @see ChangedFile
 * @see CodeClassPersistenceManager
 */
@Table("CondecCodeClass")
public interface CodeClassInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getFileName();

	void setFileName(String fileName);

	int getLineCount();

	void setLineCount(int lineCount);

	String getProjectKey();

	void setProjectKey(String projectKey);

	/**
	 * Deletes the {@link CodeClassInDatabase} object, i.e., removes it from
	 * database.
	 * 
	 * @param elementToDelete
	 *            {@link CodeClassInDatabase} object.
	 * @return true if deletion was successful, false otherwise.
	 */
	static boolean deleteElement(CodeClassInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
