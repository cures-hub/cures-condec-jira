package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestBranchDiff {

	private BranchDiff branchDiff;

	private List<KnowledgeElement> rationaleInBranch;
	private KnowledgeElement rat1 = new KnowledgeElement(0, "I am an issue", "", KnowledgeType.ISSUE, "TEST",
			"file.java 1 INSERT(0-0,0-10) 1:2:3 abcdef01", DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);
	private KnowledgeElement rat2 = new KnowledgeElement(0, "I am an issue too", "", KnowledgeType.ISSUE, "TEST",
			"commit 1:1 abcdef23", DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);
	private KnowledgeElement rat3 = new KnowledgeElement(0, "I am an old issue", "", KnowledgeType.ISSUE, "TEST",
			"~file.java 1 REPLACE(1-4,1-2) 1:2:3 abcdef45", DocumentationLocation.CODE, KnowledgeStatus.UNRESOLVED);

	@Before
	public void setUp() {
		rationaleInBranch = new ArrayList<>();
		rationaleInBranch.add(rat1);
		rationaleInBranch.add(rat2);
		rationaleInBranch.add(rat3);
	}

	@Test
	public void testConstructor() {
		branchDiff = new BranchDiff("abc", rationaleInBranch);
		Assert.assertNotNull(branchDiff);
		branchDiff = new BranchDiff("abc", new ArrayList<>());
		Assert.assertNotNull(branchDiff);
		branchDiff = new BranchDiff(null, rationaleInBranch);
		Assert.assertNotNull(branchDiff);
	}

	@Test
	public void testGetBranchName() {
		branchDiff = new BranchDiff("abc", rationaleInBranch);
		assertEquals("abc", branchDiff.getBranchName());
		branchDiff = new BranchDiff(null, rationaleInBranch);
		Assert.assertNull(branchDiff.getBranchName());
	}

	@Test
	public void testGetElements() {
		branchDiff = new BranchDiff("abc", rationaleInBranch);
		List<BranchDiff.RationaleData> elements = branchDiff.getElements();
		assertEquals(3, elements.size());

		// first element: fileB
		BranchDiff.RationaleData firstElement = elements.get(0);
		assertEquals(rat1.getDescription(), firstElement.getDescription());
		assertEquals(rat1.getType().toString(), firstElement.getType());
		assertEquals(rat1.getSummary(), firstElement.getSummary());

		BranchDiff.RationaleData.KeyData key = firstElement.getKey();
		assertEquals(rat1.getKey(), key.value);
		assertEquals("1", key.diffEntrySequence);
		assertEquals("INSERT(0-0,0-10)", key.diffEntry);
		assertEquals("file.java", key.source);
		assertEquals("1:2:3", key.position);
		assertEquals("abcdef01", key.rationaleHash);
		assertEquals(false, key.codeFileA);
		assertEquals(true, key.sourceTypeCodeFile);
		assertEquals(false, key.sourceTypeCommitMessage);

		// second element: fileB
		BranchDiff.RationaleData secondElement = elements.get(1);
		assertEquals(rat2.getDescription(), secondElement.getDescription());
		assertEquals(rat2.getType().toString(), secondElement.getType());
		assertEquals(rat2.getSummary(), secondElement.getSummary());

		key = secondElement.getKey();
		assertEquals(rat2.getKey(), key.value);
		assertEquals("", key.diffEntrySequence);
		assertEquals("", key.diffEntry);
		assertEquals("commit", key.source);
		assertEquals("1:1", key.position);
		assertEquals("abcdef23", key.rationaleHash);
		assertEquals(false, key.codeFileA);
		// assertEquals(false, key.codeFileB);
		assertEquals(false, key.sourceTypeCodeFile);
		assertEquals(true, key.sourceTypeCommitMessage);

		// third element: fileA
		BranchDiff.RationaleData thirdtElement = elements.get(2);
		assertEquals(rat3.getDescription(), thirdtElement.getDescription());
		assertEquals(rat3.getType().toString(), thirdtElement.getType());
		assertEquals(rat3.getSummary(), thirdtElement.getSummary());

		key = thirdtElement.getKey();
		assertEquals(rat3.getKey(), key.value);
		assertEquals("1", key.diffEntrySequence);
		assertEquals("REPLACE(1-4,1-2)", key.diffEntry);
		assertEquals("~file.java", key.source);
		assertEquals("1:2:3", key.position);
		assertEquals("abcdef45", key.rationaleHash);
		assertEquals(true, key.codeFileA);
		// assertEquals(false, key.codeFileB);
		assertEquals(true, key.sourceTypeCodeFile);
		assertEquals(false, key.sourceTypeCommitMessage);

	}
}
