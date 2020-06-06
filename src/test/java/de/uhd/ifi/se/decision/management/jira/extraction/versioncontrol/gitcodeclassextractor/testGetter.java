package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class testGetter extends TestSetUpGit {

	@Test
	public void testGetNumberOfCodeClasses() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals("6", extract.getNumberOfCodeClasses());
	}

	@Test
	public void testGetCodeClassListFull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(extract.getCodeClassListFull().size(), 6);
	}

	@Test
	public void testGetCodeClassOriginMap() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClassOriginMap());
	}

	@Test
	public void testGetGitClient() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getGitClient());
	}

}
