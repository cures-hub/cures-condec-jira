package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
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
	public void testRequestNullSearchTermEmptyElementEmpty() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), viewRest.getFilterSettings(null, "", "").getStatus());
	}

	@Test
	public void testRequestFilledSearchTermEmptyElementNull() {
		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),
				viewRest.getFilterSettings(request, null, "").getStatus());
	}

	@Test
	public void testRequestFilledSearchTermEmptyElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "TEST", "");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettings filterSettings = (FilterSettings) filterSettingsResponse.getEntity();
		assertEquals(3, filterSettings.getNamesOfDocumentationLocations().size());
		assertTrue(filterSettings.getKnowledgeTypes().size() > 0);
		assertEquals(-1, filterSettings.getStartDate());
		assertEquals(-1, filterSettings.getEndDate());
	}

	@Test
	public void testRequestFilledSearchTermJqlElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "TEST", "?jql=issuetype%20%3D%20Issue");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettings filterSettings = (FilterSettings) filterSettingsResponse.getEntity();
		Set<String> issueTypesMatchingFilter = filterSettings.getKnowledgeTypes();
		assertEquals("Issue", issueTypesMatchingFilter.iterator().next());
		assertEquals(1, issueTypesMatchingFilter.size());
		assertEquals(-1, filterSettings.getStartDate());
		assertEquals(-1, filterSettings.getEndDate());
	}

	@Test
	public void testRequestFilledSearchTermPresetJiraFilterElementExistent() {
		Response filterSettingsResponse = viewRest.getFilterSettings(request, "TEST", "?filter=allopenissues");
		assertEquals(Response.Status.OK.getStatusCode(), filterSettingsResponse.getStatus());

		FilterSettings filterSettings = (FilterSettings) filterSettingsResponse.getEntity();
		assertEquals(-1, filterSettings.getStartDate());
		assertEquals(-1, filterSettings.getEndDate());
	}
}
