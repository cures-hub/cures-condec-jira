package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

/**
 * Interface for release notes. Determines which table columns are used for
 * object relational mapping of release notes to the database.
 *
 * @see ReleaseNotes
 */
@Table("CondecReleaseNotes")
public interface ReleaseNotesInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getProjectKey();

	void setProjectKey(String projectKey);

	String getTitle();

	void setTitle(String title);

	String getContent();

	@StringLength(value = StringLength.UNLIMITED)
	void setContent(String content);

	String getStartDate();

	void setStartDate(String startDate);

	String getEndDate();

	void setEndDate(String endDate);

	/**
	 * Deletes the {@link ReleaseNotesInDatabase} object, i.e., removes it from
	 * database.
	 * 
	 * @param releaseNotesToDelete
	 *            {@link ReleaseNotesInDatabase} object.
	 * @return true if deletion was successful, false otherwise.
	 */
	static boolean deleteReleaseNotes(ReleaseNotesInDatabase releaseNotesToDelete) {
		try {
			releaseNotesToDelete.getEntityManager().delete(releaseNotesToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
