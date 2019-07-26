package de.uhd.ifi.se.decision.management.jira.quality.commentmetriccalculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetLinkDistance extends TestSetupCalculator {

	@Test
	@NonTransactional
	public void testTypeNull() {
		assertEquals(0, calculator.getLinkDistance(null).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testTypeFilled() {
		assertEquals(1, calculator.getLinkDistance(KnowledgeType.ARGUMENT).size(), 0.0);
	}
}
