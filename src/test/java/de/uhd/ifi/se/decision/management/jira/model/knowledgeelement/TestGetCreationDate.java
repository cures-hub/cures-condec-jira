package de.uhd.ifi.se.decision.management.jira.model.knowledgeelement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestGetCreationDate extends TestSetUp {

	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	public void testGetCreationDate() {
		Date date = new Date(0);
		element.setCreationDate(date);

		assertEquals(date, element.getCreationDate());
	}

	@Test
	public void testGetCreationDateNewDateNewerThanOldestUpdate() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(5), "FooBar");
		updateDateAndAuthor.put(new Date(15), "FooBar");

		element.setUpdateDateAndAuthor(updateDateAndAuthor);
		element.setCreationDate(new Date(120));

		assertEquals(new Date(5), element.getCreationDate());
	}

	@Test
	public void testGetCreationDateNewDateOlderThanUpdates() {
		TreeMap<Date, String> updateDateAndAuthor = new TreeMap<Date, String>();
		updateDateAndAuthor.put(new Date(5), "FooBar");
		updateDateAndAuthor.put(new Date(15), "FooBar");
		element.setUpdateDateAndAuthor(updateDateAndAuthor);
		element.setCreationDate(new Date(0));

		assertEquals(new Date(0), element.getCreationDate());
	}

	@Test
	public void testGetCreatorNameWithEmptyUpdateAndAuthorMap() {
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		assertTrue(element.getUpdateDateAndAuthor().isEmpty());
		assertNotNull(element.getCreationDate());
	}
}
