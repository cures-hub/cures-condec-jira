package de.uhd.ifi.se.decision.management.jira.extraction.metrics.commentmetriccalculator;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetLinksToIssueTypeMap extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testTypeNull() {
		assertNull(calculator.getLinksToIssueTypeMap(null));
	}

	@Test
	@NonTransactional
	public void testTypeFilled() {
		System.out.println(calculator.getLinksToIssueTypeMap(KnowledgeType.ARGUMENT));
	}
}
