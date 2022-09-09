package de.uhd.ifi.se.decision.management.jira.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.codeclasspersistencemanager.TestInsertKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeFileExtractorAndMaintainer extends TestSetUpGit {

	private CodeFileExtractorAndMaintainer codeFileExtractorAndMaintainer;
	private CodeClassPersistenceManager codeClassPersistenceManager;

	@Before
	public void setUp() {
		super.setUp();
		codeFileExtractorAndMaintainer = new CodeFileExtractorAndMaintainer("TEST");
		codeClassPersistenceManager = KnowledgePersistenceManager.getInstance("TEST")
				.getCodeClassPersistenceTextManager();
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffNull() {
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(null);
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffEmpty() {
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(new Diff());
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffValidNoFilesInDatabase() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(diff);
		assertEquals(5, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffValidWithFilesInDatabase() {
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		codeClassPersistenceManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(diff);
		assertEquals(6, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testUpdateNonJavaFile() {
		codeFileExtractorAndMaintainer.addUpdateOrDeleteChangedFileInDatabase(new ChangedFile());
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testExtractAllChangedFilesTwice() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		assertEquals(5, diff.getChangedFiles().size());
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(diff);
		assertEquals(5, codeClassPersistenceManager.getKnowledgeElements().size());
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(diff);
		assertEquals(5, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testFileTypeOfCodeFileShouldNotBeExtracted() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setFileTypesToExtract(new ArrayList<>());
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);
		assertNull(gitConfig.getFileTypeForEnding(".java"));

		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		codeFileExtractorAndMaintainer.maintainChangedFilesInDatabase(diff);
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());

		ConfigPersistenceManager.saveGitConfiguration("TEST", new GitConfiguration());
	}

	@Test
	@NonTransactional
	public void testDeleteOldFiles() {
		ChangedFile oldFile = new ChangedFile();
		oldFile.setId(42);
		oldFile.setProject("TEST");
		oldFile.setSummary("FileFromThePastAlreadyDeleted.java");
		codeClassPersistenceManager.insertKnowledgeElement(oldFile, null);
		GitClient.instances.put("TEST", gitClient);
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		assertTrue(codeFileExtractorAndMaintainer.deleteOldFiles(diff));
	}
}