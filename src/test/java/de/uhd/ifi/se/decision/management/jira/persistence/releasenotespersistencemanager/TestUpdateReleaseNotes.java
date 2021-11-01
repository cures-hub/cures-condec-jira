package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateReleaseNotes extends TestSetUp {

	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		init();
		releaseNotes = new ReleaseNotes();
		releaseNotes.setContent("release notes text");
		releaseNotes.setProjectKey("TEST");
	}

	@Test
	@NonTransactional
	public void testReleaseNotesValidUserValid() {
		long databaseId = ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		releaseNotes.setId(databaseId);
		releaseNotes.setContent("new release notes text");
		ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNotes, JiraUsers.SYS_ADMIN.getApplicationUser());
		ReleaseNotes newReleaseNote = ReleaseNotesPersistenceManager.getReleaseNotesById(databaseId);
		assertEquals("new release notes text", newReleaseNote.getContent());
	}

	@Test
	@NonTransactional
	public void testReleaseNotesNotInDatabaseUserValid() {
		assertFalse(ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNotes,
				JiraUsers.SYS_ADMIN.getApplicationUser()));
	}

	@Test
	@NonTransactional
	public void testReleaseNotesNullUserValid() {
		assertFalse(ReleaseNotesPersistenceManager.updateReleaseNotes(null, JiraUsers.SYS_ADMIN.getApplicationUser()));
	}

	@Test
	@NonTransactional
	public void testReleaseNotesValidUserNull() {
		assertFalse(ReleaseNotesPersistenceManager.updateReleaseNotes(releaseNotes, null));
	}
}