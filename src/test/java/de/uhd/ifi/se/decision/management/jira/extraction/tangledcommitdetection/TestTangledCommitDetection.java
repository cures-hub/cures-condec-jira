package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.TangledCommitDetectionImpl;

public class TestTangledCommitDetection extends TestSetUpGit {

	private Diff diffForJiraIssue;
	private Diff diffForCommit;

	private TangledCommitDetection tangledCommitDetection;

	@Before
	public void setUp() {
		super.setUp();
		tangledCommitDetection = new TangledCommitDetectionImpl();
		diffForCommit = TestDiff.createDiff(mockJiraIssueForGitTestsTangledSingleCommit);
		diffForJiraIssue = TestDiff.createDiff(mockJiraIssueForGitTestsTangled);
	}

	@Test
	public void testCalculatePackageDistances() {
		tangledCommitDetection.calculatePackageDistances(diffForJiraIssue);
		assertEquals(6, diffForJiraIssue.getChangedFiles().get(0).getPackageDistance());
		assertEquals(3, diffForJiraIssue.getChangedFiles().get(1).getPackageDistance());
		assertEquals(3, diffForJiraIssue.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePackageDistance() {
		tangledCommitDetection.calculatePackageDistances(diffForCommit);
		assertEquals(100, diffForCommit.getChangedFiles().get(0).getPackageDistance());
	}

	@Test
	public void testStandardizationWithMoreThanOneCommits() {
		tangledCommitDetection.calculatePackageDistances(diffForJiraIssue);
		diffForJiraIssue.getChangedFiles();
		tangledCommitDetection.standardization(diffForJiraIssue);
		assertEquals(100.0, diffForJiraIssue.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffForJiraIssue.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffForJiraIssue.getChangedFiles().get(2).getProbabilityOfCorrectness(), 0.0000);
	}

	@Test
	public void testStandardizationWithOneCommit() {
		tangledCommitDetection.calculatePackageDistances(diffForCommit);
		tangledCommitDetection.standardization(diffForCommit);
		assertEquals(100.0, diffForCommit.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
	}

	@Test
	public void testCalculatePackageDistanceRightBiggerLeft() {
		Diff diff = new DiffImpl();
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(2));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(1));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(0));
		// System.out.println(diff.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diff.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diff.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		tangledCommitDetection.calculatePackageDistances(diff);
		assertEquals(3, diff.getChangedFiles().get(0).getPackageDistance());
		assertEquals(3, diff.getChangedFiles().get(1).getPackageDistance());
		assertEquals(6, diff.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePredication() {
		tangledCommitDetection.calculatePredication(diffForJiraIssue);
		assertEquals(100.0, diffForJiraIssue.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffForJiraIssue.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffForJiraIssue.getChangedFiles().get(2).getProbabilityOfCorrectness(), 0.0000);
	}

}
