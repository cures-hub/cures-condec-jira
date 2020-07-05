package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestDeleteDecisionKnowledgeElement extends TestSetUp {

	private CodeClassPersistenceManager ccManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		ccManager = new CodeClassPersistenceManager("Test");
		KnowledgeElement classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement = ccManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	public void testDeleteDecisionKnowledgeElementWithIdZero() {
		assertFalse(ccManager.deleteKnowledgeElement(0, user));
	}


	@Test
	public void testDeleteDecisionKnowledgeElement() {
		assertTrue(ccManager.deleteKnowledgeElement(1, user));
		assertTrue(ccManager.getKnowledgeElements().size() == 0);
	}
}
