package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestGetIssueKeysForFile extends TestSetUpGit {

	@Test
	public void testGetIssueKeysForFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNull(extract.getIssuesKeysForFile(null));
	}

	@Test
	public void testGetIssueKeysForFile() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		List<String> list = new ArrayList<>();
		list.add("TEST-12");
		assertEquals(list, extract.getIssuesKeysForFile(extract.getCodeClassFiles().get(0)));
	}
}
