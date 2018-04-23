package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @description Test class for configuration settings
 *
 */
public class TestConfig {

	private JiraProject jiraProject;
	private String projectKey;
	private String projectName;
	private boolean isActivated;
	private boolean isIssueStrategy;

	@Before
	public void setUp() {
		this.projectKey = "TestKey";
		this.projectName = "TestName";
		this.isActivated = true;
		this.isIssueStrategy = true;
		this.jiraProject = new JiraProject(projectKey, projectName);
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.jiraProject.getProjectKey());
	}

	@Test
	public void testGetProjectName() {
		assertEquals(this.projectName, this.jiraProject.getProjectName());
	}

	@Test
	public void testIsActivated() {
		assertEquals(this.isActivated, this.jiraProject.isActivated());
	}

	@Test
	public void testIsIssueStrategy() {
		assertEquals(this.isIssueStrategy, this.jiraProject.isIssueStrategy());
	}

	@Test
	public void testSetProjectKey() {
		this.jiraProject.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.jiraProject.getProjectKey());
	}

	@Test
	public void testSetProjectName() {
		this.jiraProject.setProjectName(this.projectName + "New");
		assertEquals(this.projectName + "New", this.jiraProject.getProjectName());
	}

	@Test
	public void testSetActivated() {
		this.jiraProject.setActivated(this.isActivated);
		assertEquals(this.isActivated, this.jiraProject.isActivated());
	}

	@Test
	public void testSetIssueStrategy() {
		this.jiraProject.setIssueStrategy(this.isIssueStrategy);
		assertEquals(this.isIssueStrategy, this.jiraProject.isIssueStrategy());
	}
}