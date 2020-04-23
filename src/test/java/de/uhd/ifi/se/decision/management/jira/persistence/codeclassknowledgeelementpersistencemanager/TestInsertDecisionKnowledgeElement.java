package de.uhd.ifi.se.decision.management.jira.persistence.codeclassknowledgeelementpersistencemanager;

import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestInsertDecisionKnowledgeElement extends TestSetUp {

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
	}

	@Test
	public void testInsertDecisionKnwoledgeElement() {
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(classElement, user);
		assertEquals(classElement.getSummary(), newElement.getSummary());
	}
}
