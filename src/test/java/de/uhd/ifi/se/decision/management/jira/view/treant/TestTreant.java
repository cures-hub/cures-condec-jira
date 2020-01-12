package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreant extends TestSetUp {
	private Chart chart;
	private TreantNode nodeStructure;
	private Treant treant;
	private AbstractPersistenceManagerForSingleLocation persistenceManager;

	@Before
	public void setUp() {
		init();
		this.chart = new Chart();
		this.nodeStructure = new TreantNode();
		this.treant = new Treant("TEST", "TEST-30", 0);
		this.treant.setChart(chart);
		this.treant.setNodeStructure(nodeStructure);
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueManager();
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
		TreantNode newNode = new TreantNode();
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
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		this.treant = new Treant("TEST", "TEST-30", 3, "?jql=project=TEST", user);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		// assertEquals("decision", treant.getNodeStructure().getHtmlClass());
		assertEquals("WI: Do an interesting task", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(1, treant.getNodeStructure().getChildren().size());
	}

	@Test
	@NonTransactional
	public void testConstructorQueryNull() {
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		this.treant = new Treant("TEST", "TEST-30", 3, "null", user);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		assertEquals("WI: Do an interesting task", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(1, treant.getNodeStructure().getChildren().size());
	}

	@Test
	public void testCreateNodeStructureNullNullZeroZero() {
		assertEquals(TreantNode.class, treant.createNodeStructure(null, null, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureEmptyNullZeroZero() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(TreantNode.class, treant.createNodeStructure(element, null, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledNullZeroZero() {
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(14);
		assertEquals(TreantNode.class, treant.createNodeStructure(element, null, 0).getClass());
	}

	@Test
	public void testCreateNodeStructureNullNullFilledFilled() {
		assertEquals(TreantNode.class, treant.createNodeStructure(null, null, 0).getClass());
	}

	public void testCreateNodeStructureEmptyNullFilledFilled() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(TreantNode.class, treant.createNodeStructure(element, null, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledNullFilledFilled() {
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(14);
		assertEquals(TreantNode.class, treant.createNodeStructure(element, null, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureFilledFilledFilledFilled() {
		DecisionKnowledgeElement element = persistenceManager.getDecisionKnowledgeElement(14);
		Link link = new LinkImpl(1, 14, DocumentationLocation.JIRAISSUE, DocumentationLocation.JIRAISSUE);
		link.setType("support");
		link.setDestinationElement(persistenceManager.getDecisionKnowledgeElement(14));
		link.setSourceElement(persistenceManager.getDecisionKnowledgeElement(1));
		link.setId(23);
		assertEquals(TreantNode.class, treant.createNodeStructure(element, link, 0).getClass());
	}

	@Test
	@NonTransactional
	public void testCreateNodeStructureWithSentenceInIssue() {
		List<PartOfJiraIssueText> sentences = JiraIssues.getSentencesForCommentText("This is a testsentence");
		sentences.get(0).setRelevant(true);
		DecisionKnowledgeElement element = persistenceManager
				.getDecisionKnowledgeElement(sentences.get(0).getJiraIssueId());
		TreantNode nodeStructure = treant.createNodeStructure(element, null, 0);
		assertEquals(TreantNode.class, nodeStructure.getClass());
		assertEquals(1, nodeStructure.getChildren().size());
	}
}
