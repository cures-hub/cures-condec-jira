package de.uhd.ifi.se.decision.management.jira.persistence.releasenotesreleasemanager;

import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNote;

import static org.junit.Assert.assertEquals;

public class TestReleaseNotesPersistenceManager extends TestReleaseNotesPersistenceManagerSetUp {

	//@todo fix this test
	public void testCreateReleaseNote() {
		long newId = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		ReleaseNote newReleaseNote = ReleaseNotesPersistenceManager.getReleaseNotes(newId);
		assertEquals(aVeryLongContent, newReleaseNote.getContent());
	}

}
