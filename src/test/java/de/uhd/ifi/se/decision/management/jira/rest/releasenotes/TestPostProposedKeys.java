package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNoteRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory.*;
import static org.junit.Assert.assertEquals;

public class TestPostProposedKeys extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNoteRest releaseNoteRest;
	private String projectKey;
	private HashMap<String, ArrayList<String>> keysForContent;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNoteRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		keysForContent = new HashMap<String,ArrayList<String>>();
		keysForContent.put(BUG_FIXES.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(NEW_FEATURES.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(IMPROVEMENTS.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));

	}


	@Test
	public void testPostProposedKeys() {
		assertEquals(Response.Status.OK.getStatusCode(),releaseNoteRest.postProposedKeys(request, projectKey, keysForContent).getStatus());
	}

}
