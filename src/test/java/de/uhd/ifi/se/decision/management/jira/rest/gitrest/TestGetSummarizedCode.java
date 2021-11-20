package de.uhd.ifi.se.decision.management.jira.rest.gitrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.GitRest;

public class TestGetSummarizedCode extends TestSetUpGit {

	private GitRest gitRest;

	@Override
	@Before
	public void setUp() {
		gitRest = new GitRest();
		super.setUp();
	}

	@Test
	public void testFilterSettingsValid() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setActivated(true);
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setSelectedElement("TEST-14");
		assertEquals(Status.OK.getStatusCode(), gitRest.getSummarizedCode(filterSettings, 0).getStatus());
	}

	@Test
	public void testFilterSettingsInvalid() {
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getSummarizedCode(filterSettings, 0).getStatus());
	}

	@Test
	public void testFilterSettingsNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), gitRest.getSummarizedCode(null, 0).getStatus());
	}
}