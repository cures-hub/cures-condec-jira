package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestClassificationManagerForJiraIssueComments extends TestSetUp {

	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueComments classificationManager;
	private Issue issue;

	private KnowledgeElement createElement(KnowledgeType type, String summary) {
		KnowledgeElement element = new KnowledgeElement();
		element.setType(type);
		element.setSummary(summary);
		return element;
	}

	@Before
	public void setUp() {
		init();
		classificationManager = new ClassificationManagerForJiraIssueComments();
		classificationManager.getClassifierTrainer().setTrainingData(getTrainingData());
		classificationManager.getClassifierTrainer().train();

		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");

		addCommentsToIssue();
		fillSentenceList();
	}

	private List<KnowledgeElement> getTrainingData() {
		List<KnowledgeElement> trainingElements = new ArrayList<KnowledgeElement>();
		trainingElements.add(createElement(KnowledgeType.ISSUE, "I have an issue"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "Thats is a Decision"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "This is an Alternative"));
		trainingElements.add(createElement(KnowledgeType.PRO, "Pro"));
		trainingElements.add(createElement(KnowledgeType.CON, "Con"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Pizza is preferred"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "How to do that"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "We decided on option A."));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "An option would be"));
		trainingElements.add(createElement(KnowledgeType.PRO, "+1"));
		trainingElements.add(createElement(KnowledgeType.CON, "-1"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Lunch"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "I don't know how we can"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "We will do"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "A possible solution could be"));
		trainingElements.add(createElement(KnowledgeType.PRO, "Very good."));
		trainingElements.add(createElement(KnowledgeType.CON, "I don't agree"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Party tonight"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "The question is which library to choose"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "I implemented the feature."));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "We could have done option A."));
		trainingElements.add(createElement(KnowledgeType.PRO, "Great work guys!"));
		trainingElements.add(createElement(KnowledgeType.CON, "No that is not ok"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Hello"));
		return trainingElements;
	}

	private void addCommentsToIssue() {
		// Get the current logged in user
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		String comment = "This is a testentence without any purpose. We expect this to be irrelevant. The question is which library to choose. The previous sentence should be much more relevant";
		commentManager.create(issue, currentUser, comment, true);
	}

	private void fillSentenceList() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		sentences = persistenceManager.updateElementsOfCommentInDatabase(comment);
	}

	@Test
	@NonTransactional
	public void testBinaryClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		assertEquals("This is a testentence without any purpose.", sentences.get(0).getDescription());
		assertFalse(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
		assertFalse(sentences.get(2).isRelevant());
		assertFalse(sentences.get(2).isValidated());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertNotNull(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidData() {
		sentences.get(0).setRelevant(true);

		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertNotNull(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
		// assertEquals(KnowledgeType.ISSUE, sentences.get(0).getType());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidDataInAO() {
		sentences.get(0).setRelevant(true);
		sentences.get(0).setDescription("An option would be");

		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertNotNull(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
	}

}
