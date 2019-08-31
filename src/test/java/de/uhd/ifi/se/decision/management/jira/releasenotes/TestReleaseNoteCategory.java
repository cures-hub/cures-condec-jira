package de.uhd.ifi.se.decision.management.jira.releasenotes;

import org.junit.Test;

import static de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory.BUG_FIXES;
import static org.junit.Assert.*;

public class TestReleaseNoteCategory {

	@Test
	public void testToString() {
		assertEquals("bug_fixes", BUG_FIXES.toString());
	}

	@Test
	public void testToReadable() {
		assertEquals("Bug Fixes", ReleaseNoteCategory.getTargetGroupReadable(BUG_FIXES));
	}

	@Test
	public void testToList() {
		assertEquals(3, ReleaseNoteCategory.toList().size(), 0.0);
	}

	@Test
	public void testToBooleanMap() {
		assertEquals(3, ReleaseNoteCategory.toBooleanMap().size(), 0.0);
		assertEquals(false, ReleaseNoteCategory.toBooleanMap().get(BUG_FIXES));
	}
}