package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDefinitionOfDoneChecker extends TestSetUp {

	private FilterSettings filterSettings;
	private KnowledgeElement knowledgeElement;
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;
	private KnowledgeElement codeFile;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
		CodeFiles.addCodeFilesToKnowledgeGraph();
		issue = KnowledgeElements.getSolvedDecisionProblem();
		issue.setStatus(KnowledgeStatus.RESOLVED);
		decision = KnowledgeElements.getDecision();
		alternative = KnowledgeElements.getAlternative();
		proArgument = KnowledgeElements.getProArgument();
		codeFile = CodeFiles.getSmallCodeFileDone();
	}

	@Test
	@NonTransactional
	public void testDefinitionOfDoneCheck() {
		List<QualityCriterionCheckResult> problems = DefinitionOfDoneChecker.getQualityProblems(knowledgeElement, filterSettings);
		assertEquals(QualityCriterionType.DECISION_COVERAGE, problems.get(0).getType());
		assertEquals(QualityCriterionType.INCOMPLETE_KNOWLEDGE_LINKED, problems.get(1).getType());
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertFalse(DefinitionOfDoneChecker.checkDefinitionOfDone(issue, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertFalse(DefinitionOfDoneChecker.checkDefinitionOfDone(decision, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertFalse(DefinitionOfDoneChecker.checkDefinitionOfDone(alternative, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCompleteArgument() {
		assertFalse(DefinitionOfDoneChecker.checkDefinitionOfDone(proArgument, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCompleteCodeFile() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(codeFile, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCoverageHandlerDoesNotHaveMinimumCoverageTrue() {
		assertTrue(DefinitionOfDoneChecker.doesNotHaveMinimumCoverage(KnowledgeElements.getTestKnowledgeElement(),
				KnowledgeType.DECISION, filterSettings));
	}

	@Test
	@NonTransactional
	public void testCoverageHandlerDoesNotHaveMinimumCoverageFalse() {
		assertFalse(DefinitionOfDoneChecker.doesNotHaveMinimumCoverage(KnowledgeElements.getTestKnowledgeElement(),
				KnowledgeType.OTHER, filterSettings));
	}

	@Test
	@NonTransactional
	public void testIsCompleteElementRecommendation() {
		ElementRecommendation recommendation = new ElementRecommendation();
		assertTrue(DefinitionOfDoneChecker.isComplete(recommendation));
	}
}
