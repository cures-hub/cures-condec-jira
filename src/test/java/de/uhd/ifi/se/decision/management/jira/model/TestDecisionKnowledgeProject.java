package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.recommendation.prompts.FeatureWithPrompt;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

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
	public void testGetBasicConfiguration() {
		assertEquals(true, project.getBasicConfiguration().isActivated());
	}

	@Test
	public void testGetDecisionKnowledgeTypes() {
		assertEquals(4, project.getNamesOfConDecKnowledgeTypes().size());
	}

	@Test
	public void testGetWebhookConfiguration() {
		assertEquals(false, project.getWebhookConfiguration().isActivated());
	}

	@Test
	public void testIsClassifierEnabled() {
		assertEquals(false, project.getTextClassificationConfiguration().isActivated());
	}

	@Test
	public void testGetJiraIssueTypes() {
		assertEquals(6, project.getJiraIssueTypeNames().size());
		assertEquals(0, new DecisionKnowledgeProject("").getJiraIssueTypeNames().size());
	}

	@Test
	public void testGetKnowledgeTypesStorageInJiraIssuesActivated() {
		assertEquals(6, project.getNamesOfKnowledgeTypes().size());
	}

	@Test
	public void testGetLinkTypes() {
		assertEquals(4, DecisionKnowledgeProject.getJiraIssueLinkTypes().size());
		// currently, all Mock issue link types are called "relate"
		assertEquals(3, DecisionKnowledgeProject.getNamesOfLinkTypes().size());
	}

	@Test
	public void testGetChangeImpactAnalysisConfiguration() {
		assertEquals(0.25f, project.getChangeImpactAnalysisConfiguration().getDecayValue(), 0);
	}

	@Test
	public void testGetProjectsWithConDecActivatedAndAccessableForUser() {
		assertEquals(1, DecisionKnowledgeProject
				.getProjectsWithConDecActivatedAndAccessableForUser(JiraUsers.SYS_ADMIN.getApplicationUser()).size());
	}

	@Test
	public void testGetAllNamesOfLinkTypes() {
		assertEquals(9, DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().size());
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("other"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("contains"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("attacks"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("supports"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("comments"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("is contained by"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("is attacked by"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("is supported by"));
		assertTrue(DecisionKnowledgeProject.getInwardAndOutwardNamesOfLinkTypes().contains("is commented by"));
	}

	@Test
	public void testGetDefinitionOfDone() {
		assertFalse(project.getDefinitionOfDone().isIssueIsLinkedToAlternative());
	}

	@Test
	public void testGetDecisionGuidanceConfiguration() {
		assertFalse(project.getDecisionGuidanceConfiguration().isRecommendationAddedToKnowledgeGraph());
	}

	@Test
	public void testGetLinkSuggestionConfiguration() {
		assertTrue(project.getLinkSuggestionConfiguration().getMinProbability() > 0);
	}

	@Test
	public void testGetPromptingEventConfiguration() {
		assertFalse(project.getPromptingEventConfiguration().isPromptEventActivated(FeatureWithPrompt.DOD_CHECKING,
				"finished"));
	}

	@Test
	public void testGetGitConfigurations() {
		assertFalse(project.getGitConfiguration().isPostFeatureBranchCommitsActivated());
	}

}