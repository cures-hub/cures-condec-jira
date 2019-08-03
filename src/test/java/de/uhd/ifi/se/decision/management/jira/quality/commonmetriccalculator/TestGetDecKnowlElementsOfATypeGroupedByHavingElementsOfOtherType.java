package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestGetDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType extends TestSetupCalculator {
	@Test
	public void testTypesNull() {
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(null, null).size(), 0.0);
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(null, KnowledgeType.DECISION).size(), 0.0);
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType( KnowledgeType.DECISION, null).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testSameTypes() {
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType( KnowledgeType.DECISION,KnowledgeType.DECISION).size(), 0.0);
	}

	@Test
	@NonTransactional
	public void testForDifferentTypesNotLinked() {
		Map<String, String> calculation = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				argumentElement.getType(), decisionElement.getType());

		Map<String, String> calculation2 = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				decisionElement.getType(), argumentElement.getType());

		assertEquals(2, calculation.size(),0.0); // expecting always two categories
		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertFalse(calculation.get("Argument has no Decision").isEmpty());
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertEquals("", calculation.get("Argument has Decision"));

		assertEquals(2, calculation2.size(),0.0); // expecting always two categories
		assertTrue(calculation2.containsKey("Decision has no Argument"));
		assertFalse(calculation2.get("Decision has no Argument").isEmpty());
		assertTrue(calculation2.containsKey("Decision has Argument"));
		assertEquals("", calculation2.get("Decision has Argument"));
	}

	@Test
	@NonTransactional
	@Ignore
	public void testForDifferentTypesLinked() {
		//long linkId = linkElements(user, decisionElement, argumentElement, "");

		Map<String, String> calculation = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				argumentElement.getType(), decisionElement.getType());

		Map<String, String> calculation2 = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				decisionElement.getType(), argumentElement.getType());
/*
		assertEquals(2, calculation.size(),0.0); // expecting always two categories
		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertEquals("", calculation.get("Argument has no Decision"));
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertNotSame("", calculation.get("Argument has Decision"));

		assertEquals(2, calculation2.size(),0.0); // expecting always two categories
		assertTrue(calculation2.containsKey("Decision has no Argument"));
		assertEquals("", calculation2.get("Decision has no Argument"));
		assertTrue(calculation2.containsKey("Decision has Argument"));
		assertNotSame("",calculation2.get("Decision has Argument"));
*/
	}
}
