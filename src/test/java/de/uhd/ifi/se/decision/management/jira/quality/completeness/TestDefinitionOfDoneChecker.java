package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.Before;
import org.junit.Test;
import net.java.ao.test.jdbc.NonTransactional;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

import java.util.ArrayList;
import java.util.List;

public class TestDefinitionOfDoneChecker extends TestSetUp {

	private FilterSettings filterSettings;
	private String projectKey;
	private KnowledgeElement knowledgeElement;
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;
	private KnowledgeElement codeFile;

	@Before
	public void setUp() {
		init();
		projectKey = "TEST";
		filterSettings = new FilterSettings(projectKey, "");
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
		List<String> list = new ArrayList<>();
		list.add("hasIncompleteKnowledgeLinked");
		list.add("doesNotHaveMinimumCoverage");
		assertEquals(DefinitionOfDoneChecker.getFailedDefinitionOfDoneCheckCriteria(knowledgeElement, filterSettings), list);
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(issue, projectKey));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(decision, projectKey));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(alternative, projectKey));
	}

	@Test
	@NonTransactional
	public void testCompleteArgument() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(proArgument, projectKey));
	}

	@Test
	@NonTransactional
	public void testCompleteCodeFile() {
		assertTrue(DefinitionOfDoneChecker.checkDefinitionOfDone(codeFile, projectKey));
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
}
