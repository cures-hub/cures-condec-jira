package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionCheck extends TestSetUp {
	private KnowledgeElement decision;
	private ApplicationUser user;
	private DecisionCheck decisionCheck;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		decision = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("High_Level", "UI"), decision);
		decisionCheck = new DecisionCheck(decision);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(KnowledgeType.DECISION, decision.getType());
		assertEquals(4, decision.getId());
		KnowledgeElement issue = KnowledgeElements.getSolvedDecisionProblem();
		assertEquals(KnowledgeType.ISSUE, issue.getType());
		assertEquals(2, issue.getId());
		assertNotNull(decision.getLink(issue));
		assertTrue(decisionCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToIssue() {
		KnowledgeElement unlinkedDecision = new KnowledgeElement();
		unlinkedDecision.setType(KnowledgeType.DECISION);
		unlinkedDecision.setProject("TEST");
		unlinkedDecision.setId(4242);
		unlinkedDecision.setStatus(KnowledgeStatus.CHALLENGED);
		decisionCheck = new DecisionCheck(unlinkedDecision);
		assertFalse(decisionCheck.isDefinitionOfDoneFulfilled());
		assertTrue(decisionCheck.getQualityCheckResult(new DefinitionOfDone()).stream()
				.anyMatch(checkResult -> checkResult.isCriterionViolated()));
	}

	@Test
	@NonTransactional
	public void testGetFailedCriteria() {
		// set criteria "decision has to be linked to pro argument" in definition of
		// done
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setDecisionLinkedToPro(true);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(decisionCheck.getQualityCheckResult(definitionOfDone).stream()
				.anyMatch(checkResult -> checkResult.isCriterionViolated()));

		KnowledgeElement pro = JiraIssues.addElementToDataBase(123, KnowledgeType.PRO);
		KnowledgePersistenceManager.getInstance("TEST").insertLink(decision, pro, user);

		assertTrue(decisionCheck.getQualityCheckResult(definitionOfDone).stream()
				.noneMatch(checkResult -> checkResult.isCriterionViolated()));

		// restore default
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", new DefinitionOfDone());
	}

	@Test
	@NonTransactional
	public void testIsNotLinkedToPro() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setDecisionLinkedToPro(true);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(decisionCheck.isDefinitionOfDoneFulfilled());

		// restore default
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", new DefinitionOfDone());
	}

	@Test
	@NonTransactional
	public void testIsLinkedToPro() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setDecisionLinkedToPro(true);

		KnowledgeElement pro = JiraIssues.addElementToDataBase(123, KnowledgeType.PRO);
		KnowledgePersistenceManager.getInstance("TEST").insertLink(decision, pro, user);

		assertTrue(decisionCheck.isDefinitionOfDoneFulfilled());
	}

	@Test
	@NonTransactional
	public void testHasOnlyDecisionLevelSet() {
		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("High_Level"), decision);
		assertFalse(decisionCheck.isDefinitionOfDoneFulfilled());
	}

	@After
	public void tearDown() {
		// restore default
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", new DefinitionOfDone());
	}
}
