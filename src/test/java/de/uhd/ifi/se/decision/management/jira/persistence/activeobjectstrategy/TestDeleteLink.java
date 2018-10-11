package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import de.uhd.ifi.se.decision.management.jira.model.*;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(ActiveObjectStrategyTestSetUp.AoSentenceTestDatabaseUpdater.class)
public class TestDeleteLink extends ActiveObjectStrategyTestSetUp {

	private Link link;

	@Before
	public void setUp() {
		initialisation();
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setProject("TEST");
		element.setType(KnowledgeType.SOLUTION);

		DecisionKnowledgeElement linkedDecisision = new DecisionKnowledgeElementImpl();
		linkedDecisision.setProject("TEST");
		linkedDecisision.setType(KnowledgeType.DECISION);

		DecisionKnowledgeElement elementWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(element, user);
		DecisionKnowledgeElement linkedDecisionWithDatabaseId = aoStrategy.insertDecisionKnowledgeElement(linkedDecisision, user);
		link = new LinkImpl(linkedDecisionWithDatabaseId, elementWithDatabaseId);
		aoStrategy.insertLink(link, user);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testLinkNullUserNull() {
		aoStrategy.deleteLink(null, null);
	}

	@Test(expected = NullPointerException.class)
	@NonTransactional
	public void testLinkNullUserFilled() {
		aoStrategy.deleteLink(null, user);
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserNull() {
		assertTrue(aoStrategy.deleteLink(link, null));
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserFilledLinkNotInTable() {
		Link emptyLink = new LinkImpl();
		assertFalse(aoStrategy.deleteLink(emptyLink, user));
	}

	@Test
	@NonTransactional
	public void testLinkFilledUserFilledLinkInTable() {
		assertTrue(aoStrategy.deleteLink(link, user));
	}
}
