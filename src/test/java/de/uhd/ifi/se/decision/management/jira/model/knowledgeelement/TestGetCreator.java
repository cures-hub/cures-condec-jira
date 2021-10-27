package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetCreator extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testGetCreator() {
		assertEquals(JiraUsers.SYS_ADMIN.getApplicationUser(), element.getCreator());
	}

	@Test
	public void testGetCreatorName() {
		assertEquals("", element.getCreatorName());
	}

}
