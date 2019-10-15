package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueLinks;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeGraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import net.java.ao.test.jdbc.NonTransactional;

public class TestKnowledgeGraph extends TestSetUp {

	private KnowledgeGraph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		graph = new KnowledgeGraphImpl(element.getProject().getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGetNodes() {
		assertEquals(8, graph.vertexSet().size());
	}

	@Test
	@NonTransactional
	public void testGetEdges() {
		assertEquals(JiraIssueLinks.getTestIssueLinks().size(), graph.edgeSet().size());
	}


	@Test
	@NonTransactional
	public void testGraphWithIrrelevantComment() {
		List<PartOfJiraIssueText> comment = TestTextSplitter
				.getSentencesForCommentText("This is a test comment with some irrelevant text.");
		PartOfJiraIssueText sentence = comment.get(0);
		String projectKey = sentence.getProject().getProjectKey();
		KnowledgeGraph graph = new KnowledgeGraphImpl(projectKey);
		assertFalse(graph.containsVertex(sentence));
	}

	@Test
	@NonTransactional
	public void testGraphWithRelevantComment() {
		List<PartOfJiraIssueText> comment = TestTextSplitter
				.getSentencesForCommentText("{alternative} This would be a great solution option! {alternative}");
		PartOfJiraIssueText sentence = comment.get(0);
		String projectKey = sentence.getProject().getProjectKey();
		KnowledgeGraph graph = new KnowledgeGraphImpl(projectKey);
		assertTrue(graph.containsVertex(sentence));
	}
}
