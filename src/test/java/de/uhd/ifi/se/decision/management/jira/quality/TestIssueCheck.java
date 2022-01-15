package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIssueCheck extends TestSetUp {

	private KnowledgeElement issue;
	private IssueCheck issueCheck;

	@Before
	public void setUp() {
		init();
		issue = KnowledgeElements.getSolvedDecisionProblem();
		issueCheck = new IssueCheck(issue);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		assertNotNull(issue.getLink(decision));
		issue.setStatus(KnowledgeStatus.RESOLVED);
		assertTrue(issueCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testIsLinkedToAlternative() {
		KnowledgeElement alternative = KnowledgeElements.getAlternative();
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());
		assertEquals(3, alternative.getId());
		assertNotNull(issue.getLink(alternative));
		issue.setStatus(KnowledgeStatus.RESOLVED);
		assertTrue(issueCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToDecision() {
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		KnowledgeElement decision = KnowledgeElements.getDecision();
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());

		Link linkToDecision = issue.getLink(decision);
		assertNotNull(linkToDecision);

		linkToDecision = KnowledgeElements.getUnsolvedDecisionProblem().getLink(decision);
		assertNull(linkToDecision);

		KnowledgeGraph graph = KnowledgeGraph.getInstance(issue.getProject());
		assertFalse(graph.containsEdge(linkToDecision));
		issueCheck = new IssueCheck(KnowledgeElements.getUnsolvedDecisionProblem());
		assertFalse(issueCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToAlternative() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setProject(new DecisionKnowledgeProject("TEST"));
		knowledgeElement.setType(KnowledgeType.ISSUE);
		issueCheck = new IssueCheck(knowledgeElement);
		assertFalse(issueCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testIsCompleteAccordingToSettings() {
		// set criteria "issue has to be linked to alternative" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		issue.setStatus(KnowledgeStatus.RESOLVED);
		assertTrue(issueCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaWithAlternative() {
		// set criteria "issue has to be linked to alternative" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		issue.setStatus(KnowledgeStatus.RESOLVED);
		assertTrue(issueCheck.getQualityCheckResult(definitionOfDone).stream()
				.noneMatch(checkResult -> checkResult.isCriterionViolated()));
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaWithoutAlternative() {
		// set criteria "issue has to be linked to alternative" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		issue.setStatus(KnowledgeStatus.RESOLVED);
		assertTrue(issueCheck.getQualityCheckResult(definitionOfDone).stream()
				.noneMatch(checkResult -> checkResult.isCriterionViolated()));
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteriaWithoutAlternativeLinked() {
		// set criteria "issue has to be linked to alternative" in definition of done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setIssueLinkedToAlternative(true);
		issue.setStatus(KnowledgeStatus.RESOLVED);
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setProject(new DecisionKnowledgeProject("TEST"));
		knowledgeElement.setType(KnowledgeType.ISSUE);
		issueCheck = new IssueCheck(knowledgeElement);
		assertFalse(issueCheck.getQualityCheckResult(definitionOfDone).isEmpty());
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", new DefinitionOfDone());
	}
}
