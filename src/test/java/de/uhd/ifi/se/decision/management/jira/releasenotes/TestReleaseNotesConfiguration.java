package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestReleaseNotesConfiguration {
	private ReleaseNotesConfiguration config;

	@Before
	public void setUp() {
		config = new ReleaseNotesConfiguration();
	}

	@Test
	public void testTitle() {
		String title = "Awesome release";
		config.setTitle(title);
		assertEquals(title, config.getTitle());
	}

	@Test
	public void testStartDate() {
		String startDate = "1970-01-01";
		config.setStartDate(startDate);
		assertEquals(startDate, config.getStartDate());
	}

	@Test
	public void testEndDate() {
		String endDate = "2042-04-23";
		config.setEndDate(endDate);
		assertEquals(endDate, config.getEndDate());
	}

	@Test
	public void testSprintId() {
		String sprintId = "unicorn sprint";
		config.setSprintId(sprintId);
		assertEquals(sprintId, config.getSprintId());
	}

	@Test
	public void testJiraIssueMetricWeights() {
		config.setJiraIssueMetricWeights(JiraIssueMetric.toEnumMap());
		assertEquals(JiraIssueMetric.toEnumMap(), config.getJiraIssueMetricWeights());
	}

	@Test
	public void testJiraIssueTypesForBugFixes() {
		config.setJiraIssueTypesForBugFixes(List.of("Bug"));
		assertEquals("Bug", config.getJiraIssueTypesForBugFixes().get(0));
	}

	@Test
	public void testJiraIssueTypesForNewFeatures() {
		config.setJiraIssueTypesForNewFeatures(List.of("User Story"));
		assertEquals("User Story", config.getJiraIssueTypesForNewFeatures().get(0));
	}

	@Test
	public void testJiraIssueTypesForImprovements() {
		config.setJiraIssueTypesForImprovements(List.of("Work Item"));
		assertEquals("Work Item", config.getJiraIssueTypesForImprovements().get(0));
	}
}