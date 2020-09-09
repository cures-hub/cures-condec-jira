package de.uhd.ifi.se.decision.management.jira.persistence.releasenotespersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.ReleaseNotesPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotes;
import net.java.ao.test.jdbc.NonTransactional;

public class TestReleaseNotesPersistenceManager extends TestReleaseNotesPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testCreateReleaseNote() {
		long newId = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		ReleaseNotes newReleaseNote = ReleaseNotesPersistenceManager.getReleaseNotes(newId);
		assertEquals(aVeryLongContent, newReleaseNote.getContent());
	}

	@Test
	@NonTransactional
	public void testDeleteReleaseNote() {
		long newId = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		ReleaseNotesPersistenceManager.deleteReleaseNotes(newId,user);
		assertEquals(null, ReleaseNotesPersistenceManager.getReleaseNotes(newId));
	}

	@Test
	@NonTransactional
	public void testUpdateReleaseNote() {
		long newId = ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		String someChangedContent="some other content";
		ReleaseNotes updateReleaseNote=ReleaseNotesPersistenceManager.getReleaseNotes(newId);
		updateReleaseNote.setContent(someChangedContent);
		assertEquals(true,ReleaseNotesPersistenceManager.updateReleaseNotes(updateReleaseNote,user));
		assertEquals(someChangedContent, ReleaseNotesPersistenceManager.getReleaseNotes(newId).getContent());
	}

	@Test
	@NonTransactional
	public void testGetAllReleaseNote() {
		ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		String someChangedContent="some other content";
		releaseNote.setContent(someChangedContent);
		ReleaseNotesPersistenceManager.createReleaseNotes(releaseNote, user);
		assertEquals(2, ReleaseNotesPersistenceManager.getAllReleaseNotes(projectKey,"").size());
	}



}
