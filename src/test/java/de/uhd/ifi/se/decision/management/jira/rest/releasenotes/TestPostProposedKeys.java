package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private ReleaseNotesRest releaseNotesRest;
	private String projectKey;
	private Map<String, Map<String, List<String>>> postObject;

	@Before
	public void setUp() {
		releaseNotesRest = new ReleaseNotesRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		Map<String, List<String>> keysForContent = new HashMap<>();
		keysForContent.put(ReleaseNotesCategory.BUG_FIXES.toString(), new ArrayList<>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNotesCategory.NEW_FEATURES.toString(),
				new ArrayList<>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNotesCategory.IMPROVEMENTS.toString(),
				new ArrayList<>(List.of("TEST-14", "TEST-30")));
		postObject = new HashMap<>();
		postObject.put("selectedKeys", keysForContent);
		HashMap<String, List<String>> title = new HashMap<String, List<String>>();
		List<String> titleArray = new ArrayList<String>();
		titleArray.add("some great title");
		title.put("id", titleArray);
		postObject.put("title", title);
		Map<String, List<String>> additionalConfiguration = new HashMap<>();
		List<String> additionalConfigurationArray = AdditionalConfigurationOptions.toList();
		additionalConfiguration.put("id", additionalConfigurationArray);
		postObject.put("additionalConfiguration", additionalConfiguration);
	}

	@Test
	public void testPostProposedKeys() {
		assertEquals(Response.Status.OK.getStatusCode(),
				releaseNotesRest.postProposedKeys(request, projectKey, postObject).getStatus());
	}

}
