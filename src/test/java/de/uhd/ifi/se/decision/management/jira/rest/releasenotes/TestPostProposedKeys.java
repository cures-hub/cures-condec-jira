package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNotesRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestPostProposedKeys extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNotesRest releaseNoteRest;
	private String projectKey;
	private HashMap<String, List<String>> keysForContent;
	private HashMap<String, HashMap<String, List<String>>> postObject;

	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		keysForContent = new HashMap<String, List<String>>();
		keysForContent.put(ReleaseNotesCategory.BUG_FIXES.toString(),
				new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNotesCategory.NEW_FEATURES.toString(),
				new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNotesCategory.IMPROVEMENTS.toString(),
				new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		postObject = new HashMap<String, HashMap<String, List<String>>>();
		postObject.put("selectedKeys", keysForContent);
		HashMap<String, List<String>> title = new HashMap<String, List<String>>();
		List<String> titleArray = new ArrayList<String>();
		titleArray.add("some great title");
		title.put("id", titleArray);
		postObject.put("title", title);
		HashMap<String, List<String>> additionalConfiguration = new HashMap<String, List<String>>();
		List<String> additionalConfigurationArray = AdditionalConfigurationOptions.toList();
		additionalConfiguration.put("id", additionalConfigurationArray);
		postObject.put("additionalConfiguration", additionalConfiguration);
	}

	@Test
	public void testPostProposedKeys() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNoteRest.postProposedKeys(request, projectKey, postObject).getStatus());
	}

}
