package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestClassificationManagerForJiraIssueComments extends TestSetUp {

	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueText classificationManager;

	@Before
	public void setUp() {
		init();
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		classificationManager = new ClassificationManagerForJiraIssueText();
		sentences = JiraIssues.getSentencesForCommentText(
				"This is a testentence without any purpose. We expect this to be irrelevant. "
						+ "How can we implement? The previous sentence should be much more relevant");
	}

	@Test
	@NonTransactional
	public void testBinaryClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		assertEquals("This is a testentence without any purpose.", sentences.get(0).getDescription());
		assertTrue(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
		assertEquals("How can we implement?", sentences.get(2).getSummary());
		assertTrue(sentences.get(2).isRelevant());
		assertFalse(sentences.get(2).isValidated());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertTrue(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidData() {
		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertFalse(sentences.get(2).isRelevant());
		assertFalse(sentences.get(2).isTagged());
		assertEquals("How can we implement?", sentences.get(2).getSummary());
		// assertEquals(KnowledgeType.ISSUE, sentences.get(0).getType());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidDataInAO() {
		sentences.get(0).setRelevant(true);
		sentences.get(0).setDescription("An option would be");

		sentences = classificationManager.classifySentencesFineGrained(sentences);

		// why?
		assertTrue(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
		assertEquals(KnowledgeType.PRO, sentences.get(0).getType());
	}

}
