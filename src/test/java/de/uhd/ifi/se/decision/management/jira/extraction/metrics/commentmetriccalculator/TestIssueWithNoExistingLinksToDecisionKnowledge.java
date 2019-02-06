package de.uhd.ifi.se.decision.management.jira.extraction.metrics.commentmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestIssueWithNoExistingLinksToDecisionKnowledge extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testTypeNull(){
		assertEquals("",calculator.issuesWithNoExistingLinksToDecisionKnowledge(null));
	}

	@Test
	@NonTransactional
	public void testTypeNoElements(){
		assertEquals(" ", calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.DECISION));
	}

	@Test
	@NonTransactional
	public void testTypeFilled(){
		assertEquals(" TEST-12 ",calculator.issuesWithNoExistingLinksToDecisionKnowledge(KnowledgeType.ARGUMENT));
	}
}
