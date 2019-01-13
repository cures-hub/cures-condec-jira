package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertEquals;
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
import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
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

	private Comment comment1;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		createLocalIssue();
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

	public List<Sentence> getSentencesForCommentText(String text) {
		createLocalIssue();

		addCommentsToIssue(text);
		List<Sentence> sentences = new CommentSplitter().getSentences(comment1);
		return sentences;
	}

	@Test
	@NonTransactional
	public void testCommentIsCreated() {
		assertNotNull(getSentencesForCommentText("This is a test Sentence. With two sentences"));
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuote() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence.");
		assertNotNull(sentences);
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuoteAtTheBack() {
		List<Sentence> sentences = getSentencesForCommentText(
				"and this is a test Sentence. {quote} this is a quote {quote} ");
		assertNotNull(sentences);
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotes() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertNotNull(sentences);
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithDifferentQuotes() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, sentences.size());

		sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, sentences.size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithDifferentQuotes2() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, sentences.size());

		sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, sentences.size());

		sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} {quote} These are many quotes {quote}");
		assertEquals(3, sentences.size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(2, sentences.size());

		sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, sentences.size());

	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats2() {
		List<Sentence> sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, sentences.size());

		sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, sentences.size());

		sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformatsAndQuotes() {
		List<Sentence> comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {quote} and this is a test Sentence.{quote}");
		assertEquals(2, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, comment.size());

		comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, comment.size());
	}

	@Test
	@NonTransactional
	public void testSentenceOrder() {
		List<Sentence> comment = getSentencesForCommentText(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.size());
		assertTrue(comment.get(0).getDescription().contains("first"));
		assertTrue(comment.get(1).getDescription().contains("second"));
		assertTrue(comment.get(2).getDescription().contains("third"));
		assertTrue(comment.get(3).getDescription().contains("fourth"));
		assertTrue(comment.get(4).getDescription().contains("fifth"));
		assertTrue(comment.get(5).getDescription().contains("sixth"));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithUnknownTag() {
		List<Sentence> comment = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {wuzl} and this is a test Sentence {wuzl}");
		assertEquals(2, comment.size());
		assertTrue(comment.get(0).getDescription().contains(" this is a noformat "));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithCodeTag() {
		List<Sentence> comment = getSentencesForCommentText(
				"{code:Java} int i = 0 {code} and this is a test Sentence.");
		assertEquals(2, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {code:java} this is a code {code} and this is a test Sentence.");
		assertEquals(6, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a first code {code} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} and this is a test Sentence. {code:java} this is a second code {code} and a Sentence at the back");
		assertEquals(4, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} and a Sentence at the back");
		assertEquals(3, comment.size());

		comment = getSentencesForCommentText(
				"{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} {code:java} These are many codes {code}");
		assertEquals(3, comment.size());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfCodeElementedText() {
		List<Sentence> comment = getSentencesForCommentText(
				"{code:Java} int i = 0 {code} and this is a test Sentence.");
		assertEquals(2, comment.size());
		assertEquals(false, comment.get(0).isRelevant());
		assertEquals(false, comment.get(0).isPlainText());
		assertEquals(false, comment.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfNoFormatElementedText() {
		List<Sentence> comment = getSentencesForCommentText(
				"{noformat} int i = 0 {noformat} and this is a test Sentence.");
		assertEquals(2, comment.size());
		assertEquals(false, comment.get(0).isRelevant());
		assertEquals(false, comment.get(0).isPlainText());
		assertEquals(false, comment.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfQuotedElementedText() {
		List<Sentence> comment = getSentencesForCommentText("{quote} int i = 0 {quote} and this is a test Sentence.");
		assertEquals(2, comment.size());
		assertEquals(false, comment.get(0).isRelevant());
		assertEquals(false, comment.get(0).isPlainText());
		assertEquals(false, comment.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfTaggedElementedText() {
		List<Sentence> comment = getSentencesForCommentText(
				"{Alternative} this is a manually created alternative {Alternative} and this is a test Sentence.");
		assertEquals(2, comment.size());
		assertEquals(true, comment.get(0).isRelevant());
		//assertEquals(false, comment.get(0).isPlainText());
		// TODO
		assertEquals(true, comment.get(0).isValidated());
	}

	@Test
	@NonTransactional
	public void testPropertiesOfIconElementedText() {
		List<Sentence> comment = getSentencesForCommentText(
				"(y) this is a icon pro text. \r\n and this is a test Sentence.");
		assertEquals(2, comment.size());
		//assertEquals(true, comment.get(0).isRelevant());
	//	assertEquals(false, comment.get(0).isPlainText());
		// TODO
		// assertEquals(true, comment.get(0).isValidated());
		assertEquals(KnowledgeType.PRO, comment.get(0).getType());
	}

	// @Test
	// @NonTransactional
	// public void testSetSentences() {
	// List<Sentence> comment = new JiraIssueCommentImpl();
	// comment.setSentences(new ArrayList<Sentence>());
	// assertNotNull(comment);
	// assertEquals(0, comment.size());
	// }
	//
	// @Test
	// @NonTransactional
	// public void testGetSetBody() {
	// List<Sentence> comment = new JiraIssueCommentImpl();
	// comment.setBody("test");
	// assertEquals("test", comment.getBody());
	// }
	//
	// @Test
	// @NonTransactional
	// public void testGetSetAuthorId() {
	// JiraIssueComment comment = new JiraIssueCommentImpl();
	// comment.setAuthorId((long) 1337);
	// assertEquals(1337, comment.getAuthorId());
	// }
	//
	// @Test
	// @NonTransactional
	// public void testSetProjectKey() {
	// JiraIssueComment comment = new JiraIssueCommentImpl();
	// comment.setProjectKey("Test");
	// assertEquals("Test", comment.getProjectKey());
	// }

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}
}
