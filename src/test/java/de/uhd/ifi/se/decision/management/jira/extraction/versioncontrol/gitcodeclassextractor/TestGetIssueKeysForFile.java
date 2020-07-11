package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetIssueKeysForFile extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetIssueKeysForFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertTrue(extract.getJiraIssueKeysForFile((ChangedFile) null).isEmpty());
	}

	@Test
	@NonTransactional
	@Ignore
	public void testGetIssueKeysForFile() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");

		List<String> list = new ArrayList<>();
		list.add("TEST-12");
		assertFalse(extract.getGitClient().getRemoteUris().isEmpty());
		assertFalse(extract.getCodeClasses().isEmpty());
		assertEquals(list, extract.getJiraIssueKeysForFile(extract.getCodeClasses().get(0)));
	}
}
