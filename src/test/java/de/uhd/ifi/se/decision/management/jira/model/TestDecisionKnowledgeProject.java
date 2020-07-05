package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.project.Project;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for a Jira project with the configuration settings used in this
 * plug-in.
 */
public class TestDecisionKnowledgeProject extends TestSetUp {
	private DecisionKnowledgeProject project;

	@Before
	public void setUp() {
		init();
		this.project = new DecisionKnowledgeProject(JiraProjects.getTestProject());
	}

	@Test
	public void testUnderlyingJiraProjectNull() {
		DecisionKnowledgeProject project = new DecisionKnowledgeProject((Project) null);
		assertEquals("", project.getProjectKey());
		assertEquals("", project.getProjectName());
	}

	@Test
	public void testConstructorWithProjectKey() {
		DecisionKnowledgeProject project = new DecisionKnowledgeProject("TEST");
		assertEquals(JiraProjects.getTestProject(), project.getJiraProject());
	}

	@Test
	public void testGetProjectKey() {
		assertEquals("TEST", project.getProjectKey());
	}

	@Test
	public void testGetProjectName() {
		assertEquals("TEST", project.getProjectName());
	}

	@Test
	public void testIsActivated() {
		assertEquals(true, project.isActivated());
	}

	@Test
	public void testIsJiraIssueStorage() {
		assertEquals(true, project.isIssueStrategy());
	}

	@Test
	public void testGetKnowledgeTypes() {
		assertEquals(18, project.getDecisionKnowledgeTypes().size());
	}

	@Test
	public void testIsKnowledgeExtractedFromGit() {
		assertEquals(true, project.isKnowledgeExtractedFromGit());
	}

	@Test
	public void testIsPostSquashedCommitsActivated() {
		assertEquals(true, project.isPostSquashedCommitsActivated());
	}

	@Test
	public void testIsPostFeatureBranchCommitsActivated() {
		assertEquals(true, project.isPostFeatureBranchCommitsActivated());
	}

	@Test
	public void testIsWebhookEnabled() {
		assertEquals(true, project.isWebhookEnabled());
	}

	@Test
	public void testGetWebhookUrl() {
		assertEquals("http://true", project.getWebhookUrl());
	}

	@Test
	public void testGetWebhookSecret() {
		ConfigPersistenceManager.setWebhookSecret(project.getProjectKey(), "myhoneybee");
		assertEquals("myhoneybee", project.getWebhookSecret());
	}

	@Test
	public void testIsClassifierEnabled() {
		assertEquals(false, project.isClassifierEnabled());
	}

	@Test
	public void testGetJiraIssueTypes() {
		assertEquals(5, project.getJiraIssueTypeNames().size());
	}
/*
	@AfterClass
	public static void tearDown() {
		// reset plugin settings to default settings
		MockPluginSettingsFactory.pluginSettings = new MockPluginSettings();
	}
	 */
}