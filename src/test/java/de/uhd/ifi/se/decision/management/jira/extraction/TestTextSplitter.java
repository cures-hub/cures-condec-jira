package de.uhd.ifi.se.decision.management.jira.extraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTextSplitter.AoSentenceTestDatabaseUpdater.class)
public class TestTextSplitter extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	public static List<PartOfJiraIssueText> getSentencesForCommentText(String text) {
		Issue issue = TestSetUpWithIssues.createIssue();
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		ComponentAccessor.getCommentManager().deleteCommentsForIssue(issue);
		Comment comment = ComponentAccessor.getCommentManager().create(issue, currentUser, text, true);
		List<PartOfJiraIssueText> sentences = JiraIssueTextPersistenceManager.getPartsOfComment(comment);
		return sentences;
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoSentences() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("This is a test Sentence. With two sentences");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuote() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithOneQuoteAtTheBack() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"and this is a test Sentence. {quote} this is a quote {quote} ");
		assertEquals(2, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotes() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("{quote} this is a quote {quote} "
				+ "and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotesAndSentenceAfterwards() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{quote} this is a quote {quote} and this is a test Sentence. "
						+ "{quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, sentences.size());
	}

	@Test
	@NonTransactional
	public void testCommentWithTwoQuotesBehindEachOther() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("{quote} this is a quote {quote} "
				+ "{quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformats() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText("{noformat} this is a noformat {noformat} "
				+ "and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithNoformatsAndQuotes() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} and this is a test Sentence. "
						+ "{quote} this is a also a quote {quote}{quote} this is a also a quote {quote} "
						+ "{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, sentences.size());
	}

	@Test
	@NonTransactional
	public void testSentenceOrder() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{noformat} this is a first noformat {noformat} and this is a second test Sentence. "
						+ "{quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} "
						+ "{noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, sentences.size());
		assertTrue(sentences.get(0).getDescription().contains("first"));
		assertTrue(sentences.get(1).getDescription().contains("second"));
		assertTrue(sentences.get(2).getDescription().contains("third"));
		assertTrue(sentences.get(3).getDescription().contains("fourth"));
		assertTrue(sentences.get(4).getDescription().contains("fifth"));
		assertTrue(sentences.get(5).getDescription().contains("sixth"));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithUnknownTag() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{noformat} this is a noformat {noformat} {wuzl} and this is a test Sentence {wuzl}");
		assertEquals(2, sentences.size());
		assertTrue(sentences.get(0).getDescription().contains(" this is a noformat "));
	}

	@Test
	@NonTransactional
	public void testSentenceSplitWithCodeTag() {
		List<PartOfJiraIssueText> sentences = getSentencesForCommentText(
				"{code:java} this is a first code {code} and this is a second test Sentence. "
						+ "{quote} this is a also a third quote {quote}"
						+ "{quote} this is a also a fourth quote {quote} "
						+ "{code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, sentences.size());
	}
}
