package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
public class TestGetGroups extends TestSetUp {

    private long id;
    private KnowledgeElement decisionKnowledgeElement;

    @Before
    public void setUp() {
	init();
	this.id = 100;
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
    public void testGetGroupsForElementNull() {
	assertNull(DecisionGroupManager.getGroupsForElement(null));
    }

    @Test
    public void testGetGroupsForElementNotNull() {
	assertTrue(DecisionGroupManager.getGroupsForElement(decisionKnowledgeElement).contains("TestGroup1"));
    }

    @Test
    public void testGetGroupsForElementIdZero() {
	assertNull(DecisionGroupManager.getGroupsForElement(0, DocumentationLocation.JIRAISSUE));
    }

    @Test
    public void testGetGroupsForElementIdNotNull() {
	assertTrue(DecisionGroupManager.getGroupsForElement(this.id, DocumentationLocation.JIRAISSUE)
		.contains("TestGroup1"));
    }

    @Test
    public void testGetGroupsForElementDocLocWrong() {
	assertNull(DecisionGroupManager.getGroupsForElement(0, DocumentationLocation.COMMIT));
    }

    @Test
    public void testGetGroupsForElementDocLocNull() {
	assertNull(DecisionGroupManager.getGroupsForElement(0, null));
    }

    @Test
    public void testGetGroupsInDatabaseGroupNull() {
	assertNull(DecisionGroupManager.getGroupInDatabase(null, decisionKnowledgeElement));
    }

    @Test
    public void testGetGroupsInDatabaseElementNull() {
	assertNull(DecisionGroupManager.getGroupInDatabase("TestGroup1", null));
    }

    @Test
    public void testGetGroupsInDatabaseArgsNotNull() {
	assertNotNull(DecisionGroupManager.getGroupInDatabase("TestGroup1", decisionKnowledgeElement));
    }

    @Test
    public void testGetAllDecisionGroups() {
	assertTrue(DecisionGroupManager.getAllDecisionGroups().contains("TestGroup1"));
    }

}
