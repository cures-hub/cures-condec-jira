package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCheckForCompleteness extends TestSetUp {
	private KnowledgeElement issue;
	private KnowledgeElement decision;
	private KnowledgeElement alternative;
	private KnowledgeElement proArgument;
	private KnowledgeElement codeFile;

	@Before
	public void setUp() {
		init();
		CodeFiles.addCodeFilesToKnowledgeGraph();
		issue = KnowledgeElements.getSolvedDecisionProblem();
		issue.setStatus(KnowledgeStatus.RESOLVED);
		decision = KnowledgeElements.getDecision();
		alternative = KnowledgeElements.getAlternative();
		proArgument = KnowledgeElements.getProArgument();
		codeFile = KnowledgeElements.getCodeFile();
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertTrue(CompletenessHandler.checkForCompleteness(issue));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertTrue(CompletenessHandler.checkForCompleteness(decision));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertTrue(CompletenessHandler.checkForCompleteness(alternative));
	}

	@Test
	@NonTransactional
	public void testCompleteArgument() {
		assertTrue(CompletenessHandler.checkForCompleteness(proArgument));
	}

	@Test
	@NonTransactional
	public void testCompleteCodeFile() {
		assertTrue(CompletenessHandler.checkForCompleteness(codeFile));
	}

}
