package de.uhd.ifi.se.decision.management.jira.git;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.codeclasspersistencemanager.TestInsertKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeFileExtractorAndMaintainer extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffNull() {
		new CodeFileExtractorAndMaintainer("TEST").maintainChangedFilesInDatabase(null);
		assertEquals(0, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffEmpty() {
		new CodeFileExtractorAndMaintainer("TEST").maintainChangedFilesInDatabase(new Diff());
		assertEquals(0, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffValidNoFilesInDatabase() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").maintainChangedFilesInDatabase(diff);
		assertEquals(5, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainChangedFilesDiffValidWithFilesInDatabase() {
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		CodeClassPersistenceManager persistenceManager = new CodeClassPersistenceManager("TEST");
		persistenceManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").maintainChangedFilesInDatabase(diff);
		assertEquals(6, persistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testUpdateNonJavaFile() {
		CodeFileExtractorAndMaintainer codeFileExtractorAndMaintainer = new CodeFileExtractorAndMaintainer("TEST");
		codeFileExtractorAndMaintainer.addUpdateOrDeleteChangedFileInDatabase(new ChangedFile());
		assertEquals(0, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testExtractAllChangedFilesTwice() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		CodeFileExtractorAndMaintainer codeFileExtractorAndMaintainer = new CodeFileExtractorAndMaintainer("TEST");
		codeFileExtractorAndMaintainer.extractAllChangedFiles(diff);
		assertEquals(5, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
		codeFileExtractorAndMaintainer.extractAllChangedFiles(diff);
		assertEquals(5, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testFileTypeOfCodeFileShouldNotBeExtracted() {
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration("TEST");
		gitConfig.setFileTypesToExtract(new ArrayList<>());
		ConfigPersistenceManager.saveGitConfiguration("TEST", gitConfig);

		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		CodeFileExtractorAndMaintainer codeFileExtractorAndMaintainer = new CodeFileExtractorAndMaintainer("TEST");
		codeFileExtractorAndMaintainer.extractAllChangedFiles(diff);
		assertEquals(0, new CodeClassPersistenceManager("TEST").getKnowledgeElements().size());

		ConfigPersistenceManager.saveGitConfiguration("TEST", new GitConfiguration());
	}
}
