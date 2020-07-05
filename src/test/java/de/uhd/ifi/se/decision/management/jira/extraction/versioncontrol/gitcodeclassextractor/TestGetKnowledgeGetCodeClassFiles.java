package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestGetKnowledgeGetCodeClassFiles extends TestSetUpGit {

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
		assertEquals(6, extract.getCodeClassListFull().size());
	}
}
