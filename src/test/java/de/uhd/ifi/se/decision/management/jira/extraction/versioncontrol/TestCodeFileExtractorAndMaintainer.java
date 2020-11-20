package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager.TestInsertKnowledgeElement;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeFileExtractorAndMaintainer extends TestSetUpGit {

	private CodeFileExtractorAndMaintainer codeClassPersistenceManager;
	private Diff diff;

	@Override
	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeFileExtractorAndMaintainer("TEST");
		diff = gitClient.getDiffOfEntireDefaultBranch();
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithoutClasses() {
		codeClassPersistenceManager.maintainChangedFilesInDatabase(null);
		// assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithOutClasses() {
		codeClassPersistenceManager.maintainChangedFilesInDatabase(diff);
		// assertEquals(6, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithClasses() {
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		// codeClassPersistenceManager.insertKnowledgeElement(classElement,
		// JiraUsers.SYS_ADMIN.getApplicationUser());
		codeClassPersistenceManager.maintainChangedFilesInDatabase(diff);
		// assertEquals(7, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testExtractAllChangedFilesTwice() {
		codeClassPersistenceManager.extractAllChangedFiles();
		codeClassPersistenceManager.extractAllChangedFiles();
		// assertEquals(6, codeClassPersistenceManager.getKnowledgeElements().size());
	}
}
