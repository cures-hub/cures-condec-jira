package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

import net.java.ao.test.jdbc.NonTransactional;

public class TestDefinitionOfDone extends TestSetUp {

	@Test
	@NonTransactional
	public void testGetLinkDistanceFromCodeFileToDecision() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		int result = definitionOfDone.getLinkDistanceFromCodeFileToDecision();
		assertEquals(4, result);
	}

	@Test
	@NonTransactional
	public void testGetLineNumbersInCodeFile() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		int result = definitionOfDone.getLineNumbersInCodeFile();
		assertEquals(50, result);
	}

	@Test
	@NonTransactional
	public void testGetThreshold() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		int result = definitionOfDone.getThreshold();
		assertEquals(2, result);
	}
}
