package de.uhd.ifi.se.decision.management.jira.view.diffviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

public class TestDiffViewer {
	private DiffViewer viewer;

	private Ref branch1;
	private Ref branch2;
	private List<DecisionKnowledgeElement> rationaleInBranch1;
	private List<DecisionKnowledgeElement> rationaleInBranch2;

	@Before
	public void setUp() {
		branch1 = createDummyRef("branch1");
		branch2 = createDummyRef("branch2");
		DecisionKnowledgeElement rat1_1 = new DecisionKnowledgeElementImpl(0, "I am an issue", "", KnowledgeType.ISSUE,
				"TEST", "file.java 1 INSERT(0-0,0-10) 1:1 abcdef01", DocumentationLocation.COMMIT,
				KnowledgeStatus.UNRESOLVED);
		DecisionKnowledgeElement rat2_1 = new DecisionKnowledgeElementImpl(0, "I am an issue too", "",
				KnowledgeType.ISSUE, "TEST", "0123456789012345678901234567890123456789ef 1:1 abcdef23",
				DocumentationLocation.COMMIT, KnowledgeStatus.UNRESOLVED);
		DecisionKnowledgeElement rat2_2 = new DecisionKnowledgeElementImpl(0, "I am a decision", "",
				KnowledgeType.DECISION, "TEST", "0123456789012345678901234567890123456789ef 1:1 abcdef45",
				DocumentationLocation.COMMIT, KnowledgeStatus.UNRESOLVED);
		rationaleInBranch1 = new ArrayList<>();
		rationaleInBranch2 = new ArrayList<>();
		rationaleInBranch1.add(rat1_1);
		rationaleInBranch2.add(rat2_1);
		rationaleInBranch2.add(rat2_2);
	}

	private Ref createDummyRef(String branchName) {
		return new Ref() {
			@Override
			public String getName() {
				return branchName;
			}

			@Override
			public boolean isSymbolic() {
				return false;
			}

			@Override
			public Ref getLeaf() {
				return null;
			}

			@Override
			public Ref getTarget() {
				return null;
			}

			@Override
			public ObjectId getObjectId() {
				return null;
			}

			@Override
			public ObjectId getPeeledObjectId() {
				return null;
			}

			@Override
			public boolean isPeeled() {
				return false;
			}

			@Override
			public Storage getStorage() {
				return null;
			}
		};
	}

	@Test
	public void testDiffViewerConstructor() {
		Map<Ref, List<DecisionKnowledgeElement>> input = new HashMap<>();
		input.put(branch1, rationaleInBranch1);
		viewer = new DiffViewer(input);
		Assert.assertNotNull(viewer);
	}

	@Test
	public void testGetBranches() {
		Map<Ref, List<DecisionKnowledgeElement>> input = new HashMap<>();
		input.put(branch1, rationaleInBranch1);
		input.put(branch2, rationaleInBranch2);
		viewer = new DiffViewer(input);

		List<BranchDiff> branches = viewer.getBranches();
		Assert.assertNotNull(branches);
		Assert.assertEquals(2, branches.size());
		Assert.assertNotNull(branches.get(0));
		Assert.assertNotNull(branches.get(1));

		input = new HashMap<>();
		viewer = new DiffViewer(input);
		branches = viewer.getBranches();
		Assert.assertNotNull(branches);
		Assert.assertEquals(0, branches.size());
	}
}
