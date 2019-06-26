package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ChangedFileImpl;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestChangedFile extends TestSetUpGit {

	private ChangedFile changedFile;
	private Map<DiffEntry, EditList> diffsWithOneCommit;

	public void setChangedFile() {
		for (Map.Entry<DiffEntry, EditList> entry : diffsWithOneCommit.entrySet()) {
			File file = new File(gitClient.getDirectory().toString().replace(".git", "") + entry.getKey().getNewPath());
			changedFile = new ChangedFileImpl(file);
		}
	}

	@Before
	public void setUp() {
		super.setUp();
		List<RevCommit> commits = gitClient.getCommits(mockJiraIssueForGitTestsTangled);
		diffsWithOneCommit = gitClient.getDiff(commits.get(0));
	}

	@Test
	public void testCreateChangedFile() {
		setChangedFile();
		assertNotNull(changedFile);
	}

	@Test
	public void testGetFile() {
		setChangedFile();
		assertNotNull(changedFile.getFile());
		assertEquals("Tangled1.java", changedFile.getName());
	}

	@Test
	public void testCompilationUnit() {
		setChangedFile();
		assertNotNull(changedFile.getCompilationUnit());
	}

	@Test
	public void testGetSetPackageDistance() {
		setChangedFile();
		changedFile.setPackageDistance(10);
		assertEquals(10, changedFile.getPackageDistance());
	}

	@Test
	public void testGetSetMethodDeclarations() {
		setChangedFile();
		MethodDeclaration methodDeclaration = new MethodDeclaration();
		changedFile.addMethodDeclaration(methodDeclaration.getDeclarationAsString());
		assertEquals(1, changedFile.getMethodDeclarations().size());
	}

	@Test
	public void testGetSetPercentage() {
		setChangedFile();
		assertEquals(0, changedFile.getProbabilityOfCorrectness(), 0.00);
		changedFile.setProbabilityOfCorrectness(80);
		assertEquals(80, changedFile.getProbabilityOfCorrectness(), 0.00);
	}

}