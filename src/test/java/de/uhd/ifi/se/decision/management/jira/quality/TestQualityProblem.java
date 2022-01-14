package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

public class TestQualityProblem {

	private QualityCriterionCheckResult qualityProblem;

	@Before
	public void setUp() {
		qualityProblem = new QualityCriterionCheckResult(QualityCriterionType.DECISION_COVERAGE_TOO_LOW);
	}

	@Test
	public void testName() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE_TOO_LOW.name(), qualityProblem.getName());
	}

	@Test
	public void testType() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE_TOO_LOW, qualityProblem.getType());
	}

	@Test
	public void testExplanation() {
		assertEquals(QualityCriterionType.DECISION_COVERAGE_TOO_LOW.getViolationDescription(), qualityProblem.getExplanation());
		String betterExplanation = "This requirement is covered by only 1 decision, 3 decisions are required.";
		qualityProblem.setExplanation(betterExplanation);
		assertEquals(betterExplanation, qualityProblem.getExplanation());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		assertFalse(qualityProblem.equals((Object) null));
		assertFalse(qualityProblem.equals(QualityCriterionType.ALTERNATIVE_DOESNT_HAVE_ARGUMENT));
		assertTrue(qualityProblem.equals(qualityProblem));
		assertTrue(qualityProblem.equals(new QualityCriterionCheckResult(QualityCriterionType.DECISION_COVERAGE_TOO_LOW)));
		assertFalse(qualityProblem.equals(new QualityCriterionCheckResult(QualityCriterionType.INCOMPLETE_KNOWLEDGE_LINKED)));
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(qualityProblem.getName(), qualityProblem.getExplanation()),
				qualityProblem.hashCode());
	}
}