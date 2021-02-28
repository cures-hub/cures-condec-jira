package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.java.ao.test.jdbc.NonTransactional;

public class TestTextClassificationConfiguration {

	private TextClassificationConfiguration config;

	@Before
	public void setUp() {
		config = new TextClassificationConfiguration();
	}

	@Test
	@NonTransactional
	public void testGetEvaluationResults() {
		assertTrue(config.getLastEvaluationResults().isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetSelectedGroundTruthFileName() {
		assertEquals("defaultTrainingData.csv", config.getSelectedGroundTruthFile());
	}

	@Test
	@NonTransactional
	public void testSelectedTrainedClassifier() {
		config.setSelectedTrainedClassifier("");
		assertTrue(config.getSelectedTrainedClassifier().isEmpty());
	}

}
