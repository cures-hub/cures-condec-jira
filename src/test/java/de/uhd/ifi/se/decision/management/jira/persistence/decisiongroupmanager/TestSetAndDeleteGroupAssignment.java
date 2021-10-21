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
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
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

		DecisionGroupPersistenceManager.insertGroup("TestGroup1a", this.decisionKnowledgeElement);
	}

	@Test
	public void testSetGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(null, decisionKnowledgeElement));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentElementNull() {
		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(groups, null));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentArgsNotNull() {
		List<String> groups = new ArrayList<String>();
		groups.add("New1");
		groups.add("New2");
		DecisionGroupPersistenceManager.setGroupAssignment(groups, decisionKnowledgeElement);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1a"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).size() == 2);
	}

	@Test
	public void testDeleteGroupAssignmentIdNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment(null));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentIdNotNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", this.decisionKnowledgeElement);
		Long elementId = DecisionGroupPersistenceManager.getGroupInDatabase("TestGroup2", decisionKnowledgeElement).getId();
		assertTrue(DecisionGroupPersistenceManager.deleteGroupAssignment(elementId));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment(null, this.decisionKnowledgeElement));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentElementNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment("TestGroup1a", null));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupAndElementNotNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup3", this.decisionKnowledgeElement);
		DecisionGroupPersistenceManager.deleteGroupAssignment("TestGroup3", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup3"));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupWithGroupNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		assertFalse(DecisionGroupPersistenceManager.deleteGroup(null, "TEST"));

	}

	@Test
	@NonTransactional
	public void testDeleteGroup() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup4", this.decisionKnowledgeElement);
		DecisionGroupPersistenceManager.deleteGroup("TestGroup4", "TEST");
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup("TestGroup4", "TEST").size() == 0);
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

		DecisionGroupPersistenceManager.setGroupAssignment(groups, godClass);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("TestGroup1a"));
		assertEquals(2, DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).size());

		DecisionGroupPersistenceManager.deleteGroupAssignment("New1", issueFromCodeCommentInGodClass);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New1"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New2"));
	}

}
