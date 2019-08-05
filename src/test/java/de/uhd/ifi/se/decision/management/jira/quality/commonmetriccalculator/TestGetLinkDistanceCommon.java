package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestGetLinkDistanceCommon extends SetupCommonCalculator {

	@Test
	public void testTypeNull() {
		assertEquals(0, calculator.getLinkDistance(null).size(), 0.0);
	}

	@Test
	@Ignore
	public void testTypeFilled() {
		// TODO tests below evaluate differently on local machines (Windows) and Travis server!!!
		assertEquals(1, calculator.getLinkDistance(KnowledgeType.DECISION).size(), 0.0);
		assertEquals(2, calculator.getLinkDistance(KnowledgeType.ARGUMENT).size(), 0.0);
	}
}
