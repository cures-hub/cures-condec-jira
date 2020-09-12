package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jgrapht.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestKnowledgeGraph extends TestSetUp {

	private KnowledgeGraph graph;

	@Before
	public void setUp() {
		init();
		graph = new KnowledgeGraph("TEST");
	}

	@Test
	@NonTransactional
	public void testGetNodes() {
		assertEquals(9, graph.vertexSet().size());
	}

	@Test
	@NonTransactional
	public void testGetEdges() {
		assertEquals(13, graph.edgeSet().size());
	}

	@Test
	@NonTransactional
	public void testContainsEdge() {
		Link link = new Link(2, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setId(2);
		assertTrue(graph.containsEdge(link));
	}

	@Test
	@NonTransactional
	public void testRemoveEdge() {
		Link link = new Link(2, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		assertTrue(graph.removeEdge(link));
		assertEquals(12, graph.edgeSet().size());
		assertTrue(graph.addEdge(link));
	}

	@Test
	@NonTransactional
	public void testGraphWithIrrelevantComment() {
		// TODO Example test for 2 separate graphs
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("This is a test comment with some irrelevant text.");
		PartOfJiraIssueText sentence = comment.get(0);
		assertEquals("This is a test comment with some irrelevant text.", sentence.getSummary());
		String projectKey = sentence.getProject().getProjectKey();
		graph = KnowledgeGraph.getOrCreate(projectKey);
		assertFalse(graph.containsVertex(sentence));
	}

	@Test
	@NonTransactional
	public void testGraphWithRelevantComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		String projectKey = sentence.getProject().getProjectKey();
		graph = KnowledgeGraph.getOrCreate(projectKey);
		assertTrue(graph.containsVertex(sentence));
	}

	@Test
	@NonTransactional
	public void testUpdateNode() {
		KnowledgeElement node = graph.vertexSet().iterator().next();
		assertEquals("WI: Implement feature", node.getSummary());
		node.setSummary("Updated");
		assertEquals(5, graph.edgesOf(node).size());

		KnowledgePersistenceManager.getOrCreate("TEST").updateKnowledgeElement(node, null);
		node = graph.vertexSet().iterator().next();
		assertEquals("Updated", node.getSummary());
		assertEquals(5, graph.edgesOf(node).size());
	}

	@Test
	@NonTransactional
	public void testCopy() {
		Graph<KnowledgeElement, Link> copiedGraph = KnowledgeGraph.copy(graph);
		copiedGraph.addVertex(new KnowledgeElement());
		assertEquals(graph.vertexSet().size() + 1, copiedGraph.vertexSet().size());
	}

	@After
	public void tearDown() {
		KnowledgeGraph.instances.clear();
	}
}
