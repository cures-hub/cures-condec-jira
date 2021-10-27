package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import smile.math.MathEx;

public class TestPreprocessedData extends TestSetUp {

	private GroundTruthData trainingData;

	@Before
	public void setUp() {
		init();
		TextClassifier trainer = TextClassifier.getInstance("TEST");
		trainer.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		trainingData = trainer.getGroundTruthData();
	}

	@Test
	public void testTrainingDataNotNull() {
		assertNotNull(trainingData);
	}

	@Test
	public void testBinary() {
		assertEquals(41, trainingData.getRelevanceLabelsForAllSentences().length);
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		assertEquals(313, preprocessedData.preprocessedSentences.length);
		assertEquals(313, preprocessedData.updatedLabels.length);
		// assertEquals(0.36143, preprocessedData.preprocessedSentences[0][0]);
		assertEquals(1, preprocessedData.updatedLabels[0]);
		assertEquals(1, preprocessedData.updatedLabels[1]);
		assertEquals(1, preprocessedData.updatedLabels[2]);
		assertEquals(1, preprocessedData.updatedLabels[3]);
		assertEquals(0, preprocessedData.updatedLabels[300]);
		assertEquals(-1, preprocessedData.getIsRelevantLabels()[300]);
	}

	@Test
	public void testFineGrained() {
		assertEquals(34, trainingData.getKnowledgeTypeLabelsForRelevantSentences().length);
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		assertEquals(246, preprocessedData.preprocessedSentences.length);
		assertEquals(246, preprocessedData.updatedLabels.length);
		// assertEquals(0.36143, preprocessedData.preprocessedSentences[0][0]);
		assertEquals(4, preprocessedData.updatedLabels[0]);
		assertEquals(4, preprocessedData.updatedLabels[1]);
		assertEquals(4, preprocessedData.updatedLabels[10]);
		int[] uniqueLabels = MathEx.unique(preprocessedData.updatedLabels);
		assertEquals(5, uniqueLabels.length);
		assertEquals(preprocessedData.preprocessedSentences.length, preprocessedData.updatedLabels.length);
		assertNotNull(preprocessedData.preprocessedSentences[preprocessedData.updatedLabels.length - 1]);

		assertEquals(1, preprocessedData.getIsIssueLabels()[0]);
		assertEquals(-1, preprocessedData.getIsIssueLabels()[60]);
		assertEquals(1, preprocessedData.getIsAlternativeLabels()[60]);
	}

}
