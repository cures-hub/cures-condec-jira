package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMaintainChangedFilesInDatabase extends TestSetUpGit {

	private CodeClassPersistenceManager codeClassPersistenceManager;
	private Diff diff;

	@Override
	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("TEST");
		diff = gitClient.getDiffOfEntireDefaultBranch();
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithoutClasses() {
		codeClassPersistenceManager.maintainChangedFilesInDatabase(null);
		assertEquals(0, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithOutClasses() {
		codeClassPersistenceManager.maintainChangedFilesInDatabase(diff);
		assertEquals(6, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testMaintainCodeClassKnowledgeElementsWithClasses() {
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		codeClassPersistenceManager.insertKnowledgeElement(classElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		codeClassPersistenceManager.maintainChangedFilesInDatabase(diff);
		assertEquals(7, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	@NonTransactional
	public void testExtractAllChangedFilesTwice() {
		codeClassPersistenceManager.extractAllChangedFiles();
		codeClassPersistenceManager.extractAllChangedFiles();
		assertEquals(6, codeClassPersistenceManager.getKnowledgeElements().size());
	}
}
