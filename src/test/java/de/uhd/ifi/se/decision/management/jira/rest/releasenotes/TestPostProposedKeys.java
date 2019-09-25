package de.uhd.ifi.se.decision.management.jira.rest.releasenotes;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.AdditionalConfigurationOptions;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;
import de.uhd.ifi.se.decision.management.jira.rest.ReleaseNoteRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPostProposedKeys extends TestSetUp {
	protected HttpServletRequest request;
	private ReleaseNoteRest releaseNoteRest;
	private String projectKey;
	private HashMap<String, ArrayList<String>> keysForContent;
	private HashMap<String,HashMap<String,ArrayList<String>>> postObject;


	@Before
	public void setUp() {
		releaseNoteRest = new ReleaseNoteRest();
		init();
		request = new MockHttpServletRequest();
		projectKey = "TEST";
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		request.setAttribute("user", user);
		keysForContent = new HashMap<String,ArrayList<String>>();
		keysForContent.put(ReleaseNoteCategory.BUG_FIXES.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNoteCategory.NEW_FEATURES.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		keysForContent.put(ReleaseNoteCategory.IMPROVEMENTS.toString(), new ArrayList<String>(List.of("TEST-14", "TEST-30")));
		postObject = new HashMap<String,HashMap<String,ArrayList<String>>>();
		postObject.put("selectedKeys",keysForContent);
		HashMap<String, ArrayList<String>> title= new HashMap<String,ArrayList<String>>();
		ArrayList<String> titleArray= new ArrayList<String>();
		titleArray.add("some great title");
		title.put("id",titleArray);
		postObject.put("title",title);
		HashMap<String, ArrayList<String>> additionalConfiguration= new HashMap<String,ArrayList<String>>();
		ArrayList<String> additionalConfigurationArray= AdditionalConfigurationOptions.toList();
		additionalConfiguration.put("id",additionalConfigurationArray);
		postObject.put("additionalConfiguration",additionalConfiguration);
	}


	@Test
	public void testPostProposedKeys() {
		assertEquals(Response.Status.OK.getStatusCode(),releaseNoteRest.postProposedKeys(request, projectKey, postObject).getStatus());
	}

}
