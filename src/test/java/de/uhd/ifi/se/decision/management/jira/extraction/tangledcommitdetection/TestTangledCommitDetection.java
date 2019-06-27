package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.TangledCommitDetectionImpl;

public class TestTangledCommitDetection extends TestSetUpGit {

	private Diff diffsWithMoreThanOneCommits;
	private Diff diffsWithOneCommit;

	private TangledCommitDetection tangledCommitDetection;

	public Diff mapToDiff(Map<DiffEntry, EditList> diff) {
		DiffImpl mappedDiff = new DiffImpl();
		for (Map.Entry<DiffEntry, EditList> entry : diff.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			mappedDiff.addChangedFile(new ChangedFileImpl(file));
		}
		return mappedDiff;
	}

	@Before
	public void setUp() {
		super.setUp();
		tangledCommitDetection = new TangledCommitDetectionImpl();
		List<RevCommit> commit = gitClient.getCommits(mockJiraIssueForGitTestsTangledSingleCommit);
		Map<DiffEntry, EditList> oneCommit = gitClient.getDiff(commit);
		diffsWithOneCommit = mapToDiff(oneCommit);

		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTestsTangled);
		Map<DiffEntry, EditList> moreThanOneCommits = gitClient.getDiff(commits);
		diffsWithMoreThanOneCommits = mapToDiff(moreThanOneCommits);
	}

	@Test
	public void testCalculatePackageDistances() {
		tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
		diffsWithMoreThanOneCommits.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c2.getPackageDistance() - c1.getPackageDistance());
		assertEquals(6, diffsWithMoreThanOneCommits.getChangedFiles().get(0).getPackageDistance());
		assertEquals(3, diffsWithMoreThanOneCommits.getChangedFiles().get(1).getPackageDistance());
		assertEquals(3, diffsWithMoreThanOneCommits.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePackageDistance() {
		tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
		assertEquals(100, diffsWithOneCommit.getChangedFiles().get(0).getPackageDistance());
	}

	@Test
	public void testStandardizationWithMoreThanOneCommits() {
		tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
		diffsWithMoreThanOneCommits.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c1.getPackageDistance() - c2.getPackageDistance());
		tangledCommitDetection.standardization(diffsWithMoreThanOneCommits);
		assertEquals(100.0, diffsWithMoreThanOneCommits.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(100.0, diffsWithMoreThanOneCommits.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffsWithMoreThanOneCommits.getChangedFiles().get(2).getProbabilityOfCorrectness(), 0.0000);

	}

	@Test
	public void testStandardizationWithOneCommit() {
		tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
		tangledCommitDetection.standardization(diffsWithOneCommit);
		assertEquals(100.0, diffsWithOneCommit.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);

	}

	@Test
	public void testParsePackage() {
		assertEquals(9,
				tangledCommitDetection.parsePackage(
						diffsWithOneCommit.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration())
						.size());
	}

	@Test
	public void testCalculatePackageDistanceRightBiggerLeft() {
		Diff diff = new DiffImpl();
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diffsWithMoreThanOneCommits.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		diff.addChangedFile(diffsWithMoreThanOneCommits.getChangedFiles().get(2));
		diff.addChangedFile(diffsWithMoreThanOneCommits.getChangedFiles().get(1));
		diff.addChangedFile(diffsWithMoreThanOneCommits.getChangedFiles().get(0));
		diff.getChangedFiles().sort(
				(ChangedFile c1, ChangedFile c2) -> c1.getCompilationUnit().getPackageDeclaration().toString().length()
						- c2.getCompilationUnit().getPackageDeclaration().toString().length());
		// System.out.println(diff.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diff.getChangedFiles().get(1).getCompilationUnit().getPackageDeclaration().toString());
		// System.out.println(diff.getChangedFiles().get(2).getCompilationUnit().getPackageDeclaration().toString());
		tangledCommitDetection.calculatePackageDistances(diff);
		assertEquals(6, diff.getChangedFiles().get(0).getPackageDistance());
		assertEquals(3, diff.getChangedFiles().get(1).getPackageDistance());
		assertEquals(3, diff.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePredication() {
		tangledCommitDetection.calculatePredication(diffsWithMoreThanOneCommits);
		assertEquals(100.0, diffsWithMoreThanOneCommits.getChangedFiles().get(0).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(100.0, diffsWithMoreThanOneCommits.getChangedFiles().get(1).getProbabilityOfCorrectness(), 0.0000);
		assertEquals(0.0, diffsWithMoreThanOneCommits.getChangedFiles().get(2).getProbabilityOfCorrectness(), 0.0000);
	}

}
