package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestClassificationManagerForJiraIssueComments extends TestSetUp {
/*
	private List<PartOfJiraIssueText> sentences;
	private ClassificationManagerForJiraIssueComments classificationManager;
	private Issue issue;

	@Before
	public void setUp() {
		init();
		classificationManager = new ClassificationManagerForJiraIssueComments();
		FilteredClassifier binaryClassifier = new BinaryClassifierMock();
		classificationManager.getClassifierTrainer().setBinaryClassifier(binaryClassifier);
		LC lc = new FineGrainedClassifierMock(5);
		classificationManager.getClassifierTrainer().setFineGrainedClassifier(lc);
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

 */
}
