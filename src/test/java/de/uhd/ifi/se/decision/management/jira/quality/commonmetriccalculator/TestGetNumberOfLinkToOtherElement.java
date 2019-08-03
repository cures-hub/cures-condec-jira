package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

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
public class TestGetNumberOfLinkToOtherElement extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testLinkFromNullLinkToNull() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				null, null).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testLinkFromFilledLinkToNull() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				KnowledgeType.DECISION, null).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testLinkFromNullLinkToFilled() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				null, KnowledgeType.ISSUE).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testLinkFromFilledLinkToFilled() {
		assertEquals(2, calculator._getNumberOfLinksToOtherElement(
				KnowledgeType.ARGUMENT, KnowledgeType.DECISION).size(),
				0.0);
	}
}
