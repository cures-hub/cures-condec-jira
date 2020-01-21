package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestEquals extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testNull() {
		assertFalse(link.equals((Object) null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNoLinkObject() {
		assertFalse(link.equals(new KnowledgeElementImpl()));
	}

	@Test
	public void testSelf() {
		assertTrue(link.equals(link));
	}

	@Test
	public void testNewLinkWithSameEndings() {
		Link newLink = new LinkImpl(link.getSource(), link.getTarget());
		assertTrue(link.equals(newLink));
	}
}