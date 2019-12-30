package de.uhd.ifi.se.decision.management.jira.quality.commonmetriccalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherTypeCommon extends TestSetUp {

	protected CommonMetricCalculator calculator;
	private DecisionKnowledgeElement decisionElement;
	private DecisionKnowledgeElement argumentElement;

	@Before
	public void setUp() {
		TestSetUpGit.setUpBeforeClass();
		init();

		decisionElement = JiraIssues.addElementToDataBase(18, "Decision");
		argumentElement = JiraIssues.addElementToDataBase(19, "Argument");

		ApplicationUser user = JiraUsers.BLACK_HEAD.getApplicationUser();
		calculator = new CommonMetricCalculator(1, user, "16");
	}

	@Test
	public void testTypesNull() {
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(null, null).size());
		assertEquals(0, calculator
				.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(null, KnowledgeType.DECISION).size());
		assertEquals(0, calculator
				.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION, null).size());
	}

	@Test
	public void testSameTypes() {
		assertEquals(0, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION,
				KnowledgeType.DECISION).size());
	}

	@Test
	public void testForDifferentTypesNotLinked() {
		Map<String, String> calculation = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				argumentElement.getType(), decisionElement.getType());

		Map<String, String> calculation2 = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				decisionElement.getType(), argumentElement.getType());

		assertEquals(2, calculation.size(), 0.0); // expecting always two categories
		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertFalse(calculation.get("Argument has no Decision").isEmpty());
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertEquals("", calculation.get("Argument has Decision"));

		assertEquals(2, calculation2.size(), 0.0); // expecting always two categories
		assertTrue(calculation2.containsKey("Decision has no Argument"));
		assertFalse(calculation2.get("Decision has no Argument").isEmpty());
		assertTrue(calculation2.containsKey("Decision has Argument"));
		assertEquals("", calculation2.get("Decision has Argument"));
	}

	@Test
	@Ignore
	public void testForDifferentTypesLinked() {
		// long linkId = linkElements(user, decisionElement, argumentElement, "");
		/*
		 * Map<String, String> calculation =
		 * calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		 * argumentElement.getType(), decisionElement.getType());
		 * 
		 * Map<String, String> calculation2 =
		 * calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
		 * decisionElement.getType(), argumentElement.getType());
		 * 
		 * assertEquals(2, calculation.size(),0.0); // expecting always two categories
		 * assertTrue(calculation.containsKey("Argument has no Decision"));
		 * assertEquals("", calculation.get("Argument has no Decision"));
		 * assertTrue(calculation.containsKey("Argument has Decision"));
		 * assertNotSame("", calculation.get("Argument has Decision"));
		 * 
		 * assertEquals(2, calculation2.size(),0.0); // expecting always two categories
		 * assertTrue(calculation2.containsKey("Decision has no Argument"));
		 * assertEquals("", calculation2.get("Decision has no Argument"));
		 * assertTrue(calculation2.containsKey("Decision has Argument"));
		 * assertNotSame("",calculation2.get("Decision has Argument"));
		 */
	}
}
