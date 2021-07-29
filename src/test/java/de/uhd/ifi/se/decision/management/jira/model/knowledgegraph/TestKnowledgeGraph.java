package de.uhd.ifi.se.decision.management.jira.model.knowledgegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueLinks;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;
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
		assertEquals(JiraIssues.getTestJiraIssueCount(), graph.vertexSet().size());
	}

	@Test
	@NonTransactional
	public void testGetEdges() {
		assertEquals(JiraIssueLinks.getTestJiraIssueLinkCount(), graph.edgeSet().size());
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
	public void testContainsUndirectedEdge() {
		Link link = Links.getTestLink();
		assertTrue(graph.containsUndirectedEdge(link));
		assertFalse(graph.containsEdge(link.flip()));
		assertTrue(graph.containsUndirectedEdge(link.flip()));
	}

	@Test
	@NonTransactional
	public void testRemoveEdge() {
		Link link = new Link(2, 4, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		assertTrue(graph.removeEdge(link));
		assertEquals(JiraIssueLinks.getTestJiraIssueLinkCount() - 1, graph.edgeSet().size());
		assertTrue(graph.addEdge(link));
	}

	@Test
	@NonTransactional
	public void testGraphWithIrrelevantComment() {
		PartOfJiraIssueText sentence = JiraIssues.getIrrelevantSentence();
		String projectKey = sentence.getProject().getProjectKey();
		graph = KnowledgeGraph.getInstance(projectKey);
		assertTrue(graph.containsVertex(sentence));
	}

	@Test
	@NonTransactional
	public void testGraphWithRelevantComment() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		String projectKey = sentence.getProject().getProjectKey();
		graph = KnowledgeGraph.getInstance(projectKey);
		assertTrue(graph.containsVertex(sentence));
	}

	@Test
	@NonTransactional
	public void testUpdateNode() {
		KnowledgeElement node = graph.getElementBySummary("WI: Implement feature");
		assertEquals("WI: Implement feature", node.getSummary());
		node.setSummary("Updated");
		assertEquals(5, graph.edgesOf(node).size());

		KnowledgePersistenceManager.getOrCreate("TEST").updateKnowledgeElement(node, null);
		node = graph.getElementBySummary("Updated");
		assertEquals("Updated", node.getSummary());
		assertEquals(5, graph.edgesOf(node).size());
	}
}
