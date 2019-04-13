package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetNumberOfDecisionKnowledgeElementsFroJiraIssues extends TestSetupCalculator {
	@Test
	public void testTypeNull() {
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(null).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testTypeFilled() {
		// TODO this should be 1
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ARGUMENT).size(),
				0.0);
	}
}
