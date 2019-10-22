package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.activeobjectpersistencemanager;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElements extends ActiveObjectPersistenceManagerTestSetUp {

	@BeforeClass
	public static void setUpBeforeClass() {
		initialisation();
	}

	@Before
	public void setUp() {
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
