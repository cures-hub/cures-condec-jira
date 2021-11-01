package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;

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
	 * @param releaseNotes
	 *            {@link ReleaseNotes} to be created.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted release notes, -1 if insertion
	 *         failed.
	 */
	public static long insertReleaseNotes(ReleaseNotes releaseNotes, ApplicationUser user) {
		if (releaseNotes == null || releaseNotes.getProjectKey() == null || user == null) {
			return -1;
		}
		ReleaseNotesInDatabase databaseEntry = ACTIVE_OBJECTS.create(ReleaseNotesInDatabase.class);
		setParameters(releaseNotes, databaseEntry, false);
		databaseEntry.save();
		return databaseEntry.getId();
	}

	/**
	 * @param releaseNotes
	 *            {@link ReleaseNotes} to be updated by title and/or content.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if the release notes were successfully updated. Only the title
	 *         and/or textual content of the {@link ReleaseNotes} can be updated.
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

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @param searchTerm
	 *            for substring filtering.
	 * @return all release notes with explicit decision knowledge for the Jira
	 *         project.
	 */
	public static List<ReleaseNotes> getReleaseNotesMatchingFilter(String projectKey, String searchTerm) {
		List<ReleaseNotes> releaseNotes = new ArrayList<>();
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND CONTENT LIKE ?", projectKey, "%" + searchTerm + "%"))) {
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
