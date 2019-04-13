package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteDecisionKnowledgeElement extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testLessNull() {
		assertFalse(manager.deleteDecisionKnowledgeElement(-1, null));
	}

	@Test
	@NonTransactional
	public void testZeroNull() {
		assertFalse(manager.deleteDecisionKnowledgeElement(0, null));
	}

	@Test
	@NonTransactional
	public void testMoreNull() {
		assertFalse(manager.deleteDecisionKnowledgeElement(12, null));
	}

	@Test
	@NonTransactional
	public void testLessFilled() {
		assertFalse(manager.deleteDecisionKnowledgeElement(-1, user));
	}

	@Test
	@NonTransactional
	public void testZeroFilled() {
		assertFalse(manager.deleteDecisionKnowledgeElement(0, user));
	}

	@Test
	@NonTransactional
	public void testMoreFilled() {
		assertTrue(manager.deleteDecisionKnowledgeElement(1, user));
	}

}
