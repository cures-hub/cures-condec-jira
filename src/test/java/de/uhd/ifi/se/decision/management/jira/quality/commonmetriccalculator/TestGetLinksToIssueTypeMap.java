package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

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
		assertEquals("{Links from other to Argument=0, No links from other to Argument=0}", map.toString());
	}
}
