package de.uhd.ifi.se.decision.management.jira.persistence.codeclasspersistencemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestGetKnowledgeElement extends TestSetUp {

	private KnowledgeElement classElement;
	private KnowledgeElement classElement2;
	private CodeClassPersistenceManager codeClassPersistenceManager;

	@Before
	public void setUp() {
		init();
		codeClassPersistenceManager = new CodeClassPersistenceManager("TEST");
		classElement = TestInsertKnowledgeElement.createTestCodeClass();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		classElement2 = codeClassPersistenceManager.insertKnowledgeElement(classElement, user);
	}

	@Test
	public void testGetKnowledgeElementById() {
		assertEquals(classElement2, codeClassPersistenceManager.getKnowledgeElement(classElement2.getId()));
	}

	@Test
	public void testGetKnowledgeElementByKey() {
		assertEquals("TEST:code:1", classElement2.getKey());
		assertEquals(classElement2, codeClassPersistenceManager.getKnowledgeElement("TEST:code:1"));
	}

	@Test
	public void testGetKnowledgeElementByElementWithElementNull() {
		assertEquals(null, codeClassPersistenceManager.getKnowledgeElement((KnowledgeElement) null));
	}

	@Test
	public void testGetKnowledgeElementByElementIdZero() {
		classElement.setId(0);
		assertEquals(null, codeClassPersistenceManager.getKnowledgeElement(classElement));
		classElement.setId(1);
	}

	@Test
	public void testGetKnowledgeElementByElement() {
		assertEquals(classElement2, codeClassPersistenceManager.getKnowledgeElement(classElement2));
	}

	@Test
	public void testGetKnowledgeElementByName() {
		assertEquals(classElement2, codeClassPersistenceManager.getKnowledgeElementByName(classElement.getSummary()));
	}

	@Test
	public void testGetEntryForKnowledgeElement() {
		assertEquals(classElement.getSummary(),
				codeClassPersistenceManager.getEntryForKnowledgeElement(classElement2).getFileName());
	}

	@Test
	public void testGetKnowledgeElements() {
		assertEquals(1, codeClassPersistenceManager.getKnowledgeElements().size());
	}

	@Test
	public void testGetKnowledgeElementsMatchingName() {
		assertEquals(1, codeClassPersistenceManager.getKnowledgeElementsMatchingName(classElement.getSummary()).size());
	}
}
