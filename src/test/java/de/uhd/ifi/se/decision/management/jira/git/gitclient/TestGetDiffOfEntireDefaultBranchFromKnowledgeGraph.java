package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetDiffOfEntireDefaultBranchFromKnowledgeGraph extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testKnowledgeGraphContainsCodeElements() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").extractAllChangedFiles(diff);

		diff = gitClient.getDiffOfEntireDefaultBranchFromKnowledgeGraph();
		assertTrue(diff.get(0).getCodeElements().size() > 0);

		// List<RevCommit> allCommits = diff.getCommits();
		// assertEquals(6, allCommits.size());

		// assertEquals(5, diff.getChangedFiles().size());
		// ChangedFile extractedClass = diff.getChangedFiles().get(2);
		// assertEquals("Tangled2.java", extractedClass.getName());
		// assertEquals(1, extractedClass.getCommits().size());
		// assertEquals("TEST-30", extractedClass.getJiraIssueKeys().iterator().next());
	}
}