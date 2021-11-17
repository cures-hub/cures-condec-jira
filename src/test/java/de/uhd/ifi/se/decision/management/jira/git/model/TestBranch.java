package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBranch extends TestSetUpGit {

	private Branch branch;
	private Ref ref;

	@Before
	public void setUp() {
		super.setUp();
		DecisionKnowledgeElementInCodeComment codeElement = new DecisionKnowledgeElementInCodeComment();
		codeElement.setSummary("I am an issue captured in a code comment.");
		codeElement.setType(KnowledgeType.ISSUE);
		codeElement.setProject("TEST");
		List<DecisionKnowledgeElementInCodeComment> codeElements = List.of(codeElement);

		DecisionKnowledgeElementInCommitMessage commitMessageElement = new DecisionKnowledgeElementInCommitMessage();
		commitMessageElement.setSummary("I am an issue");
		commitMessageElement.setType(KnowledgeType.ISSUE);
		commitMessageElement.setProject("TEST");
		List<DecisionKnowledgeElementInCommitMessage> commitMessageElements = List.of(commitMessageElement);

		ref = gitClient.getRefs().get(0);

		branch = new Branch(ref, codeElements, commitMessageElements);
	}

	@Test
	public void testGetBranchName() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", branch.getName());

		branch.setRef(null);
		assertNull(branch.getName());
	}

	@Test
	public void testGetId() {
		assertEquals(ref.getObjectId().getName(), branch.getId());

		branch.setRef(null);
		assertNull(branch.getId());
	}

	@Test
	public void testRepoUri() {
		branch.setRepoUri(GIT_URI);
		assertEquals(URLEncoder.encode(GIT_URI, Charset.defaultCharset()), branch.getRepoUri());
	}

	@Test
	public void testGetCodeElements() {
		assertEquals(1, branch.getCodeElements().size());
	}

	@Test
	public void testGetCommitMessageElements() {
		assertEquals(1, branch.getCommitElements().size());
	}
}
