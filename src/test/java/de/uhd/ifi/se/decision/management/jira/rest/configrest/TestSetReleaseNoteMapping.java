package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;
import de.uhd.ifi.se.decision.management.jira.rest.ConfigRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestSetReleaseNoteMapping extends TestSetUp {

	private HttpServletRequest request;
	private ConfigRest configRest;

	@Before
	public void setUp() {
		init();
		configRest = new ConfigRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testSetReleaseNoteMappingWithEmptyValues() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				configRest.setReleaseNoteMapping(request, null, null, null).getStatus());
	}

	@Test
	public void testSetReleaseNoteMappingWithValidValues() {
		String projectKey = "TEST";
		ReleaseNotesCategory category = ReleaseNotesCategory.NEW_FEATURES;
		List<String> selectedIssueNames = new ArrayList<>();
		selectedIssueNames.add("firstName");
		selectedIssueNames.add("secondName");
		assertEquals(Status.OK.getStatusCode(),
				configRest.setReleaseNoteMapping(request, projectKey, category, selectedIssueNames).getStatus());
	}

	@Test
	public void testGetReleaseNoteMappingWithEmptyValues() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), configRest.getReleaseNoteMapping(null).getStatus());
	}

	@Test
	public void testGetReleaseNoteMappingWithValidValues() {
		String projectKey = "TEST";
		assertEquals(Status.OK.getStatusCode(), configRest.getReleaseNoteMapping(projectKey).getStatus());
	}
}