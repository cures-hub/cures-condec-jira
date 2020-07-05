package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class testGetter extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetNumberOfCodeClasses() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(6, extract.getNumberOfCodeClasses());
	}

	@Test
	@NonTransactional
	public void testGetCodeClassListFull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(6, extract.getCodeClassListFull().size());
	}

	@Test
	@NonTransactional
	public void testGetCodeClassOriginMap() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClassOriginMap());
	}

	@Test
	@NonTransactional
	public void testGetGitClient() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getGitClient());
	}
}
