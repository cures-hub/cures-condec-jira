package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetCreator extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testDeleteDecisionKnowledgeElementsProjectKeyValid() {
		CodeClassPersistenceManager codeClassPersistenceManager = new CodeClassPersistenceManager("TEST");
		KnowledgeElement classElement = TestInsertKnowledgeElement.createTestCodeClass();
		// Returns null because it is currently not implemented
		assertNull(codeClassPersistenceManager.getCreator(classElement));
	}

}
