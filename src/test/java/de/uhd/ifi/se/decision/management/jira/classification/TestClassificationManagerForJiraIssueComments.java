package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassificationManagerForJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestClassificationManagerForJiraIssueComments extends TestSetUp {

	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueText classificationManager;
	private Issue issue;

	@Before
	public void setUp() {
		init();
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingFile(TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile());
		trainer.train();
		classificationManager = new ClassificationManagerForJiraIssueText();
		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");

		addCommentsToIssue();
		fillSentenceList();
	}

	private void addCommentsToIssue() {
		// Get the current logged in user
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		String comment = "This is a testentence without any purpose. We expect this to be irrelevant. How can we implement? The previous sentence should be much more relevant";
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
		assertEquals(KnowledgeType.ISSUE, sentences.get(0).getType());
	}

}
