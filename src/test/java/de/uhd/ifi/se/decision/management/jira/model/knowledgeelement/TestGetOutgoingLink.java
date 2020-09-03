package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetOutgoingLink extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testSameElement() {
		assertNull(element.getOutgoingLink(element));
	}

	@Test
	public void testElementLinked() {
		KnowledgeElement otherElement = KnowledgeElements.getTestKnowledgeElements().get(1);
		assertNotNull(element.getOutgoingLink(otherElement));
	}

	@Test
	public void testElementNotLinked() {
		KnowledgeElement otherElement = KnowledgeElements.getTestKnowledgeElements().get(2);
		assertNull(element.getOutgoingLink(otherElement));
	}

}
