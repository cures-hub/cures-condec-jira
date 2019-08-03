package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetNumberOfCommitsForJiraIssues extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testCase() {
		Map<String, Integer> expectedCommits = new HashMap<>();
		// TODO: setup some test commits
		//expectedCommits.put(baseIssueKey,0);
		assertEquals(expectedCommits, calculator.getNumberOfCommitsForJiraIssues());
	}
}
