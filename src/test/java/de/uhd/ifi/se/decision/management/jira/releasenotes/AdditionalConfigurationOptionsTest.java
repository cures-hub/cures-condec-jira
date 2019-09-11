package de.uhd.ifi.se.decision.management.jira.releasenotes;

import org.junit.Test;

import static de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions.INCLUDE_BREAKING_CHANGES;
import static de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions.INCLUDE_BUG_FIXES;
import static org.junit.Assert.assertEquals;

public class AdditionalConfigurationOptionsTest {

	@Test
	public void testToString() {
		assertEquals("include_bug_fixes", INCLUDE_BUG_FIXES.toString());
	}

	@Test
	public void testToUpperString() {
		assertEquals("INCLUDE_BUG_FIXES", INCLUDE_BUG_FIXES.toUpperString());
	}

	@Test
	public void testGetAdditionalConfigurationOptions() {
		assertEquals(INCLUDE_BUG_FIXES, AdditionalConfigurationOptions.getAdditionalConfigurationOptions(INCLUDE_BUG_FIXES.toUpperString()));
	}


	@Test
	public void testGetMarkdownOptionsString() {
		assertEquals("### Breaking Changes\n Add your breaking changes here\n", AdditionalConfigurationOptions.getMarkdownOptionsString(INCLUDE_BREAKING_CHANGES.toUpperString()));
		assertEquals("", AdditionalConfigurationOptions.getMarkdownOptionsString(null));

	}

	@Test
	public void testToBooleanList() {
		assertEquals(6, AdditionalConfigurationOptions.toBooleanList(true).size());
		assertEquals(true, AdditionalConfigurationOptions.toBooleanList(true).get(INCLUDE_BUG_FIXES));

	}

	@Test
	public void testToList() {
		assertEquals(6, AdditionalConfigurationOptions.toList().size());

	}
}