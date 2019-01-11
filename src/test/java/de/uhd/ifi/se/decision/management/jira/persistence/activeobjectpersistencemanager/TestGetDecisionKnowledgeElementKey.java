package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectPersistenceManagerTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGetDecisionKnowledgeElementKey extends ActiveObjectPersistenceManagerTestSetUp {

	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialisation();
		DecisionKnowledgeElement insertElement = new DecisionKnowledgeElementImpl();
		insertElement.setKey("TEST-13");
		insertElement.setProject("TEST");
		insertElement.setType(KnowledgeType.DECISION);
		element = aoStrategy.insertDecisionKnowledgeElement(insertElement, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testKeyNull() {
		aoStrategy.getDecisionKnowledgeElement(null);
	}

	@Test
	@NonTransactional
	public void testKeyEmpty() {
		assertNull(aoStrategy.getDecisionKnowledgeElement(""));
	}

	@Test
	@NonTransactional
	public void testKeyFilledNotInTable() {
		assertNull(aoStrategy.getDecisionKnowledgeElement("TEST-123124"));
	}

	@Test
	@NonTransactional
	public void testKeyFilledInTable() {
		assertNotNull(aoStrategy.getDecisionKnowledgeElement(element.getKey()));
	}
}
