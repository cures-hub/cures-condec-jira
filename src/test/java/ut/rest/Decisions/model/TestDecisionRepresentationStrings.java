package ut.rest.Decisions.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.Decisions.model.DecisionRepresentation;

/**
 * 
 * @author Tim Kuchenbuch
 * @description Test Class for Simple Geter and Setter Tests
 *
 */
public class TestDecisionRepresentationStrings {

	private Long id;
	private String name;
	private String description;
	private String type;
	private String projectKey;
	private DecisionRepresentation repre;
	
	//TODO Same Test with issues
	
	@Before 
	public void setUp() {
		this.id=(long) 100;
		this.name="Test";
		this.description="Test";
		this.type="Test";
		this.projectKey="Test";
		
		this.repre= new DecisionRepresentation(id, name, description, type, projectKey);
	}
	
	@Test
	public void testgetId() {
		assertEquals(this.id, this.repre.getId(),0.0);
	}
	
	@Test
	public void testGetName() {
		assertEquals(this.name, this.repre.getName());
	}
	
	@Test
	public void testGetDescription() {
		assertEquals(this.description, this.repre.getDescription());
	}
	
	@Test
	public void testGetType() {
		assertEquals(this.type, this.repre.getType());
	}
	
	@Test
	public void testGetPKey() {
		assertEquals(this.projectKey, this.repre.getProjectKey());
	}
	
	@Test 
	public void testSetId() {
		this.repre.setId(this.id+1);
		assertEquals(this.id+1, this.repre.getId(),0.0);
	}
	
	@Test
	public void testSetName() {
		this.repre.setName(this.name+"New");
		assertEquals(this.name+"New", this.repre.getName());
	}
	
	@Test
	public void testSetDescription() {
		this.repre.setDescription(this.description+"New");
		assertEquals(this.description+"New", this.repre.getDescription());
	}
	
	@Test
	public void testSetType() {
		this.repre.setType(this.type+"New");
		assertEquals(this.type+"New", this.repre.getType());
	}
	
	@Test
	public void tstSetPKey() {
		this.repre.setProjectKey(this.projectKey+"New");
		assertEquals(this.projectKey+"New", this.repre.getProjectKey());
	}
}
