package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestHasType extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		// Jira issue with task Jira issue type
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testNotMatching() {
		assertFalse(element.hasKnowledgeType(KnowledgeType.ALTERNATIVE, KnowledgeType.DECISION));
	}

	@Test
	public void testMatching() {
		assertTrue(element.hasKnowledgeType(KnowledgeType.OTHER, KnowledgeType.DECISION));
	}
}