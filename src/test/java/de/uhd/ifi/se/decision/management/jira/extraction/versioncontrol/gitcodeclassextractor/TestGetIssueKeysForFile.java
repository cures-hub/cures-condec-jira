package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import net.java.ao.test.jdbc.NonTransactional;

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
		assertEquals(list, extract.getIssuesKeysForFile(extract.getCodeClassFiles().get(0)));
	}
}
