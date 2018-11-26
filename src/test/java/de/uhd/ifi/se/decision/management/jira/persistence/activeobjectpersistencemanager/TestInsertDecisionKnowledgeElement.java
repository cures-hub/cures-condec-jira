package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertNotNull;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestInsertDecisionKnowledgeElement extends ActiveObjectStrategyTestSetUp {

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
