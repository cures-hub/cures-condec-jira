package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
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
		Set<String> groups = new HashSet<>();
		groups.add("New1");
		groups.add("New2");
		assertFalse(DecisionGroupPersistenceManager.setGroupAssignment(groups, null));
	}

	@Test
	@NonTransactional
	public void testSetGroupAssignmentArgsNotNull() {
		Set<String> groups = new HashSet<>();
		groups.add("New1");
		groups.add("New2");
		DecisionGroupPersistenceManager.setGroupAssignment(groups, element);
		assertFalse(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup1a"));
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).size() == 2);
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
