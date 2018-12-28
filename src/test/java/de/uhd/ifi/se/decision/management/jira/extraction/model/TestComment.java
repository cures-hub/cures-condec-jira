package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class)
public class TestComment extends TestSetUpWithIssues {

	private EntityManager entityManager;
	private MockIssue issue;

	private com.atlassian.jira.issue.comments.Comment comment1;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		createLocalIssue();
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

	private void addCommentsToIssue(String comment) {

		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		comment1 = commentManager.create(issue, currentUser, comment, true);

	}

	public CommentImpl getComment(String text) {
		createLocalIssue();

		addCommentsToIssue(text);
		return new CommentImpl(comment1, true);
	}

	@Test
	public void testConstructor() {
		assertNotNull(new CommentImpl());
	}

	@Test
	public void testSentencesAreNotNull() {
		assertNotNull(new CommentImpl().getSentences());
	}

	@Test
	@NonTransactional
	public void testCommentIsCreated() {
		assertNotNull(getComment("This is a test Sentence. With two sentences"));
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuote() {
		CommentImpl comment = getComment("{quote} this is a quote {quote} and this is a test Sentence.");
		assertNotNull(comment);
		assertEquals(2, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuoteAtTheBack() {
		CommentImpl comment = getComment("and this is a test Sentence. {quote} this is a quote {quote} ");
		assertNotNull(comment);
		assertEquals(2, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotes() {
		CommentImpl comment = getComment(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertNotNull(comment);
		assertEquals(3, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithDifferentQuotes() {
		CommentImpl comment = getComment("{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());

		comment = getComment(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, comment.getSentences().size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithDifferentQuotes2() {
		CommentImpl comment = getComment(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} {quote} These are many quotes {quote}");
		assertEquals(3, comment.getSentences().size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats() {
		CommentImpl comment = getComment("{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, comment.getSentences().size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats2() {
		CommentImpl comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformatsAndQuotes() {
		CommentImpl comment = getComment(
				"{noformat} this is a noformat {noformat} {quote} and this is a test Sentence.{quote}");
		assertEquals(2, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testSentenceOrder() {
		CommentImpl comment = getComment(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());
		assertTrue(comment.getSentences().get(0).getBody().contains("first"));
		assertTrue(comment.getSentences().get(1).getBody().contains("second"));
		assertTrue(comment.getSentences().get(2).getBody().contains("third"));
		assertTrue(comment.getSentences().get(3).getBody().contains("fourth"));
		assertTrue(comment.getSentences().get(4).getBody().contains("fifth"));
		assertTrue(comment.getSentences().get(5).getBody().contains("sixth"));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithUnknownTag() {
		CommentImpl comment = getComment(
				"{noformat} this is a noformat {noformat} {wuzl} and this is a test Sentence {wuzl}");
		assertEquals(2, comment.getSentences().size());
		assertTrue(comment.getSentences().get(0).getBody().contains("{noformat} this is a noformat {noformat}"));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithCodeTag() {
		CommentImpl comment = getComment("{code:Java} int i = 0 {code} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {code:java} this is a code {code} and this is a test Sentence.");
		assertEquals(6, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a first code {code} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} and this is a test Sentence. {code:java} this is a second code {code} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());

		comment = getComment(
				"{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} {code:java} These are many codes {code}");
		assertEquals(3, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfCodeElementedText() {
		CommentImpl comment = getComment("{code:Java} int i = 0 {code} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		assertEquals(false, comment.getSentences().get(0).isRelevant());
		assertEquals(false, comment.getSentences().get(0).isPlainText());
		assertEquals(false, comment.getSentences().get(0).isTagged());
		assertEquals(false, comment.getSentences().get(0).isTaggedManually());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfNoFormatElementedText() {
		CommentImpl comment = getComment("{noformat} int i = 0 {noformat} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		assertEquals(false, comment.getSentences().get(0).isRelevant());
		assertEquals(false, comment.getSentences().get(0).isPlainText());
		assertEquals(false, comment.getSentences().get(0).isTagged());
		assertEquals(false, comment.getSentences().get(0).isTaggedManually());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfQuotedElementedText() {
		CommentImpl comment = getComment("{quote} int i = 0 {quote} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		assertEquals(false, comment.getSentences().get(0).isRelevant());
		assertEquals(false, comment.getSentences().get(0).isPlainText());
		assertEquals(false, comment.getSentences().get(0).isTagged());
		assertEquals(false, comment.getSentences().get(0).isTaggedManually());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfTaggedElementedText() {
		CommentImpl comment = getComment(
				"{Alternative} this is a manually created alternative {Alternative} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		assertEquals(true, comment.getSentences().get(0).isRelevant());
		assertEquals(false, comment.getSentences().get(0).isPlainText());
		assertEquals(true, comment.getSentences().get(0).isTagged());
		assertEquals(true, comment.getSentences().get(0).isTaggedManually());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfIconElementedText() {
		CommentImpl comment = getComment("(y) this is a icon pro text. \r\n and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		assertEquals(true, comment.getSentences().get(0).isRelevant());
		assertEquals(false, comment.getSentences().get(0).isPlainText());
		assertEquals(true, comment.getSentences().get(0).isTagged());
		assertEquals(true, comment.getSentences().get(0).isTaggedManually());
		assertEquals(KnowledgeType.PRO, comment.getSentences().get(0).getType());
	}

	@Test
	@NonTransactional
	public void testSetSentences() {
		Comment comment = new CommentImpl();
		comment.setSentences(new ArrayList<Sentence>());
		assertNotNull(comment.getSentences());
		assertEquals(0, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void testGetSetBody() {
		Comment comment = new CommentImpl();
		comment.setBody("test");
		assertEquals("test", comment.getBody());
	}

	@Test
	@NonTransactional
	public void testGetSetAuthorId() {
		Comment comment = new CommentImpl();
		comment.setAuthorId((long) 1337);
		assertEquals(1337, comment.getAuthorId());
	}

	@Test
	@NonTransactional
	public void testSetProjectKey() {
		Comment comment = new CommentImpl();
		comment.setProjectKey("Test");
		assertEquals("Test", comment.getProjectKey());
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}
}
