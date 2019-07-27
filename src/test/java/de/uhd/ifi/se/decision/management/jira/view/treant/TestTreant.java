package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUser;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreant extends TestSetUpWithIssues {
	private Chart chart;
	private Node nodeStructure;
	private Treant treant;
	private AbstractPersistenceManager persistenceStrategy;

	@Before
	public void setUp() {
		this.chart = new Chart();
		this.nodeStructure = new Node();
		this.treant = new Treant();
		this.treant.setChart(chart);
		this.treant.setNodeStructure(nodeStructure);
		initialization();
		persistenceStrategy = AbstractPersistenceManager.getDefaultPersistenceStrategy("TEST");
	}

	@Test
	public void testGetChart() {
		assertEquals(this.chart, this.treant.getChart());
	}

	@Test
	public void testGetNodeStructure() {
		assertEquals(this.nodeStructure, this.treant.getNodeStructure());
	}

	@Test
	public void testSetChart() {
		Chart newChart = new Chart();
		this.treant.setChart(newChart);
		assertEquals(newChart, this.treant.getChart());
	}

	@Test
	public void testSetNodeStructure() {
		Node newNode = new Node();
		this.treant.setNodeStructure(newNode);
		assertEquals(newNode, this.treant.getNodeStructure());
	}

	@Test
	@NonTransactional
	public void testConstructor() {
		this.treant = new Treant("TEST", "14", 3);
		assertNotNull(this.treant);
	}

	@Test
	@NonTransactional
	public void testConstructorFiltered() {
		ApplicationUser user = JiraUser.SYS_ADMIN.getApplicationUser();
		this.treant = new Treant("TEST", "TEST-30", 3, "?jql=project=TEST", user);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		// assertEquals("decision", treant.getNodeStructure().getHtmlClass());
		assertEquals("Test", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(0, treant.getNodeStructure().getChildren().size());
	}

	@Test
	@NonTransactional
	public void testConstructorQueryNull() {
		ApplicationUser user = JiraUser.SYS_ADMIN.getApplicationUser();
		this.treant = new Treant("TEST", "TEST-30", 3, "null", user);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		// assertEquals("decision", treant.getNodeStructure().getHtmlClass());
		assertEquals("Test", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(0, treant.getNodeStructure().getChildren().size());
	}

	@Test
	public void testCreateNodeStructureNullNullZeroZero() {
		assertEquals(Node.class, treant.createNodeStructure(null, null, 0, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureEmptyNullZeroZero() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(Node.class, treant.createNodeStructure(element, null, 0, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledNullZeroZero() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertEquals(Node.class, treant.createNodeStructure(element, null, 0, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureNullNullFilledFilled() {
		assertEquals(Node.class, treant.createNodeStructure(null, null, 4, 0).getClass());
	}

	public void testCreateNodeStructureEmptyNullFilledFilled() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(Node.class, treant.createNodeStructure(element, null, 4, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledNullFilledFilled() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		assertEquals(Node.class, treant.createNodeStructure(element, null, 4, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledFilledFilledFilled() {
		DecisionKnowledgeElement element = persistenceStrategy.getDecisionKnowledgeElement(14);
		Link link = new LinkImpl();
		link.setType("support");
		link.setSourceElement(10, "i");
		link.setDestinationElement(14, "i");
		link.setDestinationElement(persistenceStrategy.getDecisionKnowledgeElement(14));
		link.setSourceElement(persistenceStrategy.getDecisionKnowledgeElement(10));
		link.setId(23);
		assertEquals(Node.class, treant.createNodeStructure(element, link, 4, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureWithSentenceInIssue() {
		List<PartOfJiraIssueText> sentences = TestTextSplitter.getSentencesForCommentText("This is a testsentence");
		sentences.get(0).setRelevant(true);
		DecisionKnowledgeElement element = persistenceStrategy
				.getDecisionKnowledgeElement(sentences.get(0).getJiraIssueId());
		Node nodeStructure = treant.createNodeStructure(element, null, 4, 0);
		assertEquals(Node.class, nodeStructure.getClass());
		assertTrue(nodeStructure.getChildren().size() == 0);
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
