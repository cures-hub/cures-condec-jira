package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteConfigurationImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNoteRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestGetProposedIssues extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNoteRest releaseNoteRest;
	private String projectKey;
	private ReleaseNoteConfiguration releaseNoteConfiguration;
	private Issue issue;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNoteRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		releaseNoteConfiguration = new ReleaseNoteConfigurationImpl();
		SimpleDateFormat now = new SimpleDateFormat();
		releaseNoteConfiguration.setStartDate(now.toString());
		releaseNoteConfiguration.setEndDate(now.toString());
		List<Integer> issueKeyList = new ArrayList<Integer>();
		issueKeyList.add(0);
		issueKeyList.add(2);
		issueKeyList.add(3);
		releaseNoteConfiguration.setBugFixMapping(issueKeyList);
		releaseNoteConfiguration.setFeatureMapping(issueKeyList);
		releaseNoteConfiguration.setImprovementMapping(issueKeyList);
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		addCommentsToIssue();
		fillSentenceList();
	}

	private void addCommentsToIssue() {
		// Get the current logged in user
		ApplicationUser currentUser = JiraUsers.SYS_ADMIN.getApplicationUser();
		// Get access to the Jira comment and component manager
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		// Get the last comment entered in on the issue to a String
		String comment = "This is a testentence without any purpose. We expect this to be irrelevant. I got a problem in this class. The previous sentence should be much more relevant";
		commentManager.create(issue, currentUser, comment, true);
	}

	private void fillSentenceList() {
		Comment comment = ComponentAccessor.getCommentManager().getLastComment(issue);
		JiraIssueTextPersistenceManager.getPartsOfComment(comment);
	}

	@Test
	public void testGetProposedIssues() {
		assertEquals(Response.Status.OK.getStatusCode(), releaseNoteRest.getProposedIssues(request, projectKey, releaseNoteConfiguration).getStatus());
	}

}
