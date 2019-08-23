package de.uhd.ifi.se.decision.management.jira.releasenotes;

import com.atlassian.jira.issue.issuetype.IssueType;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteConfigurationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestReleaseNoteConfiguration {
	private ReleaseNoteConfiguration config;
	private String startDate;
	private String endDate;
	private String sprintId;
	private TargetGroup targetGroup;
	private EnumMap<TaskCriteriaPrioritisation, Double> taskCriteriaPrioritisation;
	private List<Integer> bugFixMapping;
	private List<Integer> featureMapping;
	private List<Integer> improvementMapping;
	private IssueType issueType;

	@Before
	public void setUp() {
		config = new ReleaseNoteConfigurationImpl();
		startDate = "2019-01-12";
		endDate = "2020-01-12";
		sprintId = "15";
		targetGroup = TargetGroup.DEVELOPER;
		taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toDoubleEnumMap();
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
	public void testTaskCriteriaPrioritisation() {
		config.setTaskCriteriaPrioritisation(taskCriteriaPrioritisation);
		assertEquals(taskCriteriaPrioritisation, config.getTaskCriteriaPrioritisation());
	}

	@Test
	public void testBugFixMapping() {
		config.setBugFixMapping(bugFixMapping);
		assertEquals(bugFixMapping, config.getBugFixMapping());

	}


	@Test
	public void testFeatureMapping() {
		config.setFeatureMapping(featureMapping);
		assertEquals(featureMapping, config.getFeatureMapping());

	}

	@Test
	public void testImprovementMapping() {
		config.setImprovementMapping(improvementMapping);
		assertEquals(improvementMapping, config.getImprovementMapping());
	}

}