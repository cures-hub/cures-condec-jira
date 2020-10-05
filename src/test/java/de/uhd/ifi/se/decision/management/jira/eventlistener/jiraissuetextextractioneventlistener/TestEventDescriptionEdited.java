package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.JiraIssueTextParser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestEventDescriptionEdited extends TestSetUpEventListener {

	private KnowledgeElement getFirstKnowledgeElementInDescription(String description) {
		jiraIssue.setDescription("TODO: Write description for this Jira issue");
		IssueEvent issueEvent = createIssueEvent((Comment) null, EventType.ISSUE_UPDATED_ID);
		listener.onIssueEvent(issueEvent);
		jiraIssue.setDescription(description);

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();

		List<KnowledgeElement> partsOfText = persistenceManager.updateDescription(jiraIssue);
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
		assertEquals("This is a very severe issue.", element.getDescription());
		assertEquals(KnowledgeType.ISSUE, element.getType());
	}

	@Test
	@NonTransactional
	public void testExcludedTag() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("{code:java}public static class{code}");
		assertEquals("{code:java}public static class{code}", element.getDescription());
		assertEquals(KnowledgeType.OTHER, element.getType());
	}

	@Test
	@NonTransactional
	public void testRationaleIcon() {
		KnowledgeElement element = getFirstKnowledgeElementInDescription("(!)This is a very severe issue.");
		assertEquals("This is a very severe issue.", element.getDescription());
		assertEquals(KnowledgeType.ISSUE, element.getType());
		assertEquals("{issue}This is a very severe issue.{issue}",
				JiraIssueTextParser.parseIconsToTags(jiraIssue.getDescription()));
	}
}