package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
public class TestInsertGroup extends TestSetUpGit {

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

		DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	@NonTransactional
	public void testInsertGroupAlreadyExisting() {
		assertTrue(DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement) != -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupNull() {
		assertTrue(DecisionGroupManager.insertGroup(null, decisionKnowledgeElement) == -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupElementNull() {
		assertTrue(DecisionGroupManager.insertGroup("TestGroup2", null) == -1);
	}

	@Test
	@NonTransactional
	public void testInsertGroupArgsNotNull() {
		assertTrue(DecisionGroupManager.insertGroup("TestGroup2", decisionKnowledgeElement) != -1);
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup2"));
	}

	@Test
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

		DecisionGroupManager.insertGroup("TestGroup2", godClass);
		assertTrue(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("TestGroup2"));
	}

}
