package de.uhd.ifi.se.decision.management.jira.extraction.metrics.commentmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetNumberOfRelevantSentences extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase(){
		assertEquals(2, calculator.getNumberOfRelevantSentences().size(), 0.0);
	}
}
