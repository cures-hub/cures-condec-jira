package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the persistence of the assigned decision groups.
 */
public class TestDeleteGroupAssignment extends TestSetUp {

	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		long id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String projectKey = "Test";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElementImpl(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	public void testDeleteGroupAssignmentIdNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment(null));
	}

	@Test
	public void testDeleteGroupAssignmentIdNotNull() {
		DecisionGroupManager.insertGroup("TestGroup2", this.decisionKnowledgeElement);
		Long elementId = DecisionGroupManager.getGroupInDatabase("TestGroup2", decisionKnowledgeElement).getId();
		assertTrue(DecisionGroupManager.deleteGroupAssignment(elementId));
	}

	@Test
	public void testDeleteGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment(null, this.decisionKnowledgeElement));
	}

	@Test
	public void testDeleteGroupAssignmentElementNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment("TestGroup1", null));
	}

	@Test
	public void testDeleteGroupAssignmentGroupAndElementNotNull() {
		DecisionGroupManager.insertGroup("TestGroup3", this.decisionKnowledgeElement);
		DecisionGroupManager.deleteGroupAssignment("TestGroup3", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup3"));
	}

	@Test
	public void testDeleteGroupWithGroupNull() {
		DecisionGroupManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupManager.deleteGroup(null, "TEST"));

	}

	@Test
	public void testDeleteGroup() {
		DecisionGroupManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		DecisionGroupManager.deleteGroup("TestGroup4", "TEST");
		assertTrue(DecisionGroupManager.getAllDecisionElementsWithCertainGroup("TestGroup4", "TEST").size() == 0);
	}

}
