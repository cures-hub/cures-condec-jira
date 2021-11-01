package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetReleaseNotesMatchingFilter extends TestSetUp {

	@Before
	public void setUp() {
		init();
		ReleaseNotes releaseNotes = new ReleaseNotes();
		releaseNotes.setContent("release notes text");
		releaseNotes.setProjectKey("TEST");
		ReleaseNotesPersistenceManager.insertReleaseNotes(releaseNotes, JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	@NonTransactional
	public void testOneReleaseNotesInDatabaseSearchTermEmpty() {
		assertNotNull(new ReleaseNotesPersistenceManager());
		assertEquals(1, ReleaseNotesPersistenceManager.getReleaseNotesMatchingFilter("TEST", "").size());
	}

	@Test
	@NonTransactional
	public void testOneReleaseNotesInDatabaseSearchTermDifferent() {
		assertEquals(0, ReleaseNotesPersistenceManager.getReleaseNotesMatchingFilter("TEST", "security").size());
	}
}