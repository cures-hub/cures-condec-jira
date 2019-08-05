package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestGetNumberOfLinkToOtherElementCommon extends SetupCommonCalculator {

	@Test
	public void testLinkFromNullLinkToNull() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				null, null).size(), 0.0);
	}

	@Test
	public void testLinkFromFilledLinkToNull() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				KnowledgeType.DECISION, null).size(), 0.0);
	}

	@Test
	public void testLinkFromNullLinkToFilled() {
		assertEquals(0, calculator._getNumberOfLinksToOtherElement(
				null, KnowledgeType.ISSUE).size(), 0.0);
	}

	@Test
	public void testLinkFromFilledLinkToFilled() {
		assertEquals(2, calculator._getNumberOfLinksToOtherElement(
				KnowledgeType.ARGUMENT, KnowledgeType.DECISION).size(),
				0.0);
	}
}
