package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesConfiguration;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestProposeElements extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNoteRest;
	private String projectKey;
	private ReleaseNotesConfiguration releaseNoteConfiguration;
	private Issue issue;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		releaseNoteConfiguration = new ReleaseNotesConfiguration();
		SimpleDateFormat now = new SimpleDateFormat();
		releaseNoteConfiguration.setStartDate(now.toString());
		releaseNoteConfiguration.setEndDate(now.toString());
		List<Integer> issueKeyList = new ArrayList<>();
		issueKeyList.add(0);
		issueKeyList.add(2);
		issueKeyList.add(3);
		releaseNoteConfiguration.setJiraIssueTypesForBugFixes(List.of("Bug"));
		releaseNoteConfiguration.setJiraIssueTypesForNewFeatures(List.of("Non functional requirement"));
		releaseNoteConfiguration.setJiraIssueTypesForImprovements(List.of("Task"));
		releaseNoteConfiguration.setAdditionalConfiguration(toBooleanList(true));
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		issue = ComponentAccessor.getIssueManager().getIssueObject("TEST-30");
		addCommentsToIssue();
		fillSentenceList();
	}

	/**
	 * @param value
	 * @return hashMap of AdditionalConfigurationOptions with integer and boolean
	 *         value.
	 */
	public static EnumMap<AdditionalConfigurationOptions, Boolean> toBooleanList(Boolean value) {
		EnumMap<AdditionalConfigurationOptions, Boolean> configurationTypes = new EnumMap<>(
				AdditionalConfigurationOptions.class);
		for (AdditionalConfigurationOptions criteriaType : AdditionalConfigurationOptions.values()) {
			configurationTypes.put(criteriaType, value);
		}
		return configurationTypes;
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
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance("TEST")
				.getJiraIssueTextManager();
		persistenceManager.updateElementsOfCommentInDatabase(comment);
	}

	@Test
	public void testGetProposedIssues() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.proposeElements(request, projectKey, releaseNoteConfiguration).getStatus());
	}

	@Test
	public void testProjectUnknown() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				releaseNoteRest.proposeElements(request, "UNKNOWNPROJECT", releaseNoteConfiguration).getStatus());
	}

}
