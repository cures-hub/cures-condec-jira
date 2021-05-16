package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetLinkDistance extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testSameElement() {
		assertEquals(0, element.getLinkDistance(element, 1));
	}

	@Test
	public void testMaxDistanceTooSmallToFindPath() {
		assertEquals(-1, element.getLinkDistance(KnowledgeElements.getAlternative(), 0));
	}

	@Test
	public void testLinkedWithinMaxDistance() {
		assertEquals(1, element.getLinkDistance(KnowledgeElements.getAlternative(), 3));
	}
}
