package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSetAndDeleteGroupAssignment extends TestSetUpGit {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1a", element);
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(null, element));
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
		DecisionGroupPersistenceManager.setGroupAssignment(groups, element);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup1a"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).size() == 2);
	}

	@Test
	public void testDeleteGroupAssignmentIdNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment(-1));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentIdNotNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup2", element);
		long elementId = DecisionGroupPersistenceManager.getDecisionGroupInDatabase("TestGroup2", element).getId();
		assertTrue(DecisionGroupPersistenceManager.deleteGroupAssignment(elementId));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment(null, this.element));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentElementNull() {
		assertFalse(DecisionGroupPersistenceManager.deleteGroupAssignment("TestGroup1a", null));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupAssignmentGroupAndElementNotNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup3", element);
		DecisionGroupPersistenceManager.deleteGroupAssignment("TestGroup3", element);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup3"));
	}

	@Test
	@NonTransactional
	public void testDeleteGroupWithGroupNull() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup4", element);
		assertFalse(DecisionGroupPersistenceManager.deleteGroup(null, "TEST"));

	}

	@Test
	@NonTransactional
	public void testDeleteGroup() {
		DecisionGroupPersistenceManager.insertGroup("TestGroup4", element);
		DecisionGroupPersistenceManager.deleteGroup("TestGroup4", "TEST");
		assertTrue(DecisionGroupPersistenceManager.getAllDecisionElementsWithCertainGroup("TestGroup4", "TEST")
				.size() == 0);
	}

	@Test
	@NonTransactional
	@Ignore
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
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass)
				.contains("TestGroup1a"));
		assertEquals(2, DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).size());

		DecisionGroupPersistenceManager.deleteGroupAssignment("New1", issueFromCodeCommentInGodClass);
		assertFalse(
				DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New1"));
		assertTrue(
				DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("New2"));
	}

}
