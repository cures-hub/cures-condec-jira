//package de.uhd.ifi.se.decision.documentation.jira.model;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import de.uhd.ifi.se.decision.documentation.jira.model.LinkImpl;
//
///**
// *
// * @description Test Class for Simple Getter and Setter Tests
// *
// */
//public class TestLink {
//	private String linkType;
//	private long ingoingId;
//	private long outgoingId;
//	private LinkImpl link;
//
//	@Before
//	public void setUp() {
//		this.linkType="Test";
//		this.ingoingId=(long)100;
//		this.outgoingId=(long)100;
//		this.link =new LinkImpl();
//		this.link.setLinkType(linkType);
//		this.link.setIngoingId(ingoingId);
//		this.link.setOutgoingId(outgoingId);
//	}
//
//	@Test
//	public void testgetLinkType() {
//		assertEquals(this.linkType, this.link.getLinkType());
//	}
//
//	@Test
//	public void testGetIngoingId() {
//		assertEquals(this.ingoingId, this.link.getIngoingId(), 0.0);
//	}
//
//	@Test
//	public void testGetoutGoingId() {
//		assertEquals(this.outgoingId, this.link.getOutgoingId(), 0.0);
//	}
//
//	@Test
//	public void testSetLinkType() {
//		this.link.setLinkType(this.linkType+"New");
//		assertEquals(this.linkType+"New", this.link.getLinkType());
//	}
//
//	@Test
//	public void testSetIngoingId() {
//		this.link.setIngoingId(ingoingId+1);
//		assertEquals(this.ingoingId+1, this.link.getIngoingId(), 0.0);
//	}
//
//	@Test
//	public void testSetOutgoingId() {
//		this.link.setOutgoingId(outgoingId+1);
//		assertEquals(this.outgoingId+1, this.link.getOutgoingId(),0.0);
//	}
//
//}
