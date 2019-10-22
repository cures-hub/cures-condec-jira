package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.activeobjectpersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDeleteLink extends ActiveObjectPersistenceManagerTestSetUp {

	private static DecisionKnowledgeElement linkedDecisision;

	@BeforeClass
	public static void setUpBeforeAll() {
		initialisation();
	}
	
	@Before
	public void setUp() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);

		linkedDecisision = new DecisionKnowledgeElementImpl();
		linkedDecisision.setProject("TEST");
		linkedDecisision.setType(KnowledgeType.DECISION);

		DecisionKnowledgeElement elementWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(element, user);
		DecisionKnowledgeElement linkedDecisionWithDatabaseId = aoStrategy
				.insertDecisionKnowledgeElement(linkedDecisision, user);
		Link link = new LinkImpl(linkedDecisionWithDatabaseId, elementWithDatabaseId);
		KnowledgePersistenceManager.insertLink(link, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testLinkNullUserNull() {
		KnowledgePersistenceManager.deleteLink(null, null);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testLinkNullUserFilled() {
		KnowledgePersistenceManager.deleteLink(null, user);
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserNull() {
		List<Link> links = aoStrategy.getLinks(linkedDecisision);
		boolean isDeleted = KnowledgePersistenceManager.deleteLink(links.get(0), user);
		assertTrue(isDeleted);
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserFilledLinkNotInTable() {
		Link emptyLink = new LinkImpl(linkedDecisision, linkedDecisision);
		assertFalse(KnowledgePersistenceManager.deleteLink(emptyLink, user));
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserFilledLinkInTable() {
		List<Link> links = aoStrategy.getLinks(linkedDecisision);
		Link link = links.get(0);
		long linkId = GenericLinkManager.isLinkAlreadyInDatabase(link);
		assertTrue(linkId > 0);
		boolean isDeleted = KnowledgePersistenceManager.deleteLink(link, user);
		assertTrue(isDeleted);
	}
}
