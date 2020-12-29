package de.uhd.ifi.se.decision.management.jira.persistence.automaticlinkcreator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetPotentialParentElement extends TestSetUp {

	public static KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = JiraIssues.addElementToDataBase();
	}

	@Test
	@NonTransactional
	public void testElementNull() {
		assertNull(AutomaticLinkCreator.getPotentialParentElement(null));
	}

	@Test
	@NonTransactional
	public void testElementValid() {
		element.setType(KnowledgeType.ARGUMENT);
		KnowledgeElement parentElement = AutomaticLinkCreator.getPotentialParentElement(element);
		assertEquals(1, parentElement.getId());
	}
}
