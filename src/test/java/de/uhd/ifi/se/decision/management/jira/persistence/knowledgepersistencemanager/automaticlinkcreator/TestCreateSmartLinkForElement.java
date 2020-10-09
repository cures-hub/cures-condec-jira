package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.automaticlinkcreator;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateSmartLinkForElement extends TestSetUp {

	public static KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testElementNull() {
		assertEquals(0, AutomaticLinkCreator.createSmartLinkForElement(null));
	}

	@Test
	@NonTransactional
	public void testElementValid() {
		assertEquals(1, AutomaticLinkCreator.createSmartLinkForElement(element));
	}

	@Test
	@NonTransactional
	public void testElementAlreadyLinked() {
		assertTrue(AutomaticLinkCreator.createSmartLinkForElement(KnowledgeElements.getTestKnowledgeElement()) > 1);
	}

}
