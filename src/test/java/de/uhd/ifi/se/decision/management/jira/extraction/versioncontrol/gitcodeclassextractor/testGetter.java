package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;

// TODO Start with capital letter
public class testGetter extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetNumberOfCodeClasses() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(6, extract.getCodeClasses().size());
	}

	@Test
	@NonTransactional
	public void testGetCodeClassListFull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(6, extract.getCodeClasses().size());
	}

	@Test
	@NonTransactional
	public void testGetGitClient() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getGitClient());
	}
}
