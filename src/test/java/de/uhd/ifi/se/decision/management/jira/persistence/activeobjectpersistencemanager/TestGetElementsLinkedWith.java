package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectPersistenceManagerTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestGetElementsLinkedWith extends ActiveObjectPersistenceManagerTestSetUp {

	private Link link;

	@Before
	public void setUp() {
		initialisation();

		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setId(13);
		element.setType(KnowledgeType.ASSESSMENT);

		DecisionKnowledgeElement linkedDecision = new DecisionKnowledgeElementImpl();
		linkedDecision.setProject("TEST");
		linkedDecision.setId(14);
		linkedDecision.setType(KnowledgeType.DECISION);

		DecisionKnowledgeElement elementWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(element, user);
		DecisionKnowledgeElement linkedDecisionWithDatabaseId = aoStrategy
				.insertDecisionKnowledgeElement(linkedDecision, user);
		link = new LinkImpl(elementWithDatabaseId, linkedDecisionWithDatabaseId);
		aoStrategy.insertLink(link, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullInward() {
		aoStrategy.getElementsLinkedWithInwardLinks(null);
	}

	@Test
	@NonTransactional
	public void testElementNotInTableInward() {
		assertEquals(0, aoStrategy.getElementsLinkedWithInwardLinks(link.getDestinationElement()).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testElementInTableInward() {
		aoStrategy.insertLink(link, user);
		assertEquals(1, aoStrategy.getElementsLinkedWithInwardLinks(link.getSourceElement()).size(), 0.0);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullOutward() {
		aoStrategy.getElementsLinkedWithOutwardLinks(null);
	}

	@Test
	@NonTransactional
	public void testElementNotInTableOutward() {
		assertEquals(0, aoStrategy.getElementsLinkedWithOutwardLinks(link.getSourceElement()).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testElementInTableOutward() {
		aoStrategy.insertLink(link, user);
		assertEquals(1, aoStrategy.getElementsLinkedWithOutwardLinks(link.getDestinationElement()).size(), 0.0);
	}
}
