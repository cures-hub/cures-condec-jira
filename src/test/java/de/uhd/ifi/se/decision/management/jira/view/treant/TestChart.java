package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestChart {

	@Test
	public void testExist() {
		Chart chart = new Chart();
		assertNotNull(chart);
	}
}