package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreant extends TestSetUp {
	private Chart chart;
	private TreantNode nodeStructure;
	private Treant treant;
	private KnowledgeElement classElement;

	@Before
	public void setUp() {
		init();
		this.chart = new Chart();
		this.nodeStructure = new TreantNode();
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement("TEST-30");
		this.treant = new Treant(filterSettings, false);
		this.treant.setChart(chart);
		this.treant.setNodeStructure(nodeStructure);
		CodeClassPersistenceManager ccManager = new CodeClassPersistenceManager("Test");
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = ccManager.insertKnowledgeElement(classElement, user);
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
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement("14");
		this.treant = new Treant(filterSettings, false);
		assertNotNull(this.treant);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testSecondConstructorCheckboxFalse() {
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement(classElement);
		this.treant = new Treant("treantid", false, filterSettings);
		assertNotNull(this.treant);
	}

	@Test
	@NonTransactional
	public void testSecondConstructorWithIssueViewCheckboxFalse() {
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement(classElement);
		this.treant = new Treant("treantid", true, filterSettings);
		assertNotNull(this.treant);
	}

	@Test
	@NonTransactional
	public void testConstructorFiltered() {
		FilterSettings filterSettings = new FilterSettings("TEST", "?jql=project=TEST");
		filterSettings.setLinkDistance(3);
		filterSettings.setSelectedElement("TEST-30");
		this.treant = new Treant(filterSettings);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		// assertEquals("decision", treant.getNodeStructure().getHtmlClass());
		assertEquals("WI: Do an interesting task", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(1, treant.getNodeStructure().getChildren().size());
	}

	@Test
	@NonTransactional
	public void testConstructorQueryNull() {
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement("TEST-30");
		this.treant = new Treant(filterSettings);
		assertNotNull(this.treant);
		assertNotNull(treant.getNodeStructure());
		assertEquals("WI: Do an interesting task", treant.getNodeStructure().getNodeContent().get("title"));
		assertEquals(1, treant.getNodeStructure().getChildren().size());
	}
}
