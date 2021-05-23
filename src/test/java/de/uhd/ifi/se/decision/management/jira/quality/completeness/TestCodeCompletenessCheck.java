package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCompletenessCheck extends TestSetUp {

	private List<KnowledgeElement> elements;

	private ChangedFile fileThatIsNotDone;
	private ChangedFile smallFileThatIsDone;
	private ChangedFile testFileThatIsDone;
	private ChangedFile linkedFileThatIsDone;

	private CodeCompletenessCheck codeCompletenessCheck;

	@Before
	public void setUp() {
		init();
		codeCompletenessCheck = new CodeCompletenessCheck();
		elements = KnowledgeElements.getTestKnowledgeElements();
		fileThatIsNotDone = (ChangedFile) elements.get(18);
		smallFileThatIsDone = (ChangedFile) elements.get(19);
		testFileThatIsDone = (ChangedFile) elements.get(20);
		linkedFileThatIsDone = (ChangedFile) elements.get(21);
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
		assertTrue(codeCompletenessCheck.execute(linkedFileThatIsDone));
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone("TEST");
		definitionOfDone.setMaximumLinkDistanceToDecisions(1);
		ConfigPersistenceManager.setDefinitionOfDone("TEST", definitionOfDone);
		assertFalse(codeCompletenessCheck.execute(linkedFileThatIsDone));
	}
}
