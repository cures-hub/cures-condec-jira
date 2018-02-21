package ut.de.uhd.ifi.se.decision.documentation.jira.rest.treants.model;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.rest.treants.model.Chart;

public class TestChart {
	
	@Test
	public void testExist() {
		Chart  chart = new Chart();
		assertNotNull(chart);
	}

}
