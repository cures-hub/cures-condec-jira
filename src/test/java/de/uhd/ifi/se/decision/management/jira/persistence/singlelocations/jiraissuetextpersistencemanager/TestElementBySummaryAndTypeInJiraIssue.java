package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestElementBySummaryAndTypeInJiraIssue extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;
	protected static KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeNull() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue(null, -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeNull() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue("This is a comment for test purposes", -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeNull() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue(null, 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeNull() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue("This is a comment for test purposes", 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeFilled() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue(null, -1, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeFilled() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue("This is a comment for test purposes", -1,
				KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeFilled() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue(null, 0, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeFilled() {
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue("This is a comment for test purposes", 0,
				KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyWrongIssueIdOkTypeFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		manager.insertKnowledgeElement(comment.get(1), null);
		assertNull(manager.getElementBySummaryAndTypeInJiraIssue("Not the right Body",
				comment.get(0).getJiraIssue().getId(), KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdOkTypeFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		manager.insertKnowledgeElement(comment.get(1), null);
		assertEquals(3, manager.getElementBySummaryAndTypeInJiraIssue("testobject",
				comment.get(0).getJiraIssue().getId(), KnowledgeType.ISSUE).getId());
	}
}
