package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
	@Ignore
	public void testTypeFilled() {
		Object map = calculator.getLinksToIssueTypeMap(KnowledgeType.ARGUMENT);
		assertEquals("{Links from problem to Argument=, No links from problem to Argument=}", map.toString());
	}
}
