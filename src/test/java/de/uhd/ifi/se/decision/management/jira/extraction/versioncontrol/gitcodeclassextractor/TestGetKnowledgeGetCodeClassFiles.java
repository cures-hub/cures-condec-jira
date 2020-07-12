package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetKnowledgeGetCodeClassFiles extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetCodeClassFilesProjectKeyNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor(null);
		assertTrue(extract.getCodeClasses().isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetCodeClassFiles() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertEquals(7, extract.getCodeClasses().size());
	}
}
