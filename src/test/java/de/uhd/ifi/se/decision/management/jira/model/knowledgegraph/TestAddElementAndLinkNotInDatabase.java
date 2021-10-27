package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAddElementAndLinkNotInDatabase extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = KnowledgeGraph.getInstance("TEST");
	}

	@Test
	@NonTransactional
	public void testAddElementNotInDatabase() {
		KnowledgeElement elementNotInDatabase = new KnowledgeElement();
		elementNotInDatabase.setProject("TEST");
		elementNotInDatabase = graph.addVertexNotBeingInDatabase(elementNotInDatabase);
		assertEquals("TEST:graph:-2", elementNotInDatabase.getKey());
		assertEquals(elementNotInDatabase, graph.addVertexNotBeingInDatabase(elementNotInDatabase));
	}

	@Test
	@NonTransactional
	public void testAddLinkNotInDatabase() {
		Link linkNotInDatabase = new Link(KnowledgeElements.getSolvedDecisionProblem(),
				KnowledgeElements.getDecision());
		linkNotInDatabase = graph.addEdgeNotBeingInDatabase(linkNotInDatabase);
		assertEquals(-2, linkNotInDatabase.getId());
	}
}
