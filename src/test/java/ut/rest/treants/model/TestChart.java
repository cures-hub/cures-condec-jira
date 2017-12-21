package ut.rest.treants.model;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;

import com.atlassian.DecisionDocumentation.rest.treants.model.Chart;

public class TestChart {
	
	public void setUp() {
		Chart  chart = new Chart();
		assertNotNull(chart);
	}

}
