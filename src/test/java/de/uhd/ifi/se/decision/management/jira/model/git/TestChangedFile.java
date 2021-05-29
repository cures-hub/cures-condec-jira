package de.uhd.ifi.se.decision.management.jira.model.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.junit.Before;
import org.junit.Test;

import com.github.javaparser.ast.body.MethodDeclaration;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

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
	public void testGetName() {
		assertNotNull(changedFile);
		assertEquals("Tangled2.java", changedFile.getName());
		assertEquals("Tangled2.java", changedFile.getSummary());
	}

	@Test
	public void testGetFileContent() {
		assertNotNull(changedFile);
		assertTrue(changedFile.getFileContent().startsWith("package de.uhd.ifi.se.decision.management.jira"));
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

	@Test
	public void testGetOldNameDiffEntryNull() {
		ChangedFile changedFile = new ChangedFile();
		assertEquals("", changedFile.getOldName());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsFalse() {
		assertFalse(changedFile.equals((Object) null));
		assertFalse(changedFile.equals(new KnowledgeElement()));
		assertFalse(changedFile.equals(new Link()));
	}

	@Test
	public void testEqualTrue() {
		KnowledgeElement element = new KnowledgeElement();
		element.setId(changedFile.getId());
		element.setDocumentationLocation(DocumentationLocation.CODE);
		assertTrue(changedFile.equals(element));
	}

	@Test
	public void testParseIdFromKey() {
		assertEquals(1, ChangedFile.parseIdFromKey("TEST:code:1"));
		assertEquals(0, ChangedFile.parseIdFromKey("invalid key"));
	}

	@Test
	public void testFileType() {
		assertEquals(FileType.java(), changedFile.getFileType());
		assertEquals(null, new ChangedFile().getFileType());
	}

	@Test
	public void testCommentStyleType() {
		assertEquals(FileType.java().getCommentStyleType(), changedFile.getCommentStyleType());
		assertEquals(CommentStyleType.UNKNOWN, new ChangedFile().getCommentStyleType());
	}

	@Test
	public void testIsCodeFileToExtract() {
		assertTrue(changedFile.isCodeFileToExtract());
		assertFalse(new ChangedFile().isCodeFileToExtract());
	}
}