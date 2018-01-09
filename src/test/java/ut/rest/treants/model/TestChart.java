package ut.rest.treants.model;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.treants.model.Chart;

public class TestChart {
	
	@Test
	public void testExist() {
		Chart  chart = new Chart();
		assertNotNull(chart);
	}

}
