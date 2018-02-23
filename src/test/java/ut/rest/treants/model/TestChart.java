package ut.rest.treants.model;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;

<<<<<<< Updated upstream:src/test/java/ut/rest/treants/model/TestChart.java
import com.atlassian.DecisionDocumentation.rest.treants.model.Chart;
=======
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Chart;
>>>>>>> Stashed changes:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/rest/treants/model/TestChart.java

public class TestChart {
	
	@Test
	public void testExist() {
		Chart  chart = new Chart();
		assertNotNull(chart);
	}

}
