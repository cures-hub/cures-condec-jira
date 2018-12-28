package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import weka.classifiers.meta.FilteredClassifier;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class)
public class TestClassificationManagerForCommentSentences extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private List<Comment> list = new ArrayList<Comment>();
	private ClassificationManagerForCommentSentences classificationManager;

	private MutableIssue issue;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		classificationManager = new ClassificationManagerForCommentSentences();
		FilteredClassifier binaryClassifier = new BinaryClassifierMock();
		classificationManager.getClassifier().setBinaryClassifier(binaryClassifier);
		LC lc = new FineGrainedClassifierMock(5);
		classificationManager.getClassifier().setFineGrainedClassifier(lc);

		createLocalIssue();
		addCommentsToIssue();
		fillCommentList();
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

	private void createLocalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
	}

	private void fillCommentList() {
		list.add(new CommentImpl(ComponentAccessor.getCommentManager().getLastComment(issue),true));

	}

	@Test
	@NonTransactional
	public void testBinaryClassification() throws Exception {
		list = classificationManager.classifySentenceBinary(list);
		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTagged());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassification() throws Exception {
		list = classificationManager.classifySentenceBinary(list);
		list = classificationManager.classifySentenceFineGrained(list);

		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTagged());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidData() throws Exception {
		list.get(0).getSentences().get(0).setRelevant(true);
		list = classificationManager.classifySentenceFineGrained(list);

		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTaggedFineGrained());
	}

	@Test
	@NonTransactional
	public void testFineGrainedClassificationWithValidDataInAO() throws Exception {
		list.get(0).getSentences().get(0).setRelevant(true);
		list.get(0).getSentences().get(0).setBody("[issue]nonplaintext[/issue]");

		list = classificationManager.classifySentenceFineGrained(list);

		assertNotNull(list.get(0).getSentences().get(0).isRelevant());
		assertTrue(list.get(0).getSentences().get(0).isTaggedFineGrained());
	}
}
