package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager.TestJiraIssueCommentPersistenceManagerSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAutoLinkSentences extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testSmartLinkingForProAlternative() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{alternative}first sentence{alternative} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConAlternative() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{alternative}first sentence{alternative} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProDecision() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{decision}first sentence{decision} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConDecision() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{decision}first sentence{decision} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForAlternativeIssue() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{issue}first sentence{issue} {alternative}second sentence{alternative}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForDecisionIssue() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{issue}first sentence{issue} {decision}second sentence{decision}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForBoringNonSmartLink() {
		TestComment tc = new TestComment();
		List<Sentence> comment = tc
				.getSentencesForCommentText("{issue}first sentence{issue} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals("s3 to i30", sentenceLink.toString());
	}
}
