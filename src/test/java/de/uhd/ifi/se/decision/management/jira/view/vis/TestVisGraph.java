package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

import static org.junit.Assert.*;

public class TestVisGraph extends TestSetUp {
	private VisGraph visGraph;
	private HashSet<VisNode> nodes;
	private HashSet<VisEdge> edges;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		nodes = new HashSet<>();
		edges = new HashSet<>();
		visGraph = new VisGraph();
		visGraph.setEdges(edges);
		visGraph.setNodes(nodes);

		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("Test"));
	}

	@Test
	public void testGetNodes() {
		assertEquals(this.visGraph.getNodes(), this.nodes);
	}

	@Test
	public void testGetEdges() {
		assertEquals(this.visGraph.getEdges(), this.edges);
	}

	@Test
	public void testSetNodes() {
		HashSet<VisNode> newNodes = new HashSet<>();
		this.visGraph.setNodes(newNodes);
		assertEquals(this.visGraph.getNodes(), newNodes);
	}

	@Test
	public void testSetEdges() {
		HashSet<VisEdge> newEdges = new HashSet<>();
		this.visGraph.setEdges(newEdges);
		assertEquals(this.visGraph.getEdges(), newEdges);
	}

	@Test
	public void testWithoutFiltering() {
		VisGraph newVisGraph = new VisGraph(element.getProject().getProjectKey(), element.getKey(), null, false);
		assertNotNull(newVisGraph);
	}

	@Test
	public void testConstWithListNullProjectNull() {
		VisGraph visGraph = new VisGraph((List) null, (String) null);
		assertNull(visGraph.getEdges());
	}

	@Test
	public void testConstWithListEmptyProjectNull() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		VisGraph visGraph = new VisGraph(elements, (String) null);
		assertNull(visGraph.getEdges());
	}

	@Test
	public void testConstWithListFilledProjectNull() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		elements.add(element);
		VisGraph visGraph = new VisGraph(elements, (String) null);
		assertNull(visGraph.getEdges());
	}

	@Test
	public void testConstWithListNullProjectFilled() {
		VisGraph visGraph = new VisGraph((List) null, "TEST");
		assertEquals(0, visGraph.getNodes().size(), 0.0);
	}

	@Test
	public void testConstWithListEmptyProjectFilled() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		VisGraph visGraph = new VisGraph(elements, "TEST");
		assertEquals(0, visGraph.getNodes().size(), 0.0);
	}

	@Test
	public void testConstWithListFilledProjectFilled() {
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		elements.add(element);
		VisGraph visGraph = new VisGraph(elements, "TEST");
		assertEquals(1, visGraph.getNodes().size(),0.0);
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

}
