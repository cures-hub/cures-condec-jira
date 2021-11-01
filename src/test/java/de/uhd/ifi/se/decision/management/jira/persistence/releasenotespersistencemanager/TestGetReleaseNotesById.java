package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetReleaseNotesById extends TestSetUp {

	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		init();
		assertNotNull(new ReleaseNotesPersistenceManager());
		releaseNotes = new ReleaseNotes();
		releaseNotes.setContent("release notes text");
		releaseNotes.setProjectKey("TEST");
		ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes, JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testReleaseNotesExisting() {
		assertNotNull(ReleaseNotesPersistenceManager.getReleaseNotesById(1));
	}

	@Test
	@NonTransactional
	public void testReleaseNotesNotExisting() {
		assertNull(ReleaseNotesPersistenceManager.getReleaseNotesById(-1));
	}
}