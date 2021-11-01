package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteReleaseNotes extends TestSetUp {

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
		ReleaseNotesPersistenceManager.deleteReleaseNotes(databaseId, JiraUsers.SYS_ADMIN.getApplicationUser());
		assertNull(ReleaseNotesPersistenceManager.getReleaseNotesById(databaseId));
	}

	@Test
	@NonTransactional
	public void testReleaseNotesNotInDatabaseUserValid() {
		assertFalse(ReleaseNotesPersistenceManager.deleteReleaseNotes(-1, JiraUsers.SYS_ADMIN.getApplicationUser()));
	}

	@Test
	@NonTransactional
	public void testReleaseNotesValidUserNull() {
		assertFalse(ReleaseNotesPersistenceManager.deleteReleaseNotes(4, null));
	}
}