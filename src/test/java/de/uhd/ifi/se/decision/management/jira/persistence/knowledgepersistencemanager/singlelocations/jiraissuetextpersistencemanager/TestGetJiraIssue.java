package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetJiraIssue extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static DecisionKnowledgeElement element;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testGetJiraIssueKeyForPartOfText() {
		List<PartOfJiraIssueText> comment = TestTextSplitter.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		long id = manager.insertDecisionKnowledgeElement(comment.get(1), null);

		assertEquals(3, id);

		long jiraIssueId = comment.get(0).getJiraIssueId();

		List<DecisionKnowledgeElement> listWithObjects = JiraIssueTextPersistenceManager
				.getElementsForIssue(jiraIssueId, "TEST");
		assertEquals(3, listWithObjects.size());

		String jiraIssueKey = JiraIssueTextPersistenceManager.getJiraIssue(id).getKey();

		assertEquals("TEST-30", jiraIssueKey);
	}
}
