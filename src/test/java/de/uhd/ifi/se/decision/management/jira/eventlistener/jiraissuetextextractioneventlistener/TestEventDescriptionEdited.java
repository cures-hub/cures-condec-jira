package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventDescriptionEdited extends TestSetUpEventListener {

	private KnowledgeElement getFirstKnowledgeElementInDescription(String description) {
		jiraIssue.setDescription("TODO: Write description for this JIRA issue");
		IssueEvent issueEvent = createIssueEvent((Comment) null, EventType.ISSUE_UPDATED_ID);
		listener.onIssueEvent(issueEvent);
		jiraIssue.setDescription(description);

		List<KnowledgeElement> partsOfText = JiraIssueTextPersistenceManager.updateDescription(jiraIssue);
		if (partsOfText.size() == 0) {
			return null;
		}
		return partsOfText.get(0);
	}

	@Test
	@NonTransactional
	public void testNoKnowledgeElementContained() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("");
		assertNull(element);
	}

	@Test
	@NonTransactional
	public void testRationaleTag() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("{issue}This is a very severe issue.{issue}");
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("{code:java}public static class{code}");
		assertEquals("{code:java}public static class{code}", element.getDescription());
		assertTrue(element.getType() == KnowledgeType.OTHER);
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("(!)This is a very severe issue.");
		assertTrue(element.getDescription().equals("This is a very severe issue."));
		assertTrue(element.getType() == KnowledgeType.ISSUE);
		assertEquals("{issue}This is a very severe issue.{issue}",
				TextSplitter.parseIconsToTags(jiraIssue.getDescription()));
	}
}