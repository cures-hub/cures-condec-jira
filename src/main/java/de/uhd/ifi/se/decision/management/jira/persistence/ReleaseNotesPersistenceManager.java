package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import net.java.ao.Query;

/**
 * Responsible to persist release notes with explicit decision knowledge into
 * database.
 * 
 * @see ReleaseNotesInDatabase
 */
public class ReleaseNotesPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	/**
	 * Delete release notes
	 *
	 * @param id
	 * @param user
	 * @return
	 */
	public static boolean deleteReleaseNotes(long id, ApplicationUser user) {
		if (id <= 0 || user == null) {
			LOGGER.error(
					"Element cannot be deleted since it does not exist (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class,
				Query.select().where("ID = ?", id))) {
			isDeleted = ReleaseNotesInDatabase.deleteReleaseNotes(databaseEntry);
		}
		return isDeleted;
	}

	/**
	 * Get release notes
	 *
	 * @param id
	 * @return
	 */
	public static ReleaseNotes getReleaseNotes(long id) {
		ReleaseNotes releaseNote = null;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class,
				Query.select().where("ID = ?", id))) {
			releaseNote = new ReleaseNotes(databaseEntry);
		}
		return releaseNote;
	}

	/**
	 * Create Release Notes
	 *
	 * @param releaseNote
	 * @param user
	 * @return
	 */
	public static long createReleaseNotes(ReleaseNotes releaseNote, ApplicationUser user) {
		if (releaseNote == null || user == null) {
			return 0;
		}
		ReleaseNotesInDatabase dbEntry = ACTIVE_OBJECTS.create(ReleaseNotesInDatabase.class);
		setParameters(releaseNote, dbEntry, false);
		dbEntry.save();
		return dbEntry.getId();
	}

	/**
	 * Update Release Notes
	 *
	 * @param releaseNotes
	 * @param user
	 * @return
	 */
	public static boolean updateReleaseNotes(ReleaseNotes releaseNotes, ApplicationUser user) {
		if (releaseNotes == null || user == null) {
			return false;
		}
		boolean isUpdated = false;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class,
				Query.select().where("ID = ?", releaseNotes.getId()))) {
			setParameters(releaseNotes, databaseEntry, true);
			databaseEntry.save();
			isUpdated = true;
		}

		return isUpdated;
	}

	public static List<ReleaseNotes> getAllReleaseNotes(String projectKey, String query) {
		List<ReleaseNotes> releaseNotes = new ArrayList<ReleaseNotes>();
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND CONTENT LIKE ?", projectKey, "%" + query + "%"))) {
			releaseNotes.add(new ReleaseNotes(databaseEntry));
		}
		return releaseNotes;
	}

	private static void setParameters(ReleaseNotes releaseNotes, ReleaseNotesInDatabase databaseEntry,
			boolean isUpdated) {
		databaseEntry.setTitle(releaseNotes.getTitle());
		databaseEntry.setContent(releaseNotes.getContent());
		if (!isUpdated) {
			databaseEntry.setStartDate(releaseNotes.getStartDate());
			databaseEntry.setEndDate(releaseNotes.getEndDate());
			databaseEntry.setProjectKey(releaseNotes.getProjectKey());
		}
	}
}
