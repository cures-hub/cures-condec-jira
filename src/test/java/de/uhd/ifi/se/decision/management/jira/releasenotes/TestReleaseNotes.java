package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestReleaseNotes {

	private ReleaseNotes releaseNotes;

	@Before
	public void setUp() {
		releaseNotes = new ReleaseNotes();
	}

	@Test
	public void testId() {
		long id = 42;
		releaseNotes.setId(id);
		assertEquals(id, releaseNotes.getId());
	}

	@Test
	public void testTitle() {
		String title = "version 0.42 Awesome release!";
		releaseNotes.setTitle(title);
		assertEquals(title, releaseNotes.getTitle());
	}

	@Test
	public void testProject() {
		String projectKey = "TEST";
		releaseNotes.setProjectKey(projectKey);
		assertEquals(projectKey, releaseNotes.getProjectKey());
	}

	@Test
	public void testContent() {
		String content = "<h1>verision</h2>&/=)(09(=)(=)&%kjhkjhaksdjlklöajlaksdfalsdj";
		releaseNotes.setContent(content);
		assertEquals(content, releaseNotes.getContent());
	}

	@Test
	public void testEndDate() {
		String endDate = "2042-01-23";
		releaseNotes.setEndDate(endDate);
		assertEquals(endDate, releaseNotes.getEndDate());
	}

	@Test
	public void testStartDate() {
		String startDate = "1970-01-01";
		releaseNotes.setStartDate(startDate);
		assertEquals(startDate, releaseNotes.getStartDate());
	}
}