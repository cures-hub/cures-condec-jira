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
public class TestGetNumberOfLinkToOtherElement extends TestSetupCalculator {

	@Test
	@NonTransactional
	@Ignore
	public void testLinkFromNullLinkToNull() {
		assertEquals(0, calculator.getNumberOfLinksToOtherElement(null, null).size(), 0.0);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testLinkFromFilledLinkToNull() {
		assertEquals(0, calculator.getNumberOfLinksToOtherElement(KnowledgeType.DECISION, null).size(), 0.0);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testLinkFromNullLinkToFilled() {
		assertEquals(0, calculator.getNumberOfLinksToOtherElement(null, KnowledgeType.ISSUE).size(), 0.0);
	}

	@Test
	@NonTransactional
	@Ignore
	public void testLinkFromFilledLinkToFilled() {
		assertEquals(2, calculator.getNumberOfLinksToOtherElement(KnowledgeType.ARGUMENT, KnowledgeType.ISSUE).size(),
				0.0);
	}
}
