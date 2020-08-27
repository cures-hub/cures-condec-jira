package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestGetSourceElement extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testGetSourceElement() {
		assertEquals("TEST-2", link.getSource().getKey());
	}

	@Test
	public void testGetIdOfSourceElement() {
		assertEquals(2, link.getSource().getId());
	}

	@Test
	public void testGetSourceNull() {
		link.setSourceElement(null);
		assertNull(link.getSource());

		link.setIdOfSourceElement(1);
		assertNotNull(link.getSource());
	}
}