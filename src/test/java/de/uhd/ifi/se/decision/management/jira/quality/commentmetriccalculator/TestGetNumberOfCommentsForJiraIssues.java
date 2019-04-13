package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetNumberOfCommentsForJiraIssues extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		Map<String, Integer> map = calculator.getNumberOfCommentsForJiraIssues();
		// TODO this should be 1
		assertEquals(0, map.size(), 0.0);
	}
}
