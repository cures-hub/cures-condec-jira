package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory.BUG_FIXES;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestReleaseNotesCategory {

	@Test
	public void testToString() {
		assertEquals("bug_fixes", BUG_FIXES.toString());
	}

	@Test
	public void testToReadable() {
		assertEquals("Bug Fixes", ReleaseNotesCategory.getTargetGroupReadable(BUG_FIXES));
	}

	@Test
	public void testToList() {
		assertEquals(3, ReleaseNotesCategory.toList().size());
	}

	@Test
	public void testToOriginalList() {
		assertEquals(3, ReleaseNotesCategory.toOriginalList().size());
	}

	@Test
	public void testToBooleanMap() {
		assertEquals(3, ReleaseNotesCategory.toBooleanMap().size());
		assertEquals(false, ReleaseNotesCategory.toBooleanMap().get(BUG_FIXES));
	}
}