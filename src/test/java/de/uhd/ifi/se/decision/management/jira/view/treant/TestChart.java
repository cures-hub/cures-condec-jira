package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestChart {

	private Chart chart;

	@Before
	public void setUp() {
		chart = new Chart();
	}

	@Test
	public void testExist() {
		assertNotNull(chart);
	}

	@Test
	public void testGetContainer() {
		assertEquals("#treant-container", chart.getContainer());
	}

	@Test
	public void testGetConnectors() {
		Map<String, String> connectors = new ConcurrentHashMap<String, String>();
		connectors.put("type", "straight");
		assertEquals(connectors, chart.getConnectors());
	}

	@Test
	public void testGetRootOrientation() {
		assertEquals("NORTH", chart.getRootOrientation());
	}

	@Test
	public void testGetLevelSeparation() {
		assertEquals(30, chart.getLevelSeparation(), 0.0);
	}

	@Test
	public void testGetSiblingSeparation() {
		assertEquals(30, chart.getSiblingSeparation(), 0.0);
	}

	@Test
	public void testGetSubTreeSeparation() {
		assertEquals(30, chart.getSubTreeSeparation(), 0.0);
	}

	@Test
	public void testGetNode() {
		assertEquals(ImmutableMap.of("collapsable", true), chart.getNode());
	}

	@Test
	public void testSecondConstructor() {
		Chart newChart = new Chart("treant-container-two");
		assertEquals(newChart.getContainer(), "#treant-container-two");
	}
}