package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

public class TestQualityCriterionCheckResult {

	private QualityCriterionCheckResult qualityCriterionCheckResult;

	@Before
	public void setUp() {
		qualityCriterionCheckResult = new QualityCriterionCheckResult(QualityCriterionType.DECISION_COVERAGE);
	}

	@Test
	public void testName() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE.toString(), qualityCriterionCheckResult.getName());
	}

	@Test
	public void testType() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE, qualityCriterionCheckResult.getType());
	}

	@Test
	public void testIsCriterionViolated() {
		assertTrue(qualityCriterionCheckResult.isCriterionViolated());
		qualityCriterionCheckResult.setCriterionViolated(false);
		assertFalse(qualityCriterionCheckResult.isCriterionViolated());
	}

	@Test
	public void testStandardFulfillmentExplanation() {
		qualityCriterionCheckResult = new QualityCriterionCheckResult(QualityCriterionType.DECISION_COVERAGE, false);
		assertEquals(QualityCriterionType.DECISION_COVERAGE.getFulfillmentDescription(),
				qualityCriterionCheckResult.getExplanation());
	}

	@Test
	public void testStandardViolationExplanation() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE.getViolationDescription(),
				qualityCriterionCheckResult.getExplanation());
	}

	@Test
	public void testCustomExplanation() {
		String betterExplanation = "This requirement is covered by only 1 decision, 3 decisions are required.";
		qualityCriterionCheckResult.setExplanation(betterExplanation);
		assertEquals(betterExplanation, qualityCriterionCheckResult.getExplanation());
	}

	@Test
	public void testAppendExplanation() {
		String appendedExplanation = "3 decisions are required.";
		qualityCriterionCheckResult.appendExplanation(appendedExplanation);
		assertEquals(QualityCriterionType.DECISION_COVERAGE.getViolationDescription() + " " + appendedExplanation,
				qualityCriterionCheckResult.getExplanation());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		assertFalse(qualityCriterionCheckResult.equals((Object) null));
		assertFalse(qualityCriterionCheckResult.equals(QualityCriterionType.ALTERNATIVE_LINKED_TO_ARGUMENT));
		assertTrue(qualityCriterionCheckResult.equals(qualityCriterionCheckResult));
		assertTrue(qualityCriterionCheckResult
				.equals(new QualityCriterionCheckResult(QualityCriterionType.DECISION_COVERAGE)));
		assertFalse(qualityCriterionCheckResult
				.equals(new QualityCriterionCheckResult(QualityCriterionType.QUALITY_OF_LINKED_KNOWLEDGE)));
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(qualityCriterionCheckResult.getName(), qualityCriterionCheckResult.getExplanation()),
				qualityCriterionCheckResult.hashCode());
	}
}