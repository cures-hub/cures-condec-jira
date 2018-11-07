package de.uhd.ifi.se.decision.management.jira.extraction.connector;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.TestComment;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class)
public class TestViewConnector extends TestSetUpWithIssues {

	private EntityManager entityManager;

	private MutableIssue issue;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());

		createLocalIssue();
	}

	private void createLocalIssue() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		issue = new MockIssue(30, "TEST-" + 30);
		((MockIssue) issue).setProjectId(project.getId());
		issue.setProjectObject(project);
		IssueType issueType = new MockIssueType(1, KnowledgeType.DECISION.toString().toLowerCase(Locale.ENGLISH));
		issue.setIssueType(issueType);
		issue.setSummary("Test");
	}

	private void addCommentsToIssue() {
		// Get the current logged in user
		ApplicationUser currentUser = ComponentAccessor.getUserManager().getUserByName("NoFails");
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		String comment = "This is a testentence without any purpose. We expect this to be irrelevant. I got a problem in this class. The previous sentence should be much more relevant";
		commentManager.create(issue, currentUser, comment, true);
	}

	@Test
	@NonTransactional
	public void testConstructor() {
		ViewConnector vc = new ViewConnector(issue);
		assertNotNull(vc);
	}

	@Test
	@NonTransactional
	public void testConstructorWithComments() {
		addCommentsToIssue();

		ViewConnector vc = new ViewConnector(issue, true);
		assertNotNull(vc);
	}

	@Test
	@NonTransactional
	public void testGetAllCommentIds() {
		addCommentsToIssue();

		ViewConnector vc = new ViewConnector(issue, true);
		assertNotNull(vc.getAllCommentsIDs());
	}

	@Test
	@NonTransactional
	public void testGetAllCommentAuthorNames() {
		addCommentsToIssue();

		ViewConnector vc = new ViewConnector(issue, true);
		assertNotNull(vc.getAllCommentsAuthorNames());
	}

	@Test
	@NonTransactional
	public void testGetAllCommentsDates() {
		addCommentsToIssue();

		ViewConnector vc = new ViewConnector(issue, true);
		assertNotNull(vc.getAllCommentsDates());
	}

	@Test
	@NonTransactional
	public void testGetStyle() {
		addCommentsToIssue();

		ViewConnector vc = new ViewConnector(issue, true);
		assertNotNull(vc.getSentenceStyles());
	}

}
