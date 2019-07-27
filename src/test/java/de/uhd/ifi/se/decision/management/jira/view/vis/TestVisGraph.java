package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

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

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}

}
