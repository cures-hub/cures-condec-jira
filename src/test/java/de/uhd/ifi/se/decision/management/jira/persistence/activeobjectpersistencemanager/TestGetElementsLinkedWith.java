package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementsLinkedWith extends ActiveObjectPersistenceManagerTestSetUp {

	private static Link link;

	@BeforeClass
	public static void setUpBeforeClass() {
		initialisation();
	}	

	@Before
	public void setUp() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setId(13);
		element.setType(KnowledgeType.ASSESSMENT);
		element.setDocumentationLocation(DocumentationLocation.ACTIVEOBJECT);

		DecisionKnowledgeElement linkedDecision = new DecisionKnowledgeElementImpl();
		linkedDecision.setProject("TEST");
		linkedDecision.setId(14);
		linkedDecision.setType(KnowledgeType.DECISION);
		linkedDecision.setDocumentationLocation(DocumentationLocation.ACTIVEOBJECT);

		DecisionKnowledgeElement elementWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(element, user);
		DecisionKnowledgeElement linkedDecisionWithDatabaseId = aoStrategy
				.insertDecisionKnowledgeElement(linkedDecision, user);
		link = new LinkImpl(elementWithDatabaseId, linkedDecisionWithDatabaseId);
		AbstractPersistenceManager.insertLink(link, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullInward() {
		aoStrategy.getElementsLinkedWithInwardLinks(null);
	}

	@Test
	@NonTransactional
	public void testElementNotInTableInward() {
		assertEquals(1, aoStrategy.getElementsLinkedWithInwardLinks(link.getDestinationElement()).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testElementInTableInward() {
		AbstractPersistenceManager.insertLink(link, user);
		assertEquals(0, aoStrategy.getElementsLinkedWithInwardLinks(link.getSourceElement()).size(), 0.0);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testElementNullOutward() {
		aoStrategy.getElementsLinkedWithOutwardLinks(null);
	}

	@Test
	@NonTransactional
	public void testElementNotInTableOutward() {
		assertEquals(1, aoStrategy.getElementsLinkedWithOutwardLinks(link.getSourceElement()).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testElementInTableOutward() {
		AbstractPersistenceManager.insertLink(link, user);
		assertEquals(0, aoStrategy.getElementsLinkedWithOutwardLinks(link.getDestinationElement()).size(), 0.0);
	}
}
