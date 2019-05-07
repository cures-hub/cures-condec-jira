package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import meka.classifiers.multilabel.LC;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import weka.classifiers.meta.FilteredClassifier;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTextSplitter.AoSentenceTestDatabaseUpdater.class)
public class TestClassificationManagerForJiraIssueComments extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueComments classificationManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		classificationManager = new ClassificationManagerForJiraIssueComments();
		FilteredClassifier binaryClassifier = new BinaryClassifierMock();
		classificationManager.getClassifier().setBinaryClassifier(binaryClassifier);
		LC lc = new FineGrainedClassifierMock(5);
		classificationManager.getClassifier().setFineGrainedClassifier(lc);

		createGlobalIssue();
		addCommentsToIssue();
		fillSentenceList();
	}

	private void addCommentsToIssue() {
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
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
	public void testFineGrainedClassificationWithValidDataInAO(){
		sentences.get(0).setRelevant(true);
		sentences.get(0).setDescription("[issue]nonplaintext[/issue]");

		sentences = classificationManager.classifySentencesFineGrained(sentences);

		assertNotNull(sentences.get(0).isRelevant());
		assertTrue(sentences.get(0).isTagged());
	}
}
