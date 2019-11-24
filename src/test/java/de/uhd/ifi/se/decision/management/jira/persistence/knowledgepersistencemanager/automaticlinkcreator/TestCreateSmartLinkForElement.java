package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.automaticlinkcreator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateSmartLinkForElement extends TestSetUp {

	public static DecisionKnowledgeElement element;

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

}
