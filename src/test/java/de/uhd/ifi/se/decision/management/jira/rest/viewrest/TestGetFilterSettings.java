package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;

public class TestGetFilterSettings extends TestSetUp {

	private ViewRest viewRest;
	protected HttpServletRequest request;

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		init();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testRequestNullSearchTermNullElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getFilterSettings(null, null, null).getStatus());
	}

	@Test
	public void testRequestFilledSearchTermEmptyElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "", "TEST-1");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettings filterSettings = (FilterSettings) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		assertEquals(5, filterSettings.getAllJiraIssueTypes().size());
		assertEquals(5, filterSettings.getNamesOfSelectedJiraIssueTypes().size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}

	@Test
	public void testRequestFilledSearchTermJqlElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?jql=issuetype%20%3D%20Issue",
				"TEST-1");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettingsImpl filterSettings = (FilterSettingsImpl) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		List<String> issueTypesMatchingFilter = filterSettings.getNamesOfSelectedJiraIssueTypes();
		assertEquals("Issue", issueTypesMatchingFilter.get(0));
		assertEquals(1, issueTypesMatchingFilter.size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}

	@Test
	public void testRequestFilledSearchTermPresetJiraFilterElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "?filter=allopenissues", "TEST-12");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettingsImpl filterSettings = (FilterSettingsImpl) filterSettingsResponse.getEntity();
		assertEquals(5, filterSettings.getNamesOfDocumentationLocations().size());
		assertEquals(-1, filterSettings.getCreatedEarliest());
		assertEquals(-1, filterSettings.getCreatedLatest());
	}
}
