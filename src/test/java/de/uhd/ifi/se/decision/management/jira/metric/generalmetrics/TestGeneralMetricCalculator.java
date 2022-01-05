package de.uhd.ifi.se.decision.management.jira.metric.generalmetrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.metric.GeneralMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.quality.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGeneralMetricCalculator extends TestSetUpGit {

	private GeneralMetricCalculator calculator;
	private FilterSettings filterSettings;

	@Override
	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
		filterSettings = new FilterSettings(projectKey, "");
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMaximumLinkDistanceToDecisions(0);
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(1);
		filterSettings.setDefinitionOfDone(definitionOfDone);
		calculator = new GeneralMetricCalculator(filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetNumberOfCommentsPerJiraIssueMap() {
		assertEquals(1, calculator.getNumberOfCommentsMap().size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElements() {
		assertEquals(calculator.getNumberOfDecisionKnowledgeElements().size(), 4);
	}

	@Test
	@NonTransactional
	public void testGetRequirementsAndCodeFiles() {
		assertEquals(calculator.getRequirementsAndCodeFiles().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetElementsFromDifferentOriginsWithCommitElements() {
		JiraIssues.getSentencesForCommentText("Hash: 123 {decision} We will use Nutch! {decision}", "TEST-4");
		assertEquals(1, new GeneralMetricCalculator(filterSettings).getElementsFromDifferentOrigins()
				.get("Commit Message").size());
	}

	@Test
	@NonTransactional
	public void testGetElementsFromDifferentOriginsWithJiraIssueComment() {
		JiraIssues.addComment(JiraIssues.getTestJiraIssues().get(0));
		assertEquals(1, new GeneralMetricCalculator(filterSettings).getElementsFromDifferentOrigins()
				.get("Jira Issue Description or Comment").size());
	}

	@Test
	@NonTransactional
	public void testGetElementsFromDifferentOriginsWithCode() {
		KnowledgeElement decisionInCode = KnowledgeElements.getDecision();
		decisionInCode.setDocumentationLocation(DocumentationLocation.CODE);
		assertEquals(DocumentationLocation.CODE, decisionInCode.getDocumentationLocation());
		KnowledgeGraph.getInstance("TEST").addVertex(decisionInCode);
		assertEquals(1, new GeneralMetricCalculator(filterSettings).getElementsFromDifferentOrigins()
				.get("Code Comment").size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		assertEquals(calculator.getNumberOfRelevantAndIrrelevantComments().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetNumberOfCommits() {
		assertEquals(0, calculator.getNumberOfCommitsMap().size());
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertTrue(calculator.getNumberOfCommitsMap().size() > 0);
	}

	@Test
	@NonTransactional
	public void testGetDefinitionOfDoneCheckResults() {
		assertEquals(calculator.getDefinitionOfDoneCheckResults().size(), 2);
	}

	@Test
	@NonTransactional
	public void testGetDefinitionOfDoneCheckResultsFail() {
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMaximumLinkDistanceToDecisions(6);
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(0);
		filterSettings.setDefinitionOfDone(definitionOfDone);
		assertEquals((new GeneralMetricCalculator(filterSettings)).getDefinitionOfDoneCheckResults().size(), 2);
	}

}
