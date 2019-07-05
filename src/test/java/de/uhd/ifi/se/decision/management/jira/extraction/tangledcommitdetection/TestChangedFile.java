package de.uhd.ifi.se.decision.management.jira.extraction.tangledcommitdetection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

public class TestChangedFile extends TestSetUpGit {

	private ChangedFile changedFile;

	@Before
	public void setUp() {
		super.setUp();
		changedFile = TestDiff.createDiff(mockJiraIssueForGitTestsTangledSingleCommit).getChangedFiles().get(0);
	}

	@Test
	public void testCreateChangedFile() {
		assertNotNull(changedFile);
	}

	@Test
	public void testGetFile() {
		assertNotNull(changedFile.getFile());
		assertEquals("Tangled2.java", changedFile.getName());
	}

	@Test
	public void testGetSetPackageDistance() {
		changedFile.setPackageDistance(10);
		assertEquals(10, changedFile.getPackageDistance());
	}

	@Test
	public void testGetSetMethodDeclarations() {
		MethodDeclaration methodDeclaration = new MethodDeclaration();
		changedFile.addMethodDeclaration(methodDeclaration.getDeclarationAsString());
		assertEquals(3, changedFile.getMethodDeclarations().size());
	}

	@Test
	public void testGetSetPercentage() {
		assertEquals(0, changedFile.getProbabilityOfCorrectness(), 0.00);
		changedFile.setProbabilityOfCorrectness(80);
		assertEquals(80, changedFile.getProbabilityOfCorrectness(), 0.00);
	}

	@Test
	public void testGetPackageName() {
		assertEquals(9, changedFile.getPartsOfPackageDeclaration().size());
	}

}