package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetNeighborOfType extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testHasNeighborOfType() {
		assertTrue(element.hasNeighborOfType(KnowledgeType.ISSUE));
	}

	@Test
	public void testHasNoNeighborOfType() {
		assertFalse(element.hasNeighborOfType(KnowledgeType.PRO));
	}

	@Test
	public void testProjectKeyNull() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(element.hasNeighborOfType(KnowledgeType.ISSUE));
	}
}
