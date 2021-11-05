package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestReleaseNotesCreator extends TestSetUp {

	private ReleaseNotesCreator releaseNotesCreator;

	@Before
	public void setUp() {
		init();
		releaseNotesCreator = new ReleaseNotesCreator(JiraIssues.getTestJiraIssues(), new ReleaseNotesConfiguration(),
				JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testEmpty() {
		assertNull(releaseNotesCreator.proposeReleaseNotes());
	}

	@Test
	public void testNonEmptyFeatures() {
		ReleaseNotesConfiguration config = new ReleaseNotesConfiguration();
		config.setJiraIssueTypesForNewFeatures(List.of("Non functional requirement"));
		releaseNotesCreator = new ReleaseNotesCreator(JiraIssues.getTestJiraIssues(), config,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertNotNull(releaseNotesCreator.proposeReleaseNotes());
	}

	@Test
	public void testNonEmptyBugFixes() {
		ReleaseNotesConfiguration config = new ReleaseNotesConfiguration();
		config.setJiraIssueTypesForBugFixes(List.of("Task"));
		releaseNotesCreator = new ReleaseNotesCreator(JiraIssues.getTestJiraIssues(), config,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertNotNull(releaseNotesCreator.proposeReleaseNotes());
	}

	@Test
	public void testNonEmptyImprovements() {
		ReleaseNotesConfiguration config = new ReleaseNotesConfiguration();
		config.setJiraIssueTypesForImprovements(List.of("Task"));
		releaseNotesCreator = new ReleaseNotesCreator(JiraIssues.getTestJiraIssues(), config,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertNotNull(releaseNotesCreator.proposeReleaseNotes());
	}
}
