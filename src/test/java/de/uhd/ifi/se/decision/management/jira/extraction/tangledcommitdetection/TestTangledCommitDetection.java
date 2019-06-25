package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.Diff;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.TangledCommitDetection;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DiffImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.TangledCommitDetectionImpl;

public class TestTangledCommitDetection extends TestSetUpGit {

	private Diff diffsWithMoreThanOneCommits;
	private Diff diffsWithOneCommit;
	private Map<DiffEntry, EditList> oneCommit;
	private Map<DiffEntry, EditList> moreThanOneCommits;

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
		oneCommit = gitClient.getDiff(commit);
		diffsWithOneCommit = mapToDiff(oneCommit);

		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTestsTangled);
		moreThanOneCommits = gitClient.getDiff(commits);
		diffsWithMoreThanOneCommits = mapToDiff(moreThanOneCommits);


	}
	@Test
	public void testCalculatePackageDistances() {
		tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
		diffsWithMoreThanOneCommits.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c2.getPackageDistance() - c1.getPackageDistance());
		assertEquals(4, diffsWithMoreThanOneCommits.getChangedFiles().get(0).getPackageDistance());
		assertEquals(2, diffsWithMoreThanOneCommits.getChangedFiles().get(1).getPackageDistance());
		assertEquals(2, diffsWithMoreThanOneCommits.getChangedFiles().get(2).getPackageDistance());
	}

	@Test
	public void testCalculatePackageDistance() {
		tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
		assertEquals(0, diffsWithOneCommit.getChangedFiles().get(0).getPackageDistance());
	}

	@Test
	public void testStandardizationWithMoreThanOneCommits() {
		tangledCommitDetection.calculatePackageDistances(diffsWithMoreThanOneCommits);
		diffsWithMoreThanOneCommits.getChangedFiles()
				.sort((ChangedFile c1, ChangedFile c2) -> c2.getPackageDistance() - c1.getPackageDistance());
		// sort after standardization
		tangledCommitDetection.standardization(diffsWithMoreThanOneCommits);
		assertEquals(100.0, diffsWithMoreThanOneCommits.getChangedFiles().get(0).getProbabilityOfTangledness(), 0.0000);
		assertEquals(50.0, diffsWithMoreThanOneCommits.getChangedFiles().get(1).getProbabilityOfTangledness(), 0.0000);
		assertEquals(50.0, diffsWithMoreThanOneCommits.getChangedFiles().get(2).getProbabilityOfTangledness(), 0.0000);

	}

	@Test
	public void testStandardizationWithOneCommit() {
		tangledCommitDetection.calculatePackageDistances(diffsWithOneCommit);
		tangledCommitDetection.standardization(diffsWithOneCommit);
		assertEquals(0, diffsWithOneCommit.getChangedFiles().get(0).getProbabilityOfTangledness(), 0.0000);

	}

	@Test
	public void testParsePackage() {
		assertEquals(9,
				tangledCommitDetection.parsePackage(
						diffsWithOneCommit.getChangedFiles().get(0).getCompilationUnit().getPackageDeclaration())
						.size());
	}

}
