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
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestClassificationManagerForJiraIssueText extends TestSetUp {

	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueText classificationManager;
	private JiraIssueTextPersistenceManager persistenceManager;

	@Before
	public void setUp() {
		init();
		TextClassifier classifier = TextClassifier.getInstance("TEST");
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		classifier.train();
		classificationManager = new ClassificationManagerForJiraIssueText("TEST");
		sentences = JiraIssues
				.getSentencesForCommentText("Thanks for updating the unit tests! We expect this to be irrelevant. "
						+ "How can we implement? The previous sentence should be much more relevant");
		persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST").getJiraIssueTextManager();
	}

	@Test
	@NonTransactional
	public void testClassifyDescriptionAndAllComments() {
		classificationManager.classifyDescriptionAndAllComments(JiraIssues.getTestJiraIssues().get(0));
		List<PartOfJiraIssueText> sentences = persistenceManager
				.getElementsInDescription(JiraIssues.getTestJiraIssues().get(0).getId());
		assertFalse(sentences.isEmpty());
		assertEquals(KnowledgeType.ALTERNATIVE, sentences.get(0).getType());
	}

	@Test
	@NonTransactional
	public void testBinaryClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		assertEquals("Thanks for updating the unit tests!", sentences.get(0).getDescription());
		assertFalse(sentences.get(0).isRelevant());
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

		assertFalse(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
		assertTrue(sentences.get(2).isRelevant());
		assertEquals(KnowledgeType.ISSUE, sentences.get(2).getType());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidData() {
		sentences.get(2).setRelevant(true);
		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertTrue(sentences.get(2).isRelevant());
		assertTrue(sentences.get(2).isTagged());
		assertEquals("How can we implement?", sentences.get(2).getSummary());
		assertEquals(KnowledgeType.ISSUE, sentences.get(2).getType());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidDataInDatabase() {
		sentences.get(0).setRelevant(true);
		sentences.get(0).setDescription("An option would be");

		sentences = classificationManager.classifySentencesFineGrained(sentences);
		assertTrue(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
		assertEquals(KnowledgeType.ALTERNATIVE, sentences.get(0).getType());
	}

}
