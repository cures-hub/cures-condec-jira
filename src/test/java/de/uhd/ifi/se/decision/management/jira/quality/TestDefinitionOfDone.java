package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestDefinitionOfDone {

	private DefinitionOfDone definitionOfDone;

	@Before
	public void setUp() {
		definitionOfDone = new DefinitionOfDone();
	}

	@Test
	public void testRequiredCoverageExplanation() {
		assertEquals("A minimum coverage of 2 decisions within a maximum link distance of 3 is required.",
				definitionOfDone.getRequiredCoverageExplanation());
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(1);
		assertEquals("A minimum coverage of 1 decision within a maximum link distance of 3 is required.",
				definitionOfDone.getRequiredCoverageExplanation());
	}

	@Test
	public void testLineNumberInCodeFile() {
		definitionOfDone.setLineNumbersInCodeFile(42);
		assertEquals(42, definitionOfDone.getLineNumbersInCodeFile());
	}
}