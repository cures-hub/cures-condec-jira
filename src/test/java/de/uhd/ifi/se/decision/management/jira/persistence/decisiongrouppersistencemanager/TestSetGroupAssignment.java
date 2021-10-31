package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.git.CodeFileExtractorAndMaintainer;
import de.uhd.ifi.se.decision.management.jira.git.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSetGroupAssignment extends TestSetUpGit {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		super.setUp();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup", element);
	}

	@Test
	@NonTransactional
	public void testGroupNamesNullElementValid() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(null, element));
	}

	@Test
	@NonTransactional
	public void testGroupNamesNullElementNull() {
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(Set.of("UI", "process"), null));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentArgsNotNull() {
		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("UI", "process"), element);
		List<String> groups = DecisionGroupPersistenceManager.getGroupsForElement(element);
		assertFalse(groups.contains("TestGroup"));
		assertTrue(groups.contains("process"));
		assertTrue(groups.contains("UI"));
		assertEquals(2, groups.size());
	}

	@Test
	@NonTransactional
	public void testInheritInsertGroup() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").extractAllChangedFiles(diff);
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		KnowledgeElement godClass = graph.getElementBySummary("GodClass.java");

		KnowledgeElement issueFromCodeCommentInGodClass = graph
				.getElementsNotInDatabaseBySummary("Will this issue be parsed correctly?");
		assertEquals("Will this issue be parsed correctly?", issueFromCodeCommentInGodClass.getSummary());
		assertNotNull(godClass.getLink(issueFromCodeCommentInGodClass));

		DecisionGroupPersistenceManager.setGroupAssignment(Set.of("TestGroup2"), godClass);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass)
				.contains("TestGroup2"));
	}
}
