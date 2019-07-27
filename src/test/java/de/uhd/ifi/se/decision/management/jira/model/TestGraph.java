package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGraph extends TestSetUp {

	private Graph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
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
		JiraIssuePersistenceManager issueStrategy = new JiraIssuePersistenceManager("TEST");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		Project project = JiraProjects.getTestProject();

		long i = 2;
		DecisionKnowledgeElementImpl decision = new DecisionKnowledgeElementImpl(5000, "TESTSummary", "TestDescription",
				KnowledgeType.DECISION, project.getKey(), "TEST-" + 5000, DocumentationLocation.JIRAISSUE);
		decision.setId(5000);

		issueStrategy.insertDecisionKnowledgeElement(decision, user);
		for (KnowledgeType type : KnowledgeType.values()) {
			LinkImpl link = new LinkImpl();
			link.setType("support");
			if (type != KnowledgeType.DECISION) {
				if (type.equals(KnowledgeType.ARGUMENT)) {
					DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
							"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i,
							DocumentationLocation.JIRAISSUE);
					issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
					link.setSourceElement(decision);
					link.setDestinationElement(decisionKnowledgeElement);
					AbstractPersistenceManager.insertLink(link, user);
				} else {
					DecisionKnowledgeElementImpl decisionKnowledgeElement = new DecisionKnowledgeElementImpl(i,
							"TESTSummary", "TestDescription", type, project.getKey(), "TEST-" + i,
							DocumentationLocation.JIRAISSUE);
					issueStrategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
					link.setType("attack");
					link.setDestinationElement(decision);
					link.setSourceElement(decisionKnowledgeElement);
					AbstractPersistenceManager.insertLink(link, user);
				}
			}
			i++;
		}
		System.out.println(graph.getAdjacentElements(decision).size());
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
		List<DecisionKnowledgeElement> allElements = new ArrayList<DecisionKnowledgeElement>();
		allElements.add(element);
		assertEquals(graph.getAllElements(), allElements);
	}
}
