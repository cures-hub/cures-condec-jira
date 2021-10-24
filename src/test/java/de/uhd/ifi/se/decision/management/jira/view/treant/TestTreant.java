package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTreant extends TestSetUp {
	private Chart chart;
	private TreantNode nodeStructure;
	private Treant treant;

	@Before
	public void setUp() {
		init();
		chart = new Chart();
		nodeStructure = new TreantNode();
		FilterSettings filterSettings = new FilterSettings("TEST", null);
		filterSettings.setSelectedElement("TEST-30");
		treant = new Treant(filterSettings, false);
		treant.setChart(chart);
		treant.setNodeStructure(nodeStructure);
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
		filterSettings.setSelectedElement("TEST-14");
		this.treant = new Treant(filterSettings, false);
		assertNotNull(this.treant);
	}

	@Test
	@NonTransactional
	public void testConstructorFilterSettingsNull() {
		treant = new Treant(null);
		assertNull(treant.getNodeStructure());
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
		assertTrue(treant.getNodeStructure().getChildren().size() > 0);
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
		assertTrue(treant.getNodeStructure().getChildren().size() > 0);
	}

	@Test
	@NonTransactional
	public void testIsHyperlinked() {
		assertFalse(treant.isHyperlinked());
	}
}
