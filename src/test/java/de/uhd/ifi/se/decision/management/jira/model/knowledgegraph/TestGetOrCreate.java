package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetOrCreate extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testProjectKeyValid() {
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		assertEquals(graph, KnowledgeGraph.getInstance("TEST"));
	}

	@Test
	@NonTransactional
	public void testProjectValid() {
		assertNotNull(KnowledgeGraph.getOrCreate(new DecisionKnowledgeProject("TEST")));
	}

	@Test
	@NonTransactional
	public void testProjectKeyNull() {
		assertNull(KnowledgeGraph.getInstance((String) null));
	}

	@Test
	@NonTransactional
	public void testProjectNull() {
		assertNull(KnowledgeGraph.getOrCreate((DecisionKnowledgeProject) null));
	}

}
