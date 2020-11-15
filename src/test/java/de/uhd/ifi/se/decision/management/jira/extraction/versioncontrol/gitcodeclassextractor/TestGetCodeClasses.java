package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetCodeClasses extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetCodeClassesProjectKeyNull() {
		GitCodeClassExtractor extractor = new GitCodeClassExtractor((String) null);
		assertTrue(extractor.getCodeClasses().isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetCodeClasses() {
		GitCodeClassExtractor extractor = new GitCodeClassExtractor("TEST");
		assertEquals(6, extractor.getCodeClasses().size());
		ChangedFile file = extractor.getCodeClasses().get(1);
		assertEquals("GodClass.java", file.getSummary());
		assertEquals(1, file.getJiraIssueKeys().size());
	}
}
