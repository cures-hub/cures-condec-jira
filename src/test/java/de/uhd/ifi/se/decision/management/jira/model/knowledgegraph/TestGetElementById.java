package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementById extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = KnowledgeGraph.getInstance("TEST");
	}

	@Test
	@NonTransactional
	public void testElementById() {
		KnowledgeElement elementNotInDatabase = new KnowledgeElement();
		elementNotInDatabase.setProject("TEST");
		graph.addVertexNotBeingInDatabase(elementNotInDatabase);
		KnowledgeElement elementInGraph = graph.getElementById(-2);
		assertEquals(elementNotInDatabase, elementInGraph);
	}

	@Test
	@NonTransactional
	public void testElementByKeyNotExisting() {
		KnowledgeElement elementInGraph = graph.getElementById(-42);
		assertNull(elementInGraph);
	}
}
