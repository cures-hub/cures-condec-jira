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
public class TestGetLinkDistance extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testTypeNull(){
		assertEquals(0, calculator.getLinkDistance(null).size(), 0.0);
	}


	@Test
	@NonTransactional
	public void testTypeFilled(){
		assertEquals(1, calculator.getLinkDistance(KnowledgeType.ARGUMENT).size(), 0.0);
	}
}
