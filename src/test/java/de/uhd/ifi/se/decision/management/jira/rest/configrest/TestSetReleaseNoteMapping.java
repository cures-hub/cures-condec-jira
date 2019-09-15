package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static com.atlassian.httpclient.api.HttpStatus.ACCEPTED;
import static org.junit.Assert.assertEquals;

public class TestSetReleaseNoteMapping extends TestConfigSuper {

	@Test
	public void testSetReleaseNoteMappingWithEmptyValues() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),configRest.setReleaseNoteMapping(request, null,null,null).getStatus());
	}
	@Test
	public void testSetReleaseNoteMappingWithValidValues() {
		String projectKey= "TEST";
		ReleaseNoteCategory category=ReleaseNoteCategory.NEW_FEATURES;
		List<String> selectedIssueNames=new ArrayList<>();
		selectedIssueNames.add("firstName");
		selectedIssueNames.add("secondName");
		assertEquals(Response.Status.ACCEPTED,configRest.setReleaseNoteMapping(request, projectKey,category,selectedIssueNames).getEntity());
	}
	@Test
	public void testGetReleaseNoteMappingWithEmptyValues() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),configRest.getReleaseNoteMapping(request, null).getStatus());
	}
	@Test
	public void testGetReleaseNoteMappingWithValidValues() {
		String projectKey= "TEST";
		assertEquals(Response.Status.OK.getStatusCode(),configRest.getReleaseNoteMapping(request, projectKey).getStatus());
	}

}
