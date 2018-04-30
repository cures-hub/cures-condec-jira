package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.view.treant.Chart;

public class TestChart {

	@Test
	public void testExist() {
		Chart  chart = new Chart();
		assertNotNull(chart);
	}

}
