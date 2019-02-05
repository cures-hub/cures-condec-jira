package de.uhd.ifi.se.decision.management.jira.extraction.metrics.commentmetriccalculator;

import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetNumberOfCommentsForJiraIssues extends TestSetupCalculator {


	@Test
	public void testCase(){
		Map<String, Integer> map = calculator.getNumberOfCommentsForJiraIssues();
		assertEquals(1, map.size() , 0.0);
	}
}
