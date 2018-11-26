package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGetDecisionKnowledgeElements extends ActiveObjectStrategyTestSetUp {

	@Before
	public void setUp() {
		initialisation();
		DecisionKnowledgeElement insertElement = new DecisionKnowledgeElementImpl();
		insertElement.setKey("TEST-13");
		insertElement.setProject("TEST");
		insertElement.setType(KnowledgeType.DECISION);

		aoStrategy.insertDecisionKnowledgeElement(insertElement, user);

		insertElement.setKey("TEST-14");
		aoStrategy.insertDecisionKnowledgeElement(insertElement, user);
	}

	@Test
	@NonTransactional
	public void testFunction() {
		assertNotNull(aoStrategy.getDecisionKnowledgeElements());
	}
}
