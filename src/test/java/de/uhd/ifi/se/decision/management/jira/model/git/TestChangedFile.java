package de.uhd.ifi.se.decision.management.jira.model.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;

public class TestChangedFile extends TestSetUpGit {

	private ChangedFile changedFile;

	@Override
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
		assertNotNull(changedFile);
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

		ChangedFile changedFile = new ChangedFile("File content");
		assertEquals(0, changedFile.getMethodDeclarations().size());
	}

	@Test
	public void testParsingMethodsOfNonJavaFile() {
		ChangedFile changedFile = new ChangedFile("File content");
		assertEquals(0, changedFile.getMethodDeclarations().size());
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

	@Test
	public void testGetDiffEntry() {
		assertEquals(ChangeType.ADD, changedFile.getDiffEntry().getChangeType());
	}

	@Test
	public void testGetEditList() {
		assertEquals("EditList[INSERT(0-0,0-29)]", changedFile.getEditList().toString());
	}

	@Test
	public void testGetOldName() {
		// since change type is "ADD", the file did not exist before
		assertEquals("null", changedFile.getOldName());
	}

}