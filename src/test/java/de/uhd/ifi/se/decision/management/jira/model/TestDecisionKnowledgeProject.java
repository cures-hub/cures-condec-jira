package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

/**
 * Test class for a JIRA project with the configuration settings used in this
 * plug-in
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestDecisionKnowledgeProject extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private DecisionKnowledgeProject project;
	private String projectKey;
	private String projectName;
	private boolean isActivated;
	private boolean isIssueStrategy;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		initialization();
		this.projectKey = "TestKey";
		this.projectName = "TestName";
		this.isActivated = true;
		this.isIssueStrategy = true;
		this.project = new DecisionKnowledgeProjectImpl(projectKey, projectName);
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
	public void testSetIsKnowledgeExtractedFromIssues() {
		project.setKnowledgeExtractedFromIssues(true);
		assertTrue(project.isKnowledgeExtractedFromIssues());
	}

	@Test
	public void testSetWebhookDataNullNull() {
		project.setWebhookData(null, null);
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataNullFilled() {
		project.setWebhookData(null, "TEST-Sec");
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataFilledNull() {
		project.setWebhookData("TEST", null);
		assertTrue(true);
	}

	@Test
	public void testSetWebhookDataFilledFilled() {
		project.setWebhookData("TEST", "TEST-Sec");
		assertTrue(true);
	}

	@Test
	public void testGetWebhookUrl() {
		assertEquals("http://true", project.getWebhookUrl());
	}

	@Test
	public void testGetWebhookSecret() {
		assertEquals("true", project.getWebhookSecret());
	}
}