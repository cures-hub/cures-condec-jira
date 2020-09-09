package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestReleaseNotesMapping extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testMapping() {
		ReleaseNotesMapping mapping = new ReleaseNotesMapping("TEST");
		assertEquals(1, mapping.getJiraIssueTypesForNewFeatures().size());
		assertEquals(1, mapping.getJiraIssueTypesForImprovements().size());
		assertEquals(1, mapping.getJiraIssueTypesForBugFixes().size());
	}

}
