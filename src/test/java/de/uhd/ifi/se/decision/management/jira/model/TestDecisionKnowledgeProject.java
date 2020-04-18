package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettings;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;

/**
 * Test class for a JIRA project with the configuration settings used in this
 * plug-in
 */
public class TestDecisionKnowledgeProject extends TestSetUp {
	private DecisionKnowledgeProject project;
	private String projectKey;
	private String projectName;
	private boolean isActivated;
	private boolean isIssueStrategy;

	@Before
	public void setUp() {
		init();
		this.projectKey = "TestKey";
		this.projectName = "TestName";
		this.isActivated = true;
		this.isIssueStrategy = true;
		this.project = new DecisionKnowledgeProject(projectKey, projectName);
	}

	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.project.getProjectKey());
	}

	@Test
	public void testGetProjectName() {
		assertEquals(this.projectName, this.project.getProjectName());
	}

	@Test
	public void testIsActivated() {
		assertEquals(this.isActivated, this.project.isActivated());
	}

	@Test
	public void testIsIssueStrategy() {
		assertEquals(this.isIssueStrategy, this.project.isIssueStrategy());
	}

	@Test
	public void testSetProjectKey() {
		this.project.setProjectKey(this.projectKey + "New");
		assertEquals(this.projectKey + "New", this.project.getProjectKey());
	}

	@Test
	public void testSetProjectName() {
		this.project.setProjectName(this.projectName + "New");
		assertEquals(this.projectName + "New", this.project.getProjectName());
	}

	@Test
	public void testSetActivated() {
		this.project.setActivated(this.isActivated);
		assertEquals(this.isActivated, this.project.isActivated());
	}

	@Test
	public void testSetIssueStrategy() {
		this.project.setIssueStrategy(this.isIssueStrategy);
		assertEquals(this.isIssueStrategy, this.project.isIssueStrategy());
	}

	@Test
	public void testGetKnowledgeTypes() {
		assertEquals(18, project.getKnowledgeTypes().size(), 0.0);
	}

	@Test
	public void testSetIsKnowledgeExtractedFromGit() {
		project.setKnowledgeExtractedFromGit(true);
		assertTrue(project.isKnowledgeExtractedFromGit());
	}

	@Test
	public void testSetWebhookDataNullNull() {
		project.setWebhookData(null, null);
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataNullFilled() {
		project.setWebhookData(null, "myhoneybee");
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataFilledNull() {
		project.setWebhookData("http://true", null);
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataFilledFilled() {
		project.setWebhookData("http://true", "myhoneybee");
		assertTrue(true);
	}

	@Test
	public void testGetWebhookUrl() {
		assertEquals("http://true", project.getWebhookUrl());
	}

	@Test
	public void testGetWebhookSecret() {
		project.setWebhookData("http://true", "myhoneybee");
		assertEquals("myhoneybee", project.getWebhookSecret());
	}

	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
}