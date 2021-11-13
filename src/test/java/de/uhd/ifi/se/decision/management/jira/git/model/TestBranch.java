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
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBranch extends TestSetUpGit {

	private Branch branchDiff;
	private Ref ref;
	private List<KnowledgeElement> rationaleInBranch;
	private KnowledgeElement rat1;
	private KnowledgeElement rat2;
	private KnowledgeElement rat3;

	@Before
	public void setUp() {
		super.setUp();
		rat1 = new KnowledgeElement(0, "I am an issue", "", KnowledgeType.ISSUE, "TEST",
				"file.java 1 INSERT(0-0,0-10) 1:2:3 abcdef01", DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);
		rat2 = new KnowledgeElement(0, "I am an issue too", "", KnowledgeType.ISSUE, "TEST", "commit 1:1 abcdef23",
				DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);
		rat3 = new KnowledgeElement(0, "I am an old issue", "", KnowledgeType.ISSUE, "TEST",
				"~file.java 1 REPLACE(1-4,1-2) 1:2:3 abcdef45", DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);

		rationaleInBranch = new ArrayList<>();
		rationaleInBranch.add(rat1);
		rationaleInBranch.add(rat2);
		rationaleInBranch.add(rat3);
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
		assertEquals("refs/remotes/origin/TEST-4.feature.branch", branchDiff.getBranchName());
		branchDiff = new Branch(null, rationaleInBranch, new ArrayList<>());
		assertNull(branchDiff.getBranchName());
	}

	@Test
	public void testGetElements() {
		branchDiff = new Branch(ref, rationaleInBranch, new ArrayList<>());
		List<Branch.RationaleData> elements = branchDiff.getCodeElements();
		assertEquals(3, elements.size());

		// first element: fileB
		Branch.RationaleData firstElement = elements.get(0);
		assertEquals(rat1.getDescription(), firstElement.getDescription());
		assertEquals(rat1.getType(), firstElement.getType());
		assertEquals(rat1.getSummary(), firstElement.getSummary());

		Branch.RationaleData.KeyData key = firstElement.getKeyData();
		assertEquals(rat1.getKey(), key.value);
		assertEquals("file.java 1", key.source);
		assertEquals("1:2:3", key.position);
		assertEquals("abcdef01", key.rationaleHash);

		// second element: fileB
		Branch.RationaleData secondElement = elements.get(1);
		assertEquals(rat2.getDescription(), secondElement.getDescription());
		assertEquals(rat2.getType(), secondElement.getType());
		assertEquals(rat2.getSummary(), secondElement.getSummary());

		key = secondElement.getKeyData();
		assertEquals(rat2.getKey(), key.value);
		assertEquals("commit", key.source);
		assertEquals("1:1", key.position);
		assertEquals("abcdef23", key.rationaleHash);
		// assertEquals(false, key.codeFileB);

		// third element: fileA
		Branch.RationaleData thirdtElement = elements.get(2);
		assertEquals(rat3.getDescription(), thirdtElement.getDescription());
		assertEquals(rat3.getType(), thirdtElement.getType());
		assertEquals(rat3.getSummary(), thirdtElement.getSummary());

		key = thirdtElement.getKeyData();
		assertEquals(rat3.getKey(), key.value);
		assertEquals("~file.java 1", key.source);
		assertEquals("1:2:3", key.position);
		assertEquals("abcdef45", key.rationaleHash);
		// assertEquals(false, key.codeFileB);
	}
}
