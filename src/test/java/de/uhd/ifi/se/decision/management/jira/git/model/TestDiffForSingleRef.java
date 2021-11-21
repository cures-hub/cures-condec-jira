package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestDiffForSingleRef extends TestSetUpGit {

	private DiffForSingleRef diffForSingleRef;
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

		ref = gitClient.getDiffForFeatureBranchWithName("TEST-4").getRefs().get(0);

		diffForSingleRef = new DiffForSingleRef(ref, codeElements, commitMessageElements);
	}

	@Test
	public void testGetBranchName() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", diffForSingleRef.getName());

		diffForSingleRef.setRef(null);
		assertNull(diffForSingleRef.getName());
	}

	@Test
	public void testGetId() {
		assertEquals(ref.getObjectId().getName(), diffForSingleRef.getId());

		diffForSingleRef.setRef(null);
		assertNull(diffForSingleRef.getId());
	}

	@Test
	public void testRepoUri() {
		diffForSingleRef.setRepoUri(GIT_URI);
		assertEquals(URLEncoder.encode(GIT_URI, Charset.defaultCharset()), diffForSingleRef.getRepoUri());
	}

	@Test
	public void testGetCodeElements() {
		assertEquals(1, diffForSingleRef.getCodeElements().size());
	}

	@Test
	public void testGetCommitMessageElements() {
		assertEquals(1, diffForSingleRef.getCommitElements().size());
	}

	@Test
	public void testGetQualityProblems() {
		assertEquals(0, diffForSingleRef.getQualityProblems().size());
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(diffForSingleRef.getName(), diffForSingleRef.getId()), diffForSingleRef.hashCode());
	}
}