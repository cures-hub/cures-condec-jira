package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetElementBySummary extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
	}

	@Test
	@NonTransactional
	public void testElementByKey() {
		KnowledgeElement elementInGraph = graph.getElementBySummary("WI: Implement feature");
		assertNotNull(elementInGraph);
	}

	@Test
	@NonTransactional
	public void testElementByKeyNotExisting() {
		KnowledgeElement elementInGraph = graph.getElementBySummary("Unknown summary");
		assertNull(elementInGraph);
	}
}
