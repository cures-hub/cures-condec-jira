package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
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
public class TestSetGroupAssignment extends TestSetUpGit {

	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
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
		assertFalse(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1"));
		assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).size() == 2);
	}

	@Test
	public void testInheritSetGroupAssignment() {
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
		assertFalse(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).contains("TestGroup1"));
		assertTrue(DecisionGroupManager.getGroupsForElement(issueFromCodeCommentInGodClass).size() == 2);
	}

}
