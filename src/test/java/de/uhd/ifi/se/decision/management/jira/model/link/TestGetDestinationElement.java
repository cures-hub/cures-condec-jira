package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestGetDestinationElement extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testGetDestinationElement() {
		assertEquals("TEST-4", link.getTarget().getKey());
	}

	@Test
	public void testGetIdOfDestinationElement() {
		assertEquals(4, link.getTarget().getId());
	}

	@Test
	public void testGetTargetNull() {
		link.setDestinationElement(null);
		assertNull(link.getTarget());

		link.setIdOfDestinationElement(1);
		assertNotNull(link.getTarget());
	}
}