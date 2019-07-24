package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElement extends ActiveObjectPersistenceManagerTestSetUp {

	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialisation();
		element = new DecisionKnowledgeElementImpl();
		element.setId(13);
		element.setKey("TEST-13");
		element.setType(KnowledgeType.SOLUTION);
		element.setProject("TEST");
		element.setDescription("Old");
		aoStrategy.insertDecisionKnowledgeElement(element, user);
		element.setDescription("New");
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullUserNull() {
		aoStrategy.updateDecisionKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullUserFilled() {
		aoStrategy.updateDecisionKnowledgeElement(null, user);
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNull() {
		assertTrue(aoStrategy.updateDecisionKnowledgeElement(element, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledNotInTable() {
		DecisionKnowledgeElement notInTableElement = new DecisionKnowledgeElementImpl();
		notInTableElement.setProject("TESTNOT");
		notInTableElement.setType(KnowledgeType.SOLUTION);
		notInTableElement.setKey("TESTNOT-12");
		notInTableElement.setId(12);
		assertFalse(aoStrategy.updateDecisionKnowledgeElement(notInTableElement, user));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledInTable() {
		assertTrue(aoStrategy.updateDecisionKnowledgeElement(element, user));
	}
}
