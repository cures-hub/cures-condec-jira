package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ReleaseNotesInDatabase;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteImpl;
import net.java.ao.Query;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReleaseNotesPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseNotesPersistenceManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	private String projectKey;

	public ReleaseNotesPersistenceManager(String projectKey) {
		this.projectKey = projectKey;
	}

	/**
	 * Delete release notes
	 * @param id
	 * @param user
	 * @return
	 */
	public static boolean deleteReleaseNotes(long id, ApplicationUser user) {
		if (id <= 0 || user == null) {
			LOGGER.error("Element cannot be deleted since it does not exist (id is less than zero) or the user is null.");
			return false;
		}
		boolean isDeleted = false;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class, Query.select().where("ID = ?", id))) {
			isDeleted = ReleaseNotesInDatabase.deleteReleaseNotes(databaseEntry);
		}
		return isDeleted;
	}

	/**
	 * Get release notes
	 * @param id
	 * @return
	 */
	public static ReleaseNote getReleaseNotes(long id) {
		ReleaseNote releaseNote=null;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class, Query.select().where("ID = ?", id))) {
			releaseNote = new ReleaseNoteImpl(databaseEntry);
		}
		return releaseNote;
	}


	/**
	 * Create Release Notes
	 * @param releaseNote
	 * @param user
	 * @return
	 */
	public static long createReleaseNotes(ReleaseNote releaseNote, ApplicationUser user) {
		if(releaseNote==null || user==null){
			return 0;
		}
		ReleaseNotesInDatabase dbEntry = ACTIVE_OBJECTS.create(ReleaseNotesInDatabase.class);
		dbEntry.setTitle(releaseNote.getTitle());
		String escaped=StringEscapeUtils.escapeJavaScript(releaseNote.getContent());
//		String escaped=StringEscapeUtils.escapeJavaScript(releaseNote.getContent());
		dbEntry.setContent(releaseNote.getContent());
		dbEntry.setProjectKey(releaseNote.getProjectKey());
		dbEntry.save();
		return dbEntry.getId();
	}

	/**
	 * Update Release Notes
	 * @param releaseNote
	 * @param user
	 * @return
	 */
	public static boolean updateReleaseNotes(ReleaseNote releaseNote, ApplicationUser user) {
		if (releaseNote == null||user==null) {
			return false;
		}
		boolean isUpdated=false;
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class, Query.select().where("ID = ?", releaseNote.getId()))) {
			databaseEntry.setTitle(releaseNote.getTitle());
			databaseEntry.setContent(releaseNote.getContent());
			databaseEntry.save();
			isUpdated=true;
		}

		return isUpdated;
	}

	public static List<ReleaseNote> getAllReleaseNotes(String projectKey) {
		List<ReleaseNote> result=new ArrayList<ReleaseNote>();
		for (ReleaseNotesInDatabase databaseEntry : ACTIVE_OBJECTS.find(ReleaseNotesInDatabase.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
			result.add(new ReleaseNoteImpl(databaseEntry));
		}
		return result;
	}
}
