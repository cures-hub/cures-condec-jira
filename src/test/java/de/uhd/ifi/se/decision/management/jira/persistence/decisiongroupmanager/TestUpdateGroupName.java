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
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;

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
		String projectKey = "Test";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	public void testUpdateGroupName() {
		assertTrue(DecisionGroupManager.updateGroupName("TestGroup1", "ChangedGroup", "Test"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("ChangedGroup"));
		assertFalse(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1"));
	}
}
