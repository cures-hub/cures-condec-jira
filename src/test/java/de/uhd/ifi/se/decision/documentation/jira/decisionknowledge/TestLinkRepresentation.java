package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;
//package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.SimpleDecisionKnowledgeElement;
//
///**
// *
// * @author Tim Kuchenbuch
// * @description Test Class for Simple Getter and Setter Tests
// */
//public class TestLinkRepresentation {
//
//	private long id;
//	private String text;
//	private SimpleDecisionKnowledgeElement repres;
//
//	@Before
//	public void setUp() {
//		this.id=(long) 100;
//		this.text="Test";
//		this.repres=new SimpleDecisionKnowledgeElement();
//		this.repres.setId(id);
//		this.repres.setText(text);
//	}
//
//	@Test
//	public void testGetId() {
//		assertEquals(this.id, this.repres.getId(),0.0);
//	}
//
//	@Test
//	public void testGetText() {
//		assertEquals(this.text, this.repres.getText());
//	}
//
//	@Test
//	public void testSetId() {
//		this.repres.setId(id+1);
//		assertEquals(this.id+1, this.repres.getId(),0.0);
//	}
//
//	@Test
//	public void testSetText() {
//		this.repres.setText(text+"New");
//		assertEquals(this.text+ "New", this.repres.getText());
//	}
//}
