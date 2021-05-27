package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCompletenessCheck extends TestSetUp {

	private ChangedFile fileThatIsNotDone;
	private ChangedFile smallFileThatIsDone;
	private ChangedFile testFileThatIsDone;
	private ChangedFile linkedFileThatIsDone;

	private CodeCompletenessCheck codeCompletenessCheck;

	@Before
	public void setUp() {
		init();
		codeCompletenessCheck = new CodeCompletenessCheck();
		CodeFiles.addCodeFilesToKnowledgeGraph();
		fileThatIsNotDone = CodeFiles.getCodeFileNotDone();
		smallFileThatIsDone = CodeFiles.getSmallCodeFileDone();
		testFileThatIsDone = CodeFiles.getTestCodeFileDone();
		linkedFileThatIsDone = CodeFiles.getCodeFileLinkedToSolvedDecisionProblemDone();
	}

	@Test
	@NonTransactional
	public void testIsNotDone() {
		assertFalse(codeCompletenessCheck.execute(fileThatIsNotDone));
	}

	@Test
	@NonTransactional
	public void testIsDoneSmallFile() {
		assertTrue(codeCompletenessCheck.execute(smallFileThatIsDone));
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone("TEST");
		definitionOfDone.setLineNumbersInCodeFile(10);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(codeCompletenessCheck.execute(smallFileThatIsDone));
	}

	@Test
	@NonTransactional
	public void testIsDoneTestFile() {
		assertTrue(codeCompletenessCheck.execute(testFileThatIsDone));
	}

	@Test
	@NonTransactional
	public void testIsDoneLinkedFile() {
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone("TEST");
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(1);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		KnowledgeElement decision = JiraIssues.addElementToDataBase(322, KnowledgeType.DECISION);
		KnowledgePersistenceManager.getOrCreate("TEST").insertLink(linkedFileThatIsDone, decision,
				JiraUsers.SYS_ADMIN.getApplicationUser());
		assertTrue(codeCompletenessCheck.execute(linkedFileThatIsDone));
		definitionOfDone.setMaximumLinkDistanceToDecisions(0);
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(codeCompletenessCheck.execute(linkedFileThatIsDone));
	}

	@After
	public void tearDown() {
		// reset plugin settings to default settings
		ConfigPersistenceManager.saveDefinitionOfDone("TEST", new DefinitionOfDone());
	}
}
