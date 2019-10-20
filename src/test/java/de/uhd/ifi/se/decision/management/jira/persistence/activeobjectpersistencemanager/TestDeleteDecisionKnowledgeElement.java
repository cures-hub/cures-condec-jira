package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteDecisionKnowledgeElement extends ActiveObjectPersistenceManagerTestSetUp {

	private static DecisionKnowledgeElement element;
	private static DecisionKnowledgeElement linkedDecisision;

	@BeforeClass
	public static void setUpBeforeClass() {
		initialisation();
	}

	@Before
	public void setUp() {
		element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);

		linkedDecisision = new DecisionKnowledgeElementImpl();
		linkedDecisision.setProject("TEST");
		linkedDecisision.setType(KnowledgeType.DECISION);
	}

	@Test
	@NonTransactional
	public void testElementNullUserNull() {
		assertFalse(aoStrategy.deleteDecisionKnowledgeElement(null, null));
	}

	@Test
	@NonTransactional
	public void testElementNullUserFilled() {
		assertFalse(aoStrategy.deleteDecisionKnowledgeElement(null, user));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserNull() {
		assertFalse(aoStrategy.deleteDecisionKnowledgeElement(element, null));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledNoElementInTable() {
		assertFalse(aoStrategy.deleteDecisionKnowledgeElement(element, user));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledElementInTable() {
		DecisionKnowledgeElement returnDecision = aoStrategy.insertDecisionKnowledgeElement(element, user);
		assertTrue(aoStrategy.deleteDecisionKnowledgeElement(returnDecision, user));
	}

	@Test
	@NonTransactional
	public void testElementFilledUserFilledElementLinked() {
		DecisionKnowledgeElement elementWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(element, user);
		DecisionKnowledgeElement linkedDecisionWithDatabaseId = aoStrategy
				.insertDecisionKnowledgeElement(linkedDecisision, user);
		Link link = new LinkImpl(linkedDecisionWithDatabaseId, elementWithDatabaseId);
		AbstractPersistenceManager.insertLink(link, user);
		assertTrue(aoStrategy.deleteDecisionKnowledgeElement(elementWithDatabaseId, user));
	}
}