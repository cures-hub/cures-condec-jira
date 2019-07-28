package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGraph extends TestSetUp {

	private Graph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 4));
		graph = new GraphImpl(element.getProject().getProjectKey(), element.getKey());
	}

	@Test
	@NonTransactional
	public void testProjectKeyConstructor() {
		Graph graph = new GraphImpl(element.getProject().getProjectKey());
		assertNotNull(graph);
	}

	@Test
	@NonTransactional
	public void testRootElementLinkDistConstructor() {
		Graph graphRoot = new GraphImpl(element.getProject().getProjectKey(), element.getKey());
		assertNotNull(graphRoot);
	}

	@Test
	@NonTransactional
	public void testRootElementConstructor() {
		Graph graphRoot = new GraphImpl(element);
		assertNotNull(graphRoot);
	}

	@Test
	@NonTransactional
	public void testGetAdjacentElementsNull() {
		assertEquals(0, graph.getAdjacentElements(null).size());
	}

	@Test
	@NonTransactional
	public void testGetAdjacentElementsEmpty() {
		DecisionKnowledgeElement emptyElement = new DecisionKnowledgeElementImpl();
		emptyElement.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertEquals(0, graph.getAdjacentElements(emptyElement).size());
	}

	@Test
	@NonTransactional
	public void testGetAdjacentElementsFilled() {
		assertEquals(1, graph.getAdjacentElements(element).size());
	}

	@Test
	@NonTransactional
	public void testGetAdjacentElementsAndLinksNull() {
		assertEquals(0, graph.getAdjacentElementsAndLinks(null).size());
	}

	@Test
	@NonTransactional
	public void testGetAdjacentElementsAndLinksEmpty() {
		DecisionKnowledgeElement emptyElement = new DecisionKnowledgeElementImpl();
		emptyElement.setDocumentationLocation(DocumentationLocation.JIRAISSUE);
		assertEquals(0, graph.getAdjacentElements(emptyElement).size());
	}

	@Test
	public void testSetRootElement() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(123);
		element.setSummary("Test New Element");
		graph.setRootElement(element);
		assertEquals(element.getSummary(), graph.getRootElement().getSummary());
	}

	@Test
	public void testSetGetProject() {
		DecisionKnowledgeProject project = new DecisionKnowledgeProjectImpl("CONDEC");
		graph.setProject(project);
		assertEquals("CONDEC", graph.getProject().getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGraphWithSentences() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter
				.getSentencesForCommentText("How should we mock a knowledge graph?");
		element = sentences.get(0);
		graph.setRootElement(element);
		assertNotNull(graph.getAdjacentElementsAndLinks(element));
		assertEquals(element.getSummary(), graph.getRootElement().getSummary());
	}

	@Test
	@NonTransactional
	public void testGetAllElements() {
		assertEquals(2, graph.getAllElements().size());
	}
}
