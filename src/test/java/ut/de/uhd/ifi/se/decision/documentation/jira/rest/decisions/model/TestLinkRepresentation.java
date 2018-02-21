package ut.de.uhd.ifi.se.decision.documentation.jira.rest.decisions.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.documentation.jira.rest.decisions.model.SimpleDecisionRepresentation;

/**
 * 
 * @author Tim Kuchenbuch
 * @description Test Class for Simple Getter and Setter Tests
 * TODO Adding Test for Issue Constructor 
 */
public class TestLinkRepresentation {

	private Long id;
	private String text;
	private SimpleDecisionRepresentation repres;

	@Before
	public void setUp() {
		this.id=(long) 100;
		this.text="Test";
		this.repres=new SimpleDecisionRepresentation();
		this.repres.setId(id);
		this.repres.setText(text);
	}
	
	@Test
	public void testGetId() {
		assertEquals(this.id, this.repres.getId(),0.0);
	}
	
	@Test
	public void testGetText() {
		assertEquals(this.text, this.repres.getText());
	}
	
	@Test
	public void testSetId() {
		this.repres.setId(id+1);
		assertEquals(this.id+1, this.repres.getId(),0.0);
	}
	
	@Test
	public void testSetText() {
		this.repres.setText(text+"New");
		assertEquals(this.text+ "New", this.repres.getText());
	}
}
