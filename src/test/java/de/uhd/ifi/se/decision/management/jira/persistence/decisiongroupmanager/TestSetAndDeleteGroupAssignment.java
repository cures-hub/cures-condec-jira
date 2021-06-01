package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import net.java.ao.test.jdbc.NonTransactional;

/**
 * Test class for the persistence of the assigned decision groups.
 */
public class TestSetAndDeleteGroupAssignment extends TestSetUpGit {

	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		super.setUp();
		long id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		String projectKey = "TEST";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup1a", this.decisionKnowledgeElement);
	}

	@Test
	public void testSetGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupManager.setGroupAssignment(null, decisionKnowledgeElement));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentElementNull() {
		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");
		assertFalse(DecisionGroupManager.setGroupAssignment(groups, null));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentArgsNotNull() {
		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");
		DecisionGroupManager.setGroupAssignment(groups, decisionKnowledgeElement);
		assertFalse(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1a"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).size() == 2);
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
		assertFalse(DecisionGroupManager.deleteGroupAssignment("TestGroup1a", null));
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
	public void testInheritSetAndDeleteGroupAssignment() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").extractAllChangedFiles(diff);
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		KnowledgeElement godClass = graph.getElementBySummary("GodClass.java");

		KnowledgeElement issueFromCodeCommentInGodClass = graph
				.getElementsNotInDatabaseBySummary("Will this issue be parsed correctly?");
		assertEquals("Will this issue be parsed correctly?", issueFromCodeCommentInGodClass.getSummary());
		assertNotNull(godClass.getLink(issueFromCodeCommentInGodClass));

		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");

		DecisionGroupManager.setGroupAssignment(groups, godClass);
		assertFalse(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("TestGroup1a"));
		assertEquals(2, DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).size());

		DecisionGroupManager.deleteGroupAssignment("New1", issueFromCodeCommentInGodClass);
		assertFalse(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New1"));
		assertTrue(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New2"));
	}

}
