package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

public class TestQualityProblem {

	private QualityProblem qualityProblem;

	@Before
	public void setUp() {
		qualityProblem = new QualityProblem(QualityProblemType.DECISION_COVERAGE_TOO_LOW);
	}

	@Test
	public void testName() {
		assertEquals(QualityProblemType.DECISION_COVERAGE_TOO_LOW.name(), qualityProblem.getName());
	}

	@Test
	public void testType() {
		assertEquals(QualityProblemType.DECISION_COVERAGE_TOO_LOW, qualityProblem.getType());
	}

	@Test
	public void testExplanation() {
		assertEquals(QualityProblemType.DECISION_COVERAGE_TOO_LOW.getDescription(), qualityProblem.getExplanation());
		String betterExplanation = "This requirement is covered by only 1 decision, 3 decisions are required.";
		qualityProblem.setExplanation(betterExplanation);
		assertEquals(betterExplanation, qualityProblem.getExplanation());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		assertFalse(qualityProblem.equals(null));
		assertFalse(qualityProblem.equals(QualityProblemType.ALTERNATIVE_DOESNT_HAVE_ARGUMENT));
		assertTrue(qualityProblem.equals(qualityProblem));
		assertTrue(qualityProblem.equals(new QualityProblem(QualityProblemType.DECISION_COVERAGE_TOO_LOW)));
		assertFalse(qualityProblem.equals(new QualityProblem(QualityProblemType.INCOMPLETE_KNOWLEDGE_LINKED)));
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(qualityProblem.getName(), qualityProblem.getExplanation()),
				qualityProblem.hashCode());
	}
}