package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElement extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
	}

	@Test
	@NonTransactional
	public void testElementByKey() {
		KnowledgeElement elementNotInDatabase = new KnowledgeElement();
		elementNotInDatabase.setProject("TEST");
		graph.addVertexNotBeeingInDatabase(elementNotInDatabase);
		KnowledgeElement elementInGraph = graph.getElement("TEST:graph:-2");
		assertEquals(elementNotInDatabase, elementInGraph);
		KnowledgeGraph.instances.clear();
	}

	@Test
	@NonTransactional
	public void testElementByKeyNotExisting() {
		KnowledgeElement elementInGraph = graph.getElement("Unknown key");
		assertNull(elementInGraph);
	}
}
