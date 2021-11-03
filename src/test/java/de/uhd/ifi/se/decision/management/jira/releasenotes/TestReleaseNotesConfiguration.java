package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestReleaseNotesConfiguration {
	private ReleaseNotesConfiguration config;
	private String startDate;
	private String endDate;
	private String sprintId;
	private TargetGroup targetGroup;
	private EnumMap<JiraIssueMetric, Double> issueMetric;
	private List<Integer> bugFixMapping;
	private List<Integer> featureMapping;
	private List<Integer> improvementMapping;

	@Before
	public void setUp() {
		config = new ReleaseNotesConfiguration();
		startDate = "2019-01-12";
		endDate = "2020-01-12";
		sprintId = "15";
		targetGroup = TargetGroup.DEVELOPER;
		issueMetric = JiraIssueMetric.toEnumMap();
		bugFixMapping = new ArrayList<Integer>();
		featureMapping = new ArrayList<Integer>();
		improvementMapping = new ArrayList<Integer>();
		bugFixMapping.add(1);
		featureMapping.add(2);
		improvementMapping.add(3);
	}

	@Test
	public void testStartDate() {
		config.setStartDate(startDate);
		assertEquals(startDate, config.getStartDate());
	}

	@Test
	public void testEndDate() {
		config.setEndDate(endDate);
		assertEquals(endDate, config.getEndDate());
	}

	@Test
	public void testSprintId() {
		config.setSprintId(sprintId);
		assertEquals(sprintId, config.getSprintId());

	}

	@Test
	public void testTargetGroup() {
		config.setTargetGroup(targetGroup);
		assertEquals(targetGroup, config.getTargetGroup());
	}

	@Test
	public void testissueMetric() {
		config.setJiraIssueMetricWeights(issueMetric);
		assertEquals(issueMetric, config.getJiraIssueMetricWeights());
	}

	@Test
	public void testBugFixMapping() {
		config.setJiraIssueTypesForBugFixes(List.of("Bug"));
		assertEquals("Bug", config.getJiraIssueTypesForBugFixes().get(0));
	}

	@Test
	public void testFeatureMapping() {
		config.setJiraIssueTypesForNewFeatures(List.of("User Story"));
		assertEquals("User Story", config.getJiraIssueTypesForNewFeatures().get(0));
	}

	@Test
	public void testJiraMapping() {
		config.setJiraIssueTypesForImprovements(List.of("Work Item"));
		assertEquals("Work Item", config.getJiraIssueTypesForImprovements().get(0));
	}

}