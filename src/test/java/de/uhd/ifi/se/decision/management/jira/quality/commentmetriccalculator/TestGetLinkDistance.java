package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetLinkDistance extends TestSetupCalculator {

	@Test
	@NonTransactional
	@Ignore
	public void testTypeNull() {
		assertEquals(0, calculator.getLinkDistance(null).size(), 0.0);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testTypeFilled() {
		assertEquals(1, calculator.getLinkDistance(KnowledgeType.ARGUMENT).size(), 0.0);
	}
}
