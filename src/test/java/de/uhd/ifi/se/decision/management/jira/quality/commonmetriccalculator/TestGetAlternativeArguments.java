package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetAlternativeArguments extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		assertEquals(2, calculator.getAlternativesHavingArguments().size(), 0.0);
	}
}
