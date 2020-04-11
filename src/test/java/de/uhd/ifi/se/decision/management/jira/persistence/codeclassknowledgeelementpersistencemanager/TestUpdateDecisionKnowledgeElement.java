package de.uhd.ifi.se.decision.management.jira.persistence.codeclassknowledgeelementpersistencemanager;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.CodeClassKnowledgeElementPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUpdateDecisionKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private CodeClassKnowledgeElementPersistenceManager ccManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		ccManager = new CodeClassKnowledgeElementPersistenceManager("Test");
		classElement = new KnowledgeElementImpl();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = ccManager.insertDecisionKnowledgeElement(classElement, user);
	}

	@Test
	public void testUpdateDecisionKnowledgeElementWithElementNull() {
		assertFalse(ccManager.updateDecisionKnowledgeElement(null, user));
	}

	@Test
	public void testUpdateDecisionKnowledgeElementWithElementNoProject() {
		classElement.setProject((DecisionKnowledgeProject) null);
		assertFalse(ccManager.updateDecisionKnowledgeElement(classElement, user));
		classElement.setProject("TEST");
	}

	@Test
	public void testUpdateDecisionKnowledgeElementWithElementNotInDatabase() {
		KnowledgeElement newClassElement = new KnowledgeElementImpl();
		newClassElement.setProject("TEST");
		assertFalse(ccManager.updateDecisionKnowledgeElement(newClassElement, user));
	}

	@Test
	public void testUpdateDecisionKnowledgeElement() {
		classElement.setSummary("ChangedTestClass.java");
		assertTrue(ccManager.updateDecisionKnowledgeElement(classElement, user));

	}
}
