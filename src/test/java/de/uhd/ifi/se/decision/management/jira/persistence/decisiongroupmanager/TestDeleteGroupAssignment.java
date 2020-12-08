package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import net.java.ao.test.jdbc.NonTransactional;

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

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	public void testDeleteGroupAssignmentIdNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment(null));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentIdNotNull() {
		DecisionGroupManager.insertGroup("TestGroup2", this.decisionKnowledgeElement);
		Long elementId = DecisionGroupManager.getGroupInDatabase("TestGroup2", decisionKnowledgeElement).getId();
		assertTrue(DecisionGroupManager.deleteGroupAssignment(elementId));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment(null, this.decisionKnowledgeElement));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentElementNull() {
		assertFalse(DecisionGroupManager.deleteGroupAssignment("TestGroup1", null));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupAndElementNotNull() {
		DecisionGroupManager.insertGroup("TestGroup3", this.decisionKnowledgeElement);
		DecisionGroupManager.deleteGroupAssignment("TestGroup3", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup3"));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupWithGroupNull() {
		DecisionGroupManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupManager.deleteGroup(null, "TEST"));

	}

	@Test
	@NonTransactional
	public void testDeleteGroup() {
		DecisionGroupManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		DecisionGroupManager.deleteGroup("TestGroup4", "TEST");
		assertTrue(DecisionGroupManager.getAllDecisionElementsWithCertainGroup("TestGroup4", "TEST").size() == 0);
	}

	@Test
	public void testInheritDeleteGroupAssignment() {
		new CodeFileExtractorAndMaintainer("TEST").extractAllChangedFiles();
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate("TEST");
		List<KnowledgeElement> codeFiles = graph.getElements(KnowledgeType.CODE);

		KnowledgeElement godClass = null;
		for (KnowledgeElement codeFile : codeFiles) {
			if (codeFile.getSummary().equals("GodClass.java")) {
				godClass = codeFile;
				break;
			}
		}

		KnowledgeElement issueFromCodeCommentInGodClass = graph.getElementsNotInDatabaseBySummary("Will this issue be parsed correctly?");

		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");
		DecisionGroupManager.setGroupAssignment(groups, godClass);
		DecisionGroupManager.deleteGroupAssignment("New1", issueFromCodeCommentInGodClass);
		assertFalse(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New1"));
		assertTrue(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New2"));
	}

}
