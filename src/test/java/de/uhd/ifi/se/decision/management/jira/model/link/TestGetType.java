package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

/**
 * Test class for links between decision knowledge elements
 */
public class TestGetType extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testGetTypeValid() {
		assertEquals(LinkType.RELATE.toString(), link.getTypeAsString().toLowerCase());
	}

	@Test
	public void testGetTypeNull() {
		link.setType((LinkType) null);
		assertEquals(LinkType.RELATE, link.getType());
	}

	@Test
	public void testSetType() {
		link.setType(LinkType.RELATE.toString() + "New");
		assertEquals("The LinkType has to be in the Enum LinkType. Otherwise the default \"RELATE\" LinkType is used.",
				LinkType.RELATE.toString(), link.getTypeAsString());
		link.setType(LinkType.RELATE);
	}

	@Test
	public void testGetColor() {
		assertEquals(LinkType.RELATE.getColor(), link.getColor());
	}

}