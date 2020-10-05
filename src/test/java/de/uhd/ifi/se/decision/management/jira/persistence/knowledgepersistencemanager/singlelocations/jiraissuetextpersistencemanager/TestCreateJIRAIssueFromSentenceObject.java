package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateJIRAIssueFromSentenceObject extends TestSetUp {

	protected static JiraIssueTextPersistenceManager manager;
	protected static ApplicationUser user;

	@Before
	public void setUp() {
		init();
		manager = new JiraIssueTextPersistenceManager("TEST");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testIdLessUserNull() {
		assertNull(manager.createJiraIssueFromSentenceObject(-1, null));
	}

	@Test
	@NonTransactional
	public void testIdZeroUserNull() {
		assertNull(manager.createJiraIssueFromSentenceObject(0, null));
	}

	@Test
	@NonTransactional
	public void testIdOkUserNull() {
		assertNull(manager.createJiraIssueFromSentenceObject(1, null));
	}

	@Test
	@NonTransactional
	public void testIdLessUserFilled() {
		assertNull(manager.createJiraIssueFromSentenceObject(-1, user));
	}

	@Test
	@NonTransactional
	public void testIdZeroUserFilled() {
		assertNull(manager.createJiraIssueFromSentenceObject(0, user));
	}

	@Test
	@NonTransactional
	public void testIdOkUserFilled() {
		List<PartOfJiraIssueText> comment = JiraIssues.getSentencesForCommentText(
				"some sentence in front. {issue} testobject {issue} some sentence in the back.");
		manager.insertKnowledgeElement(comment.get(1), null);
		assertNotNull(manager.createJiraIssueFromSentenceObject(3, user));
	}
}
