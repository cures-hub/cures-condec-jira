package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetUpdateDateAndAuthor extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testGetUpdateDateAndAuthorNotNull() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(), "FooBar");
		element.setUpdateDateAndAuthor(updateDateAndAuthor);

		assertEquals(updateDateAndAuthor, element.getUpdateDateAndAuthor());
	}

	@Test
	public void testGetLatestAuthorName() {
		assertFalse(element.getUpdateDateAndAuthor().isEmpty());
		assertEquals("", element.getLatestAuthorName());
	}

	@Test
	public void testGetLatestAuthorNameWithEmptyUpdateAndAuthorMap() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertTrue(element.getUpdateDateAndAuthor().isEmpty());
		assertEquals("", element.getLatestAuthorName());
	}
}
