package de.uhd.ifi.se.decision.management.jira.persistence.decisiongrouppersistencemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

public class TestInsertGroup extends TestSetUpGit {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		super.setUp();
		element = KnowledgeElements.getDecision();
		DecisionGroupPersistenceManager.insertGroup("TestGroup1", element);
	}

	@Test
	@NonTransactional
	public void testInsertGroupAlreadyExisting() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup1", element) != -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup(null, element) == -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupElementNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup2", null) == -1);
	}

	@Test

	public void testInsertGroupArgsNotNull() {
		assertTrue(DecisionGroupPersistenceManager.insertGroup("TestGroup2", element) != -1);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(element).contains("TestGroup2"));
	}

	@Test
	@NonTransactional
	public void testInheritInsertGroup() {
		Diff diff = gitClient.getDiffOfEntireDefaultBranch();
		new CodeFileExtractorAndMaintainer("TEST").extractAllChangedFiles(diff);
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		KnowledgeElement godClass = graph.getElementBySummary("GodClass.java");

		graph.vertexSet().stream().forEach(element -> System.out.println(element.getSummary()));

		KnowledgeElement issueFromCodeCommentInGodClass = graph
				.getElementsNotInDatabaseBySummary("Will this issue be parsed correctly?");
		assertEquals("Will this issue be parsed correctly?", issueFromCodeCommentInGodClass.getSummary());
		assertNotNull(godClass.getLink(issueFromCodeCommentInGodClass));

		DecisionGroupPersistenceManager.insertGroup("TestGroup2", godClass);
		assertTrue(DecisionGroupPersistenceManager.getGroupsForElement(issueFromCodeCommentInGodClass)
				.contains("TestGroup2"));
	}

}
