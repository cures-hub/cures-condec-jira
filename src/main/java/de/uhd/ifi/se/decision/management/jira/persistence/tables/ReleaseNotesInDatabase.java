package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

/**
 * Interface for release notes. Determines which table columns are used for
 * object relational mapping of release notes to the database.
 *
 * @see ReleaseNote
 */
@Table("CondecReleaseNotes")
public interface ReleaseNotesInDatabase extends RawEntity<Integer>, ReleaseNote {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	@StringLength(value = StringLength.UNLIMITED)
	void setContent(String content);

	static boolean deleteReleaseNotes(ReleaseNotesInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
