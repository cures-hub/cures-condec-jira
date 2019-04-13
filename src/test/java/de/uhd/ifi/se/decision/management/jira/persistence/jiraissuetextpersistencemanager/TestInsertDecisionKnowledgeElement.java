package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertDecisionKnowledgeElement extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testElementNullUserNullParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilledParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(null, user, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentNull() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, user, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserNullParentFilled() {
		assertNull(manager.insertDecisionKnowledgeElement(null, null, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNullParentFilled() {
		assertNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilledParentFilled() {
		assertNull(manager.insertDecisionKnowledgeElement(null, user, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledParentFilled() {
		assertNotNull(manager.insertDecisionKnowledgeElement(decisionKnowledgeElement, user, element));
	}

}
