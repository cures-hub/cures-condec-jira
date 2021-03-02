package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;
import smile.validation.ClassificationMetrics;

public class TestTextClassifier extends TestSetUp {

	private TextClassifier classifier;
	private static final List<String> TEST_SENTENCES = Arrays.asList("Pizza is preferred", "I have an issue");

	@Before
	public void setUp() {
		init();
		classifier = TextClassifier.getInstance("TEST");
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
	}

	@Test
	@NonTransactional
	public void testSetTrainingData() {
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		assertEquals(TestGroundTruthData.getTestGroundTruthDataFile().getName(),
				classifier.getGroundTruthData().getFileName());
	}

	@Test
	@NonTransactional
	@Ignore
	public void testOnlineClassificationTrainerFromElementsInKnowledgeGraph() {
		File file = classifier.saveTrainingFile();
		assertTrue(file.exists());
		classifier.setGroundTruthFile(file);
		assertNotNull(classifier.getGroundTruthData());
		classifier = TextClassifier.getInstance("TEST");
		assertTrue(classifier.train(file.getName()));
		file.delete();
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifierWith3FoldCrossValidation() {
		Map<String, ClassificationMetrics> evaluationResults = classifier.evaluate(3);
		assertEquals(0.8, evaluationResults.get("Binary").f1, 0.1);
		assertEquals(0.1, evaluationResults.get("Fine-grained Alternative").f1, 0.4);
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifierOnSameDataAsTraining() {
		// Training and evaluating the classifier on the same data should not be done in
		// reality!
		classifier.train();
		Map<String, ClassificationMetrics> evaluationResults = classifier.evaluate(-1);
		assertEquals(0.9, evaluationResults.get("Binary").f1, 0.1);
		assertEquals(0.9, evaluationResults.get("Fine-grained Alternative").f1, 0.1);
	}

	@Test
	@NonTransactional
	public void testUpdate() {
		classifier.train();

		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setDescription("In my opinion the query would be better!");
		sentence.setRelevant(true);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		sentence.setValidated(true);

		TextClassificationConfiguration config = ConfigPersistenceManager.getTextClassificationConfiguration("TEST");
		config.setOnlineLearningActivated(true);
		ConfigPersistenceManager.saveTextClassificationConfiguration("TEST", config);
		assertTrue(TextClassifier.getInstance("TEST").update(sentence));
	}

	// TODO: tests with unvalidated data element

	@Test
	@NonTransactional
	public void testGetInstances() {
		assertNotNull(this.classifier.getGroundTruthData());
	}

	@Test
	@NonTransactional
	public void testMakeBinaryPredicition() {
		classifier.train();
		assertEquals(2, TextClassifier.getInstance("TEST").getBinaryClassifier().predict(TEST_SENTENCES).length);
	}

	@Test
	@NonTransactional
	public void testMakeFineGrainedPredicition() {
		assertEquals(2, TextClassifier.getInstance("TEST").getFineGrainedClassifier().predict(TEST_SENTENCES).size());
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

}
