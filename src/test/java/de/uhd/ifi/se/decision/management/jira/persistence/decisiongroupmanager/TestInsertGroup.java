package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;

/**
 * Test class for the persistence of the assigned decision groups.
 */
public class TestInsertGroup extends TestSetUp {

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
    public void testInsertGroupAlreadyExisting() {
	assertTrue(DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement) != -1);
    }

    @Test
    public void testInsertGroupNull() {
	assertTrue(DecisionGroupManager.insertGroup(null, decisionKnowledgeElement) == -1);
    }

    @Test
    public void testInsertGroupElementNull() {
	assertTrue(DecisionGroupManager.insertGroup("TestGroup2", null) == -1);
    }

    @Test
    public void testInsertGroupArgsNotNull() {
	assertTrue(DecisionGroupManager.insertGroup("TestGroup2", decisionKnowledgeElement) != -1);
    }

}
