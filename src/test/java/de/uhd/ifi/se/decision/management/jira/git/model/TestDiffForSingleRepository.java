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

public class TestDiffForSingleRepository extends TestSetUpGit {

	private DiffForSingleRef diffForSingleRepo;
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

		diffForSingleRepo = new DiffForSingleRef(ref, codeElements, commitMessageElements);
	}

	@Test
	public void testGetBranchName() {
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", diffForSingleRepo.getName());

		diffForSingleRepo.setRef(null);
		assertNull(diffForSingleRepo.getName());
	}

	@Test
	public void testGetId() {
		assertEquals(ref.getObjectId().getName(), diffForSingleRepo.getId());

		diffForSingleRepo.setRef(null);
		assertNull(diffForSingleRepo.getId());
	}

	@Test
	public void testRepoUri() {
		diffForSingleRepo.setRepoUri(GIT_URI);
		assertEquals(URLEncoder.encode(GIT_URI, Charset.defaultCharset()), diffForSingleRepo.getRepoUri());
	}

	@Test
	public void testGetCodeElements() {
		assertEquals(1, diffForSingleRepo.getCodeElements().size());
	}

	@Test
	public void testGetCommitMessageElements() {
		assertEquals(1, diffForSingleRepo.getCommitElements().size());
	}

	@Test
	public void testGetQualityProblems() {
		assertEquals(0, diffForSingleRepo.getQualityProblems().size());
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(diffForSingleRepo.getName(), diffForSingleRepo.getId()), diffForSingleRepo.hashCode());
	}
}