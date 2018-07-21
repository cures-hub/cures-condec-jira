package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @description Test class for link getter and setter methods
 */
public class TestLink {
	private String linkType;
	private long ingoingId;
	private long outgoingId;
	private Link link;

	@Before
	public void setUp() {
		linkType = "Test";
		ingoingId = 100;
		outgoingId = 100;
		link = new LinkImpl();
		link.setLinkType(linkType);
		link.setSourceElement(ingoingId);
		link.setDestinationElement(outgoingId);
	}

	@Test
	public void testgetLinkType() {
		assertEquals(this.linkType, this.link.getLinkType());
	}

	@Test
	public void testGetIngoingId() {
		assertEquals(this.ingoingId, this.link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testGetoutGoingId() {
		assertEquals(this.outgoingId, this.link.getDestinationElement().getId(), 0.0);
	}

	@Test
	public void testSetLinkType() {
		this.link.setLinkType(this.linkType + "New");
		assertEquals(this.linkType + "New", this.link.getLinkType());
	}

	@Test
	public void testSetIngoingId() {
		this.link.setSourceElement(ingoingId + 1);
		assertEquals(this.ingoingId + 1, this.link.getSourceElement().getId(), 0.0);
	}

	@Test
	public void testSetOutgoingId() {
		this.link.setDestinationElement(outgoingId + 1);
		assertEquals(this.outgoingId + 1, this.link.getDestinationElement().getId(), 0.0);
	}
}