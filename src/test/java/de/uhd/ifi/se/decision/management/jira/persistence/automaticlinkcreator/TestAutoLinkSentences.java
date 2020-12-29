package de.uhd.ifi.se.decision.management.jira.persistence.automaticlinkcreator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestAutoLinkSentences extends TestSetUp {

	protected static KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProAlternative() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative}first sentence{alternative} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		KnowledgeElement oppositeElement = sentenceLink.getOppositeElement(comment.get(0));
		assertNotNull(oppositeElement);
		assertEquals(oppositeElement.getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConAlternative() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{alternative}first sentence{alternative} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProDecision() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{decision}first sentence{decision} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForProDecisionAndAlternative() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"{decision}first sentence{decision} {alternative}second sentence{alternative} {pro}third sentence{pro}");

		KnowledgeElement decision = comment.get(0);
		assertEquals(KnowledgeType.DECISION, decision.getType());

		KnowledgeElement alternative = comment.get(1);
		assertEquals(KnowledgeType.ALTERNATIVE, alternative.getType());

		KnowledgeElement argument = comment.get(2);
		assertEquals(KnowledgeType.PRO, argument.getType());

		Link sentenceLink = GenericLinkManager.getLinksForElement(argument).get(0);

		// TODO Why is not the alternative linked? Should not the alternative be younger
		// than the decision?
		assertEquals(sentenceLink.getOppositeElement(decision), argument);
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForConDecision() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{decision}first sentence{decision} {con}second sentence{con}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForAlternativeIssue() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue}first sentence{issue} {alternative}second sentence{alternative}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForDecisionIssue() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue}first sentence{issue} {decision}second sentence{decision}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals(sentenceLink.getOppositeElement(comment.get(0)).getId(), comment.get(1).getId());
	}

	@Test
	@NonTransactional
	public void testSmartLinkingForBoringNonSmartLink() {
		List<PartOfJiraIssueText> comment = JiraIssues
				.getSentencesForCommentText("{issue}first sentence{issue} {pro}second sentence{pro}");
		Link sentenceLink = GenericLinkManager.getLinksForElement(comment.get(1)).get(0);
		assertEquals("s3 to i30", sentenceLink.toString());
	}
}