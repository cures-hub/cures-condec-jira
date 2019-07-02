package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

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
		Diff diffForJiraIssue = TestDiff.createDiff(mockJiraIssueForGitTestsTangled);
		
		int[][] matrix = tangledCommitDetection.calculatePackageDistances(diffForJiraIssue);
		int[][] expectedMatrix = new int[3][3];
		expectedMatrix[0][0] = 0;
		expectedMatrix[0][1] = 2;
		expectedMatrix[0][2] = 2;
		expectedMatrix[1][0] = 2;
		expectedMatrix[2][0] = 2;
		Assert.assertArrayEquals(expectedMatrix, matrix);

		List<ChangedFile> changedFiles = diffForJiraIssue.getChangedFiles();

		assertEquals("Tangled1.java", changedFiles.get(0).getName());
		assertEquals(Arrays.asList("package de", "uhd", "ifi", "se", "decision", "management", "jira"),
				changedFiles.get(0).getPackageName());

		assertEquals("Untangled.java", changedFiles.get(1).getName());
		assertEquals(
				Arrays.asList("package de", "uhd", "ifi", "se", "decision", "management", "jira", "extraction", "impl"),
				changedFiles.get(1).getPackageName());

		assertEquals("Untangled2.java", changedFiles.get(2).getName());
		assertEquals(
				Arrays.asList("package de", "uhd", "ifi", "se", "decision", "management", "jira", "extraction", "impl"),
				changedFiles.get(2).getPackageName());
		
		assertEquals(4, changedFiles.get(0).getPackageDistance());
		assertEquals(2, changedFiles.get(1).getPackageDistance());
		assertEquals(2, changedFiles.get(2).getPackageDistance());
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
		Diff diffForJiraIssue = TestDiff.createDiff(mockJiraIssueForGitTestsTangled);
		Diff diff = new DiffImpl();

		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(2));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(1));
		diff.addChangedFile(diffForJiraIssue.getChangedFiles().get(0));

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
