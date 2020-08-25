package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetLinks extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testElementUnknownProject() {
		KnowledgeElement element = new KnowledgeElement();
		assertEquals(0, element.getLinks().size());
	}

	@Test
	public void testElementLinked() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertEquals(5, element.getLinks().size());
	}
}
