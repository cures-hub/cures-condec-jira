package de.uhd.ifi.se.decision.documentation.jira.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.model.JiraProject;

/**
 * @description Test class for configuration settings
 *
 */
public class TestConfig {

	private JiraProject config;
	private String projectKey;
	private String projectName;
	private boolean isActivated;
	private boolean isIssueStrategy;

	@Before
	public void setUp() {
		this.projectKey = "TestKey";
		this.projectName = "TestName";
		this.isActivated = true;
		this.isIssueStrategy = false;
		this.config = new JiraProject(projectKey, projectName, isActivated, isIssueStrategy);
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.config.getProjectKey());
	}

	@Test
	public void testGetProjectName() {
		assertEquals(this.projectName, this.config.getProjectName());
	}

	@Test
	public void testIsActivated() {
		assertEquals(this.isActivated, this.config.isActivated());
	}

	@Test
	public void testIsIssueStrategy() {
		assertEquals(this.isIssueStrategy, this.config.isIssueStrategy());
	}

	@Test
	public void testSetProjectKey() {
		this.config.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.config.getProjectKey());
	}

	@Test
	public void testSetProjectName() {
		this.config.setProjectName(this.projectName + "New");
		assertEquals(this.projectName + "New", this.config.getProjectName());
	}

	@Test
	public void testSetActivated() {
		this.config.setActivated(this.isActivated);
		assertEquals(this.isActivated, this.config.isActivated());
	}

	@Test
	public void testSetIssueStrategy() {
		this.config.setIssueStrategy(this.isIssueStrategy);
		assertEquals(this.isIssueStrategy, this.config.isIssueStrategy());
	}
}