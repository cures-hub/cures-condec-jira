package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGitCodeClassExtractor extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetCodeClasses() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(7, extract.getCodeClasses().size());
	}

	@Test
	@NonTransactional
	public void testGetGitClient() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getGitClient());
	}
}
