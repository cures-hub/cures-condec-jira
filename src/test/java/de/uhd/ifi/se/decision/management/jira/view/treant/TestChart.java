package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestChart {

	private Chart chart;

	@Before
	public void setUp(){
		chart = new Chart();
	}

	@Test
	public void testExist() {
		assertNotNull(chart);
	}

	@Test
	public void testGetContainer(){
		assertEquals("#treant-container", chart.getContainer());
	}

	@Test
	public void testGetConnectors(){
		Map connectors = new ConcurrentHashMap();
		connectors.put("type", "straight");
		Map style = new ConcurrentHashMap();
		style.put("arrow-end", "classic-wide-long");
		style.put("stroke-width", 2);
		connectors.put("style", style);
		assertEquals(connectors, chart.getConnectors());
	}

	@Test
	public void testGetRootOrientation(){
		assertEquals("NORTH", chart.getRootOrientation());
	}

	@Test
	public void testGetLevelSeparation(){
		assertEquals(30, chart.getLevelSeparation(), 0.0);
	}

	@Test
	public void testGetSiblingSeparation(){
		assertEquals(30, chart.getSiblingSeparation(), 0.0);
	}

	@Test
	public void testGetSubTreeSeparation(){
		assertEquals(30, chart.getSubTreeSeparation(), 0.0);
	}

	@Test
	public void testGetNode(){
		assertEquals(ImmutableMap.of("collapsable", true), chart.getNode());
	}
}