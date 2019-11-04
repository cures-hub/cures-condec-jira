package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventDescriptionEdited extends TestSetUpEventListener {

	private DecisionKnowledgeElement getFirstKnowledgeElementInDescription(String description) {
		jiraIssue.setDescription("TODO: Write description for this JIRA issue");
		IssueEvent issueEvent = createIssueEvent((Comment) null, EventType.ISSUE_UPDATED_ID);
		listener.onIssueEvent(issueEvent);
		jiraIssue.setDescription(description);

		List<DecisionKnowledgeElement> partsOfText = JiraIssueTextPersistenceManager.updateDescription(jiraIssue);
		if (partsOfText.size() == 0) {
			return null;
		}
		return partsOfText.get(0);
	}

	@Test
	@NonTransactional
	public void testNoKnowledgeElementContained() {
		DecisionKnowledgeElement element = getFirstKnowledgeElementInDescription("");
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		DecisionKnowledgeElement element = getFirstKnowledgeElementInDescription(
				"{issue}This is a very severe issue.{issue}");
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		DecisionKnowledgeElement element = getFirstKnowledgeElementInDescription("{code}public static class{code}");
		assertEquals("{code}public static class{code}", element.getDescription());
		assertTrue(element.getType() == KnowledgeType.OTHER);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		DecisionKnowledgeElement element = getFirstKnowledgeElementInDescription("(!)This is a very severe issue.");
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
		assertEquals("{issue}This is a very severe issue.{issue}",
				TextSplitter.parseIconsToTags(jiraIssue.getDescription()));
	}
}