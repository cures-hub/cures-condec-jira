package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions.INCLUDE_BUG_FIXES;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAdditionalConfigurationOptions {

	@Test
	public void testToString() {
		assertEquals("include_bug_fixes", INCLUDE_BUG_FIXES.toString());
	}

	@Test
	public void testToUpperString() {
		assertEquals("INCLUDE_BUG_FIXES", INCLUDE_BUG_FIXES.name());
	}

	@Test
	public void testToList() {
		assertEquals(6, AdditionalConfigurationOptions.toList().size());
	}
}