package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

import java.util.Set;

public class TestGetFailedCompletenessCriteria extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testGetFailedCompletenessCriteriaTrue() {
		KnowledgeElement decision = KnowledgeElements.getTestKnowledgeElements().get(6);
		Set<Link> links = decision.getLinks();
		for (Link link : links) {
			if (link.getOppositeElement(decision).getType() == KnowledgeType.ISSUE) {
				KnowledgeGraph.getInstance("TEST").removeEdge(link);
			}
		}
		assertFalse(decision.getFailedCompletenessCriteria().isEmpty());
	}

	@Test
	public void testIsIncompleteFalse() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElements().get(4);

		assertEquals(KnowledgeType.ISSUE, element.getType());
		element.setStatus(KnowledgeStatus.RESOLVED);
		assertEquals(KnowledgeStatus.RESOLVED, element.getStatus());
		assertTrue(element.getFailedCompletenessCriteria().isEmpty());
	}
}
