package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class TestGetIssueKeysForFile extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testGetIssueKeysForFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNull(extract.getIssuesKeysForFile(null));
	}

	@Test
	@NonTransactional
	public void testGetIssueKeysForFile() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");

		List<String> list = new ArrayList<>();
		list.add("TEST-12");
		assertFalse(extract.getGitClient().getRemoteUris().isEmpty());
		assertFalse(extract.getCodeClassListFull().isEmpty());
		assertEquals(list, extract.getIssuesKeysForFile(extract.getCodeClassListFull().get(0)));
	}
}
