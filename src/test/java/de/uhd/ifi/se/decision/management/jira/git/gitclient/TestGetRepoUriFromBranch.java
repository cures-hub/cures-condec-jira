package de.uhd.ifi.se.decision.management.jira.git.gitclient;

import static org.junit.Assert.assertEquals;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.model.Diff;

public class TestGetRepoUriFromBranch extends TestSetUpGit {

	@Test
	public void testBranchExisting() {
		Diff featureBranch = gitClient.getDiffForBranchWithName("TEST-4.feature.branch");
		assertEquals(URLEncoder.encode(GIT_URI, Charset.defaultCharset()), featureBranch.get(0).getRepoUri());
	}
}
