package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertEquals;

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

public class TestGetIdOfElement extends TestSetUp {

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
	public void testBodyNullIssueIdLessTypeNullKeyNull() {
		assertEquals(0, manager.getElement(null, -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeNullKeyNull() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeNullKeyNull() {
		assertEquals(0, manager.getElement(null, 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeNullKeyNull() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeFilledKeyNull() {
		assertEquals(0, manager.getElement(null, -1, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeFilledKeyNull() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", -1, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeFilledKeyNull() {
		assertEquals(0, manager.getElement(null, 0, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeFilledKeyNull() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", 0, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeNullKeyFilled() {
		assertEquals(0, manager.getElement(null, -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeNullKeyFilled() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", -1, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeNullKeyFilled() {
		assertEquals(0, manager.getElement(null, 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeNullKeyFilled() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", 0, null));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdLessTypeFilledKeyFilled() {
		assertEquals(0, manager.getElement(null, -1, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdLessTypeFilledKeyFilled() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", -1, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyNullIssueIdZeroTypeFilledKeyFilled() {
		assertEquals(0, manager.getElement(null, 0, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdZeroTypeFilledKeyFilled() {
		assertEquals(0, manager.getElement("This is a comment for test purposes", 0, KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyWrongIssueIdOkTypeFilledKeyFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		manager.insertKnowledgeElement(comment.get(1), null);
		assertEquals(0, manager.getElement("Not the right Body", comment.get(0).getJiraIssue().getId(),
				KnowledgeType.ISSUE));
	}

	@Test
	@NonTransactional
	public void testBodyFilledIssueIdOkTypeFilledKeyFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		manager.insertKnowledgeElement(comment.get(1), null);
		assertEquals(3,
				manager.getElement("testobject", comment.get(0).getJiraIssue().getId(), KnowledgeType.ISSUE));
	}
}
