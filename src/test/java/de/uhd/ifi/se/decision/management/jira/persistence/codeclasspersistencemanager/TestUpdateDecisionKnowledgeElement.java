package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestUpdateDecisionKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private CodeClassPersistenceManager ccManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		ccManager = new CodeClassPersistenceManager("Test");
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = ccManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	@NonTransactional
	public void testUpdateDecisionKnowledgeElementWithElementNull() {
		assertFalse(ccManager.updateKnowledgeElement(null, user));
	}

	@Test
	@NonTransactional
	public void testUpdateDecisionKnowledgeElementWithElementNoProject() {
		classElement.setProject((DecisionKnowledgeProject) null);
		assertFalse(ccManager.updateKnowledgeElement(classElement, user));
		classElement.setProject("TEST");
	}

	@Test
	@NonTransactional
	public void testUpdateDecisionKnowledgeElementWithElementNotInDatabase() {
		KnowledgeElement newClassElement = new KnowledgeElement();
		newClassElement.setProject("TEST");
		assertFalse(ccManager.updateKnowledgeElement(newClassElement, user));
	}

	@Test
	@NonTransactional
	public void testUpdateDecisionKnowledgeElement() {
		classElement.setSummary("ChangedTestClass.java");
		assertTrue(ccManager.updateKnowledgeElement(classElement, user));

	}
}
