package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
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
		KnowledgeElements.getTestKnowledgeElements();
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
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
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
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertTrue(codeCompletenessCheck.execute(linkedFileThatIsDone));
		definitionOfDone.setMaximumLinkDistanceToDecisions(1);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(codeCompletenessCheck.execute(linkedFileThatIsDone));
	}
}
