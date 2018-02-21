package ut.de.uhd.ifi.se.decdoc.jira.rest.decisions.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decdoc.jira.rest.decisions.model.LinkRepresentation;

/**
 * 
 * @author Tim Kuchenbuch
 * @description Test Class for Simple Getter and Setter Tests
 *
 */
public class TestSimpleDecisionRepresentation {
	private String linkType;
	private long ingoingId;
	private long outgoingId;
	private LinkRepresentation repres;
	
	@Before
	public void setUp() {
		this.linkType="Test";
		this.ingoingId=(long)100;
		this.outgoingId=(long)100;
		this.repres=new LinkRepresentation();
		this.repres.setLinkType(linkType);
		this.repres.setIngoingId(ingoingId);
		this.repres.setOutgoingId(outgoingId);
	}
	
	@Test
	public void testgetLinkType() {
		assertEquals(this.linkType, this.repres.getLinkType());
	}
	
	@Test
	public void testGetIngoingId() {
		assertEquals(this.ingoingId, this.repres.getIngoingId(), 0.0);
	}
	
	@Test
	public void testGetoutGoingId() {
		assertEquals(this.outgoingId, this.repres.getOutgoingId(), 0.0);
	}
	
	@Test
	public void testSetLinkType() {
		this.repres.setLinkType(this.linkType+"New");
		assertEquals(this.linkType+"New", this.repres.getLinkType());
	}
	
	@Test
	public void testSetIngoingId() {
		this.repres.setIngoingId(ingoingId+1);
		assertEquals(this.ingoingId+1, this.repres.getIngoingId(), 0.0);
	}
	
	@Test
	public void testSetOutgoingId() {
		this.repres.setOutgoingId(outgoingId+1);
		assertEquals(this.outgoingId+1, this.repres.getOutgoingId(),0.0);
	}

}
