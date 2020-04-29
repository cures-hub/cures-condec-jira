package de.uhd.ifi.se.decision.management.jira.persistence.codeclassknowledgeelementpersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetDecisionKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private KnowledgeElement classElement2;
	private CodeClassPersistenceManager ccManager;

	@Before
	public void setUp() {
		init();
		ccManager = new CodeClassPersistenceManager("TEST");
		classElement = new KnowledgeElement();
		classElement.setProject("TEST");
		classElement.setType("Other");
		classElement.setDescription("TEST-1;");
		classElement.setSummary("TestClass.java");
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement2 = ccManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	public void testGetDecisionKnowledgeElementById() {
		assertEquals(classElement2, ccManager.getKnowledgeElement(classElement2.getId()));
	}

	@Test
	public void testGetDecisionKnowledgeElementByKey() {
		assertEquals(classElement2, ccManager.getKnowledgeElement(classElement2.getKey()));
	}

	@Test
	public void testGetDecisionKnowledgeElementByElementWithElementNull() {
		assertEquals(null, ccManager.getKnowledgeElement((KnowledgeElement) null));
	}

	@Test
	public void testGetDecisionKnowledgeElementByElementIdZero() {
		classElement.setId(0);
		assertEquals(null, ccManager.getKnowledgeElement(classElement));
		classElement.setId(1);
	}

	@Test
	public void testGetDecisionKnowledgeElementByElement() {
		assertEquals(classElement2, ccManager.getKnowledgeElement(classElement2));
	}

	@Test
	public void testGetDecisionKnowledgeElementByNameAndIssueKeys() {
		assertEquals(classElement2, ccManager.getKnowledgeElementByNameAndIssueKeys(classElement.getSummary(), classElement.getDescription()));
	}

	@Test
	public void testGetEntryForKnowledgeElement() {
		assertEquals(classElement.getSummary(), ccManager.getEntryForKnowledgeElement(classElement2).getFileName());
	}

	@Test
	public void testGetDecisionKnowledgeElements() {
		System.out.println(ccManager.getKnowledgeElements());
		assertEquals(1, ccManager.getKnowledgeElements().size());
	}

	@Test
	public void testGetDecisionKnowledgeElementsMatchingName() {
		assertEquals(1, ccManager.getKnowledgeElementsMatchingName(classElement.getSummary()).size());
	}

}
