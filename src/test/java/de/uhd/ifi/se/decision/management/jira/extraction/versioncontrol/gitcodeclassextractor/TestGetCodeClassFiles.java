package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetCodeClassFiles extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetCodeClassFilesProjectKeyNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor(null);
		assertNull(extract.getCodeClassListFull());
	}

	@Test
	@NonTransactional
	public void testGetCodeClassFiles() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(extract.getCodeClassListFull().size(), 6);
	}
}
