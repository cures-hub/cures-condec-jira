package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertDecisionKnowledgeElement extends ActiveObjectPersistenceManagerTestSetUp {

	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialisation();
		element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullUserNull() {
		aoStrategy.insertDecisionKnowledgeElement(null, null);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementEmptyUserNull() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		aoStrategy.insertDecisionKnowledgeElement(element, null);
	}

	@Test
	@NonTransactional
	public void testRepresFilledUserNoFails() {
		assertNotNull(aoStrategy.insertDecisionKnowledgeElement(element, user));
	}
}
