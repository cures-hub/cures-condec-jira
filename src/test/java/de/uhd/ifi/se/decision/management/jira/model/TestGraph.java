package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGraph extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private Graph graph;
	private DecisionKnowledgeElement element;

	@Before
	public void setUp() throws CreateException {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("Test"));
		graph = new GraphImpl(element.getProject().getProjectKey(), element.getKey());
	}

	@Test
	public void testProjectKeyConstructor() {
		Graph graphRoot = new GraphImpl(element.getProject().getProjectKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testRootElementLinkDistConstructor() {
		Graph graphRoot = new GraphImpl(element.getProject().getProjectKey(), element.getKey());
		assertNotNull(graphRoot);
	}

	@Test
	public void testRootElementConstructor() {
		Graph graphRoot = new GraphImpl(element);
		assertNotNull(graphRoot);
	}

	@Test
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
		ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");

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
		DecisionKnowledgeProject project = new DecisionKnowledgeProjectImpl("TEST-Set");
		graph.setProject(project);
		assertEquals("TEST-Set", graph.getProject().getProjectKey());
	}

	@Test
	@NonTransactional
	public void testGraphWithSentences() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText("I got an issue in this testclass");
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
