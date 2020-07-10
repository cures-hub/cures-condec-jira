package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestInsertKnowledgeElement extends TestSetUp {

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
		classElement.setSummary("TestClass.java");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	@Test
	@NonTransactional
	public void testInsertDecisionKnwoledgeElement() {
		classElement.setDescription("TEST-1;");
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(classElement, user);
		assertEquals(classElement.getSummary(), newElement.getSummary());
	}

	@Test
	@NonTransactional
	public void testInsertDecisionKnwoledgeElementDescLength() {
		classElement.setDescription("TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;"
				+ "TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;TEST-1;");
		KnowledgeElement newElement = ccManager.insertKnowledgeElement(classElement, user);
		assertEquals(classElement.getSummary(), newElement.getSummary());
	}
}
