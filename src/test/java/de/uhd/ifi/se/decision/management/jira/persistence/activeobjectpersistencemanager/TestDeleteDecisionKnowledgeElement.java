package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectPersistenceManagerTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestDeleteDecisionKnowledgeElement extends ActiveObjectPersistenceManagerTestSetUp {

	private DecisionKnowledgeElement element;
	private DecisionKnowledgeElement linkedDecisision;

	@Before
	public void setUp() {
		initialisation();
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