package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestIsIncomplete extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testIsIncompleteTrue() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(element.isIncomplete());
	}

	@Test
	public void testIsIncompleteFalse() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElements().get(3);

		assertEquals(KnowledgeType.ISSUE, element.getType());
		assertFalse(element.isIncomplete());
	}
}
