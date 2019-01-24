package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.TestCommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager.TestJiraIssueCommentPersistenceManagerSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAutoLinkSentences extends TestJiraIssueCommentPersistenceManagerSetUp {

	@Test
	@NonTransactional
	public void testSmartLinkingForProAlternative() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{alternative}first sentence{alternative} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConAlternative() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{alternative}first sentence{alternative} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProDecision() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{decision}first sentence{decision} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConDecision() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{decision}first sentence{decision} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}
	
	@Test
	@NonTransactional
	public void testSmartLinkingForAlternativeIssue() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{issue}first sentence{issue} {alternative}second sentence{alternative}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForDecisionIssue() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{issue}first sentence{issue} {decision}second sentence{decision}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForBoringNonSmartLink() {
		List<Sentence> comment = TestCommentSplitter
				.getSentencesForCommentText("{issue}first sentence{issue} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals("s3 to i30", sentenceLink.toString());
	}
}
