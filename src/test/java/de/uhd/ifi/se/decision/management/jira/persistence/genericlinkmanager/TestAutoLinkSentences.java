package de.uhd.ifi.se.decision.management.jira.persistence.genericlinkmanager;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager.TestJiraIssueCommentPersistenceMangerSetUp;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAutoLinkSentences extends TestJiraIssueCommentPersistenceMangerSetUp {

    @Test
    @NonTransactional
    public void testSmartLinkingForProAlternative() {
        Comment comment = getComment("{alternative}first sentence{alternative} {pro}second sentence{pro}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForConAlternative() {
        Comment comment = getComment("{alternative}first sentence{alternative} {con}second sentence{con}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForProDecision() {
        Comment comment = getComment("{decision}first sentence{decision} {pro}second sentence{pro}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForConDecision() {
        Comment comment = getComment("{decision}first sentence{decision} {con}second sentence{con}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForAlternativeIssue() {
        Comment comment = getComment("{issue}first sentence{issue} {alternative}second sentence{alternative}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForDecisionIssue() {
        Comment comment = getComment("{issue}first sentence{issue} {decision}second sentence{decision}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals(sentenceLink.getOppositeElement(comment.getSentences().get(0)).getId(),
                comment.getSentences().get(1).getId());
    }

    @Test
    @NonTransactional
    public void testSmartLinkingForBoringNonSmartLink() {
        Comment comment = getComment("{issue}first sentence{issue} {pro}second sentence{pro}");
        Link sentenceLink = GenericLinkManager.getLinksForElement(comment.getSentences().get(1)).get(0);
        assertEquals("s3 to i30", sentenceLink.toString());
    }
}
