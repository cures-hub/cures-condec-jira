package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateGroupName extends TestSetUp {

	private long id;
	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		this.id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String projectKey = "TEST";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupPersistenceManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	@NonTransactional
	public void testUpdateGroupName() {
		assertTrue(DecisionGroupPersistenceManager.updateGroupName("TestGroup1", "ChangedGroup", "TEST"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).contains("ChangedGroup"));
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1"));
	}
}
