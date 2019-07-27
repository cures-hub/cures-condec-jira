package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import meka.classifiers.multilabel.LC;
import net.java.ao.test.jdbc.NonTransactional;
import weka.classifiers.meta.FilteredClassifier;

public class TestClassificationManagerForJiraIssueComments extends TestSetUp {

	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueComments classificationManager;
	private Issue issue;

	@Before
	public void setUp() {
		init();
		classificationManager = new ClassificationManagerForJiraIssueComments();
		FilteredClassifier binaryClassifier = new BinaryClassifierMock();
		classificationManager.getClassifier().setBinaryClassifier(binaryClassifier);
		LC lc = new FineGrainedClassifierMock(5);
		classificationManager.getClassifier().setFineGrainedClassifier(lc);
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
		String comment = "This is a testentence without any purpose. We expect this to be irrelevant. I got a problem in this class. The previous sentence should be much more relevant";
		commentManager.create(issue, currentUser, comment, true);
	}

	private void fillSentenceList() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
	}

	@Test
	@NonTransactional
	public void testBinaryClassification() {
		sentences = classificationManager.classifySentencesBinary(sentences);
		assertNotNull(sentences.get(0).isRelevant());
		assertFalse(sentences.get(0).isValidated());
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
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidDataInAO() {
		sentences.get(0).setRelevant(true);
		sentences.get(0).setDescription("[issue]nonplaintext[/issue]");

		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertNotNull(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
	}
}
