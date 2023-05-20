package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import net.java.ao.test.jdbc.NonTransactional;
import smile.validation.ClassificationMetrics;

public class TestTextClassifier extends TestSetUp {

	private static TextClassifier classifier;
	private static final List<String> TEST_SENTENCES = Arrays.asList("Pizza is preferred", "I have an issue");

	@BeforeClass
	public static void setUp() {
		init();
		classifier = TextClassifier.getInstance("TEST");
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		classifier.train();
	}

	@Test(expected = IllegalArgumentException.class)
	@NonTransactional
	public void testGetInstanceProjectKeyInvalid() {
		TextClassifier.getInstance(null);
	}

	@Test
	@NonTransactional
	public void testSetGroundTruthDataThroughFile() {
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		assertEquals(TestGroundTruthData.getTestGroundTruthDataFile().getName(),
				classifier.getGroundTruthData().getFileName());
	}

	@Test
	@NonTransactional
	public void testSetGroundTruthDataThroughFileName() {
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile().getName());
		assertEquals(TestGroundTruthData.getTestGroundTruthDataFile().getName(),
				classifier.getGroundTruthData().getFileName());
	}

	@Test
	@NonTransactional
	public void testGetGroundTruthData() {
		assertTrue(classifier.getGroundTruthData().getDataFrame().size() > 30);
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifierWith2FoldCrossValidation() {
		Map<String, ClassificationMetrics> evaluationResults = classifier.evaluate(2, ClassifierType.LR,
				ClassifierType.LR);
		for (Map.Entry<String, ClassificationMetrics> entry : evaluationResults.entrySet()) {
			if (entry.getKey().startsWith("Binary")) {
				assertEquals(0.7, entry.getValue().accuracy, 0.5);
			}
		}
		assertEquals(0.4, evaluationResults.get("Fine-grained Alternative").accuracy, 0.4);
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifierOnSameDataAsTraining() {
		// Training and evaluating the classifier on the same data should not be done in
		// reality, this is only for unit testing!
		Map<String, ClassificationMetrics> evaluationResults = classifier.evaluate(-1, null, null);
		for (Map.Entry<String, ClassificationMetrics> entry : evaluationResults.entrySet()) {
			if (entry.getKey().startsWith("Binary")) {
				assertEquals(0.9, entry.getValue().f1, 0.1);
			}
		}
		assertEquals(0.9, evaluationResults.get("Fine-grained Alternative").f1, 0.2);
	}

	@Test
	@NonTransactional
	public void testUpdateOnlineLearningDisabled() {
		classifier.activateOnlineLearning(false);
		assertFalse(classifier.update(null));
	}

	@Test
	@NonTransactional
	public void testUpdateOnlineLearningEnabled() {
		// precondition: classifier freshly trained
		classifier.train(ClassifierType.LR, ClassifierType.LR);
		assertEquals(KnowledgeType.ALTERNATIVE,
				classifier.getFineGrainedClassifier().predict("Increases extensibility"));
		assertTrue(classifier.getBinaryClassifier().model.online());
		assertTrue(classifier.getFineGrainedClassifier().model.online());

		// test steps: update classifier with new part of text
		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setDescription("Increases extensibility");
		sentence.setSummary("Increases extensibility");
		sentence.setRelevant(true);
		sentence.setType(KnowledgeType.PRO);
		sentence.setValidated(true);

		classifier.activateOnlineLearning(true);
		assertTrue(classifier.update(sentence));
		assertTrue(classifier.update(sentence));
		assertTrue(classifier.update(sentence));
		assertTrue(classifier.update(sentence));

		// postcondition: classifier now recognizes the correct knowledge type
		// should be pro-argument!
		assertEquals(KnowledgeType.ALTERNATIVE,
				classifier.getFineGrainedClassifier().predict("Increases extensibility"));
	}

	// TODO: tests with unvalidated data element

	@Test
	@NonTransactional
	public void testMakeBinaryPrediction() {
		assertEquals(2, classifier.getBinaryClassifier().predict(TEST_SENTENCES).length);
	}

	@Test
	@NonTransactional
	public void testMakeFineGrainedPrediction() {
		assertEquals(2, classifier.getFineGrainedClassifier().predict(TEST_SENTENCES).size());
	}

	@Test
	@NonTransactional
	public void testTrainInvalidFile() {
		assertFalse(classifier.train(null, ClassifierType.LR, ClassifierType.LR));
		assertFalse(classifier.train("", ClassifierType.LR, ClassifierType.LR));
	}

	@Test
	@NonTransactional
	public void testTrainValidFile() {
		assertTrue(classifier.train(TestGroundTruthData.getTestGroundTruthDataFile().getName(), ClassifierType.LR,
				ClassifierType.LR));
	}

	@Test
	@NonTransactional
	public void testIsTraining() {
		assertFalse(classifier.isTraining());
	}

	@Test
	@NonTransactional
	public void testIsTrained() {
		assertTrue(classifier.isTrained());
	}

	@Test
	@NonTransactional
	public void testTrainNaiveBayes() {
		assertTrue(classifier.train(ClassifierType.NB, ClassifierType.NB));
	}

	@Test
	@NonTransactional
	public void testTrainSVM() {
		assertTrue(classifier.train(ClassifierType.SVM, ClassifierType.SVM));
	}

	@Test
	@NonTransactional
	public void testfitSVMmaxNumberOfTrainingSamplesReached() {
		PreprocessedData preprocessedData = new PreprocessedData(classifier.getGroundTruthData(), false);
		assertNotNull(TextClassifier.fitSVM(preprocessedData.preprocessedSentences,
				preprocessedData.getIsRelevantLabels(), 1));
	}
}
