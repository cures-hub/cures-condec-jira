package de.uhd.ifi.se.decision.management.jira.view.vis;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;

import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.test.jdbc.DatabaseUpdater;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestVis.AoSentenceTestDatabaseUpdater.class)
public class TestVis extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private Vis vis;
	private HashSet<VisNode> nodes;
	private HashSet<VisEdge> edges;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		nodes = new HashSet<>();
		edges = new HashSet<>();
		vis = new Vis();
		vis.setEdges(edges);
		vis.setNodes(nodes);


		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("Test"));
	}
	@Test
	public void testGetNodes(){assertEquals(this.vis.getNodes(),this.nodes);}

	@Test
	public void testGetEdges(){assertEquals(this.vis.getEdges(),this.edges);}

	@Test
	public void testSetNodes() {
		HashSet<VisNode> newNodes = new HashSet<>();
		this.vis.setNodes(newNodes);
		assertEquals(this.vis.getNodes(),newNodes);
	}

	@Test
	public void testSetEdges() {
		HashSet<VisEdge> newEdges = new HashSet<>();
		this.vis.setEdges(newEdges);
		assertEquals(this.vis.getEdges(),newEdges);
	}

	@Test
	public void testWithoutFiltering() {
		Vis newVis = new Vis(element.getProject().getProjectKey(),element.getKey());
		assertNotNull(newVis);
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
