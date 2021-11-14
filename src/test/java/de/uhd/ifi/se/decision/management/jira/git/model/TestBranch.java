package de.uhd.ifi.se.decision.management.jira.git.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBranch extends TestSetUpGit {

	private Branch branchDiff;
	private Ref ref;
	private List<DecisionKnowledgeElementInCodeComment> rationaleInBranch;
	private DecisionKnowledgeElementInCodeComment rat1;

	@Before
	public void setUp() {
		super.setUp();
		rat1 = new DecisionKnowledgeElementInCodeComment();
		rat1.setSummary("I am an issue");
		rat1.setType(KnowledgeType.ISSUE);
		rat1.setProject("TEST");

		rationaleInBranch = new ArrayList<>();
		rationaleInBranch.add(rat1);
		ref = gitClient.getRefs().get(0);
	}

	@Test
	public void testConstructor() {
		branchDiff = new Branch(ref, rationaleInBranch, new ArrayList<>());
		assertNotNull(branchDiff);
		branchDiff = new Branch(ref, new ArrayList<>(), new ArrayList<>());
		assertNotNull(branchDiff);
		branchDiff = new Branch(null, rationaleInBranch, new ArrayList<>());
		assertNotNull(branchDiff);
	}

	@Test
	public void testGetBranchName() {
		branchDiff = new Branch(ref, rationaleInBranch, new ArrayList<>());
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", branchDiff.getName());
		branchDiff = new Branch(null, rationaleInBranch, new ArrayList<>());
		assertNull(branchDiff.getName());
	}

	@Test
	public void testGetElements() {
		branchDiff = new Branch(ref, rationaleInBranch, new ArrayList<>());
		List<DecisionKnowledgeElementInCodeComment> elements = branchDiff.getCodeElements();
		assertEquals(1, elements.size());

		// first element: fileB
		DecisionKnowledgeElementInCodeComment firstElement = elements.get(0);
		assertEquals(rat1.getDescription(), firstElement.getDescription());
		assertEquals(rat1.getType(), firstElement.getType());
		assertEquals(rat1.getSummary(), firstElement.getSummary());

		// DecisionKnowledgeElementInCodeComment.KeyData key =
		// firstElement.getKeyData();
		// assertEquals("file.java 1", key.source);
	}
}
