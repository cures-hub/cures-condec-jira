package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.activeobjectpersistencemanager;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDecisionKnowledgeElementId extends ActiveObjectPersistenceManagerTestSetUp {

	private static DecisionKnowledgeElement element;

	@BeforeClass
	public static void setUpBeforeAll() {
		initialisation();
	}

	@Before
	public void setUp() {
		DecisionKnowledgeElement insertElement = new DecisionKnowledgeElementImpl();
		insertElement.setId(13);
		insertElement.setProject("TEST");
		insertElement.setType(KnowledgeType.DECISION);
		element = aoStrategy.insertDecisionKnowledgeElement(insertElement, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testIdNull() {
		aoStrategy.getDecisionKnowledgeElement(null);
	}

	@Test
	@NonTransactional
	public void testIdNotInTable() {
		assertNull(aoStrategy.getDecisionKnowledgeElement(123132));
	}

	@Test
	@NonTransactional
	public void testIdInTable() {
		assertNotNull(aoStrategy.getDecisionKnowledgeElement(element.getId()));
	}
}
