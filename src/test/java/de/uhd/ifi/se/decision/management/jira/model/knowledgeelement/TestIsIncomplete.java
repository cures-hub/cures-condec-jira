package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsIncomplete extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testIsIncompleteTrue() {
		KnowledgeElement decision = KnowledgeElements.getTestKnowledgeElements().get(6);
		Set<Link> links = decision.getLinks();
		for (Link link : links) {
			if (link.getOppositeElement(decision).getType() == KnowledgeType.ISSUE) {
				KnowledgeGraph.getOrCreate("TEST").removeEdge(link);
			}
		}
		assertTrue(decision.isIncomplete());
	}

	@Test
	public void testIsIncompleteFalse() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElements().get(3);

		assertEquals(KnowledgeType.ISSUE, element.getType());
		element.setStatus(KnowledgeStatus.RESOLVED);
		assertEquals(KnowledgeStatus.RESOLVED, element.getStatus());
		assertFalse(element.isIncomplete());
	}
}
