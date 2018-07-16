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
		this.linkType = "Test";
		this.ingoingId = (long) 100;
		this.outgoingId = (long) 100;
		this.link = new LinkImpl();
		this.link.setLinkType(linkType);
		this.link.setIdOfSourceElement(ingoingId);
		this.link.setIdOfDestinationElement(outgoingId);
	}

	@Test
	public void testgetLinkType() {
		assertEquals(this.linkType, this.link.getLinkType());
	}

	@Test
	public void testGetIngoingId() {
		assertEquals(this.ingoingId, this.link.getIdOfSourceElement(), 0.0);
	}

	@Test
	public void testGetoutGoingId() {
		assertEquals(this.outgoingId, this.link.getIdOfDestinationElement(), 0.0);
	}

	@Test
	public void testSetLinkType() {
		this.link.setLinkType(this.linkType + "New");
		assertEquals(this.linkType + "New", this.link.getLinkType());
	}

	@Test
	public void testSetIngoingId() {
		this.link.setIdOfSourceElement(ingoingId + 1);
		assertEquals(this.ingoingId + 1, this.link.getIdOfSourceElement(), 0.0);
	}

	@Test
	public void testSetOutgoingId() {
		this.link.setIdOfDestinationElement(outgoingId + 1);
		assertEquals(this.outgoingId + 1, this.link.getIdOfDestinationElement(), 0.0);
	}
}