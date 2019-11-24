package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetOrCreate extends TestSetUp {

	public KnowledgePersistenceManager knowledgePersistenceManager;

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testProjectKeyValid() {
		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate("TEST");
		assertEquals(manager, KnowledgePersistenceManager.getOrCreate("TEST"));
		assertEquals("TEST", manager.getProjectKey());
	}

	@Test
	@NonTransactional
	public void testProjectValid() {
		assertNotNull(KnowledgePersistenceManager.getOrCreate(new DecisionKnowledgeProjectImpl("TEST")));
	}

	@Test(expected = IllegalArgumentException.class)
	@NonTransactional
	public void testProjectKeyNull() {
		KnowledgePersistenceManager.getOrCreate((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	@NonTransactional
	public void testProjectNull() {
		KnowledgePersistenceManager.getOrCreate((DecisionKnowledgeProject) null);
	}
}