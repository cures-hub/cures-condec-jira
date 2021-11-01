package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertReleaseNotes extends TestSetUp {

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
		long newId = ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		ReleaseNotes newReleaseNote = ReleaseNotesPersistenceManager.getReleaseNotesById(newId);
		assertEquals("release notes text", newReleaseNote.getContent());
	}

	@Test
	@NonTransactional
	public void testReleaseNotesNullUserValid() {
		long id = ReleaseNotesPersistenceManager.insertReleaseNotes(null, JiraUsers.SYS_ADMIN.getApplicationUser());
		assertEquals(-1, id);
	}

	@Test
	@NonTransactional
	public void testReleaseNotesInvalidUserValid() {
		releaseNotes.setProjectKey(null);
		long id = ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertEquals(-1, id);
	}

	@Test
	@NonTransactional
	public void testReleaseNotesValidUserNull() {
		long id = ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes, null);
		assertEquals(-1, id);
	}
}