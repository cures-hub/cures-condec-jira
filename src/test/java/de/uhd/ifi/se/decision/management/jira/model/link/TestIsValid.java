package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestIsValid extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testTrue() {
		assertTrue(link.isValid());
	}

	@Test
	public void testFalse() {
		link.setIdOfDestinationElement(-1);
		assertFalse(link.isValid());
	}
}
