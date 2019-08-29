package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteConfiguration;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteConfigurationImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNoteRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestGetProposedIssues extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNoteRest releaseNoteRest;
	private String projectKey;
	private ReleaseNoteConfiguration releaseNoteConfiguration;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNoteRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		releaseNoteConfiguration = new ReleaseNoteConfigurationImpl();
		SimpleDateFormat now= new SimpleDateFormat();
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
	}

	@Test
	public void testGetProposedIssues() {
		assertEquals(Response.Status.OK.getStatusCode(), releaseNoteRest.getProposedIssues(request,projectKey,releaseNoteConfiguration).getStatus());
	}
}
