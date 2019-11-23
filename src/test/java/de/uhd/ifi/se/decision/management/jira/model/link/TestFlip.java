package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestFlip extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testValidLink() {
		Link flippedLink = link.flip();
		assertEquals(link.getSource(), flippedLink.getTarget());
		assertEquals(link.getTarget(), flippedLink.getSource());
	}
}