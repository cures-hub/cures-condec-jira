package de.uhd.ifi.se.decision.management.jira.rationalebacklog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rationale.backlog.KnowledgeCompletion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestKnowledgeCompletion {

	private KnowledgeCompletion knowledgeCompletion;
	private KnowledgeElement parentElement;
	private KnowledgeElement childElement;

	@Before
	public void setUp() {
		knowledgeCompletion = new KnowledgeCompletion();
		parentElement = new KnowledgeElement();
		childElement = new KnowledgeElement();
	}

	@Test
	public void testInitialization() {
		assertEquals(3, knowledgeCompletion.getCompletionCheckMap().size());
	}

	@Test
	public void testIsElementCompleteParentDecision() {
		parentElement.setType(KnowledgeType.DECISION);
		childElement.setType(KnowledgeType.ISSUE);
		assertTrue(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.DECISION);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.ALTERNATIVE);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.PRO);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.CON);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.OTHER);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
	}

	@Test
	public void testIsElementCompleteParentIssue() {
		parentElement.setType(KnowledgeType.ISSUE);
		childElement.setType(KnowledgeType.DECISION);
		assertTrue(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.ALTERNATIVE);
		assertTrue(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.ISSUE);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.PRO);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.CON);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.OTHER);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
	}

	@Test
	public void testIsElementCompleteParentAlternative() {
		parentElement.setType(KnowledgeType.ALTERNATIVE);
		childElement.setType(KnowledgeType.ISSUE);
		assertTrue(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.ALTERNATIVE);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.DECISION);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.PRO);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.CON);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
		childElement.setType(KnowledgeType.OTHER);
		assertFalse(knowledgeCompletion.isElementComplete(parentElement, childElement));
	}

}
