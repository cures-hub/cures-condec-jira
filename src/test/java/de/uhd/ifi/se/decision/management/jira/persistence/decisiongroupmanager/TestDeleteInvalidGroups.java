package de.uhd.ifi.se.decision.management.jira.persistence.decisiongroupmanager;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

/**
 * Test class for the persistence of the assigned decision groups.
 */
public class TestDeleteInvalidGroups extends TestSetUp {

	private String projectKey;
	private KnowledgeElement decisionKnowledgeElement;

	@Before
	public void setUp() {
		init();
		long id = 100;
		String summary = "Test";
		String description = "Test";
		KnowledgeType type = KnowledgeType.SOLUTION;
		this.projectKey = "TEST";
		String key = "Test";

		this.decisionKnowledgeElement = new KnowledgeElement(id, summary, description, type, projectKey, key,
				DocumentationLocation.JIRAISSUE, KnowledgeStatus.UNDEFINED);

		DecisionGroupManager.insertGroup("TestGroup1", this.decisionKnowledgeElement);
	}

	@Test
	@NonTransactional
	public void testDeleteInvalidGroups() {
		KnowledgePersistenceManager kpManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		kpManager.deleteKnowledgeElement(decisionKnowledgeElement, JiraUsers.SYS_ADMIN.getApplicationUser());
		assertTrue(DecisionGroupManager.deleteInvalidGroups());
	}

}
