package de.uhd.ifi.se.decision.management.jira.rest.configrest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNotesCategory;

public class TestSetReleaseNoteMapping extends TestConfigSuper {

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
		assertEquals(Status.ACCEPTED,
				configRest.setReleaseNoteMapping(request, projectKey, category, selectedIssueNames).getEntity());
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