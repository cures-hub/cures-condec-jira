package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
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
        diffForJiraIssue.getChangedFiles().sort((ChangedFile c1, ChangedFile c2) -> c1.getPackageDistance() - c2.getPackageDistance());
		//System.out.println(diffForJiraIssue.getChangedFiles().get(0).getPackageDistance());
        //System.out.println(diffForJiraIssue.getChangedFiles().get(1).getPackageDistance());
        //System.out.println(diffForJiraIssue.getChangedFiles().get(2).getPackageDistance());
        assertEquals(2, diffForJiraIssue.getChangedFiles().get(0).getPackageDistance());
		assertEquals(2, diffForJiraIssue.getChangedFiles().get(1).getPackageDistance());
		assertEquals(4, diffForJiraIssue.getChangedFiles().get(2).getPackageDistance());
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
		assertEquals(100.0, diffForJiraIssue.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
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
		//System.out.println(diffForJiraIssue.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		//System.out.println(diffForJiraIssue.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		//System.out.println(diffForJiraIssue.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(2));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(1));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(0));
		//System.out.println(diff.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		//System.out.println(diff.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		//System.out.println(diff.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		tangledCommitDetection.calculatePackageDistances(diff);
		assertEquals(2, diff.getChangedFiles().get(0).getPackageDistance());
		assertEquals(2, diff.getChangedFiles().get(1).getPackageDistance());
		assertEquals(4, diff.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePredication() {
		tangledCommitDetection.calculatePredication(diffForJiraIssue);
		assertEquals(100.0, diffForJiraIssue.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(100.0, diffForJiraIssue.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffForJiraIssue.getChangedFiles().get(2).getProbabilityOfCorrectness(), 0.0000);
	}

}
