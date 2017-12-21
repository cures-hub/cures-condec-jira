package ut.servlet.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.servlet.model.ConfigRepresentation;

/**
 * 
 * @author Tim Kuchenbuch
 * @description Test Class for Simple Geter and Setter Tests
 *
 */
public class TestConfigRepresentation {
	
	private ConfigRepresentation repres;
	private String projectKey;
	private String projectName;
	private String isActivated;
	private String isIssueStrategy;
	
	@Before
	public void setUp() {
		this.projectKey="TestKey";
		this.projectName="TestName";
		this.isActivated="TestTrue";
		this.isIssueStrategy="TestFalse";
		this.repres= new ConfigRepresentation(projectKey, projectName, isActivated, isIssueStrategy);
	}
	
	@Test
	public void testGetProjectKey() {
		assertEquals(this.projectKey, this.repres.getProjectKey());
	}
	
	@Test
	public void testGetProjectName() {
		assertEquals(this.projectName, this.repres.getProjectName());
	}
	
	@Test
	public void testGetIsActivated() {
		assertEquals(this.isActivated, this.repres.getIsActivated());
	}
	
	@Test
	public void testGetIsIssueStrategy() {
		assertEquals(this.isIssueStrategy, this.repres.getIsIssueStrategy());
	}
	
	@Test
	public void testSetProjectKey() {
		this.repres.setProjectKey(this.projectKey+"New");		
		assertEquals(this.projectKey+"New", this.repres.getProjectKey());
	}
	
	@Test
	public void testSetProjectName() {
		this.repres.setProjectName(this.projectName+"New");
		assertEquals(this.projectName+"New", this.repres.getProjectName());
	}
	
	@Test
	public void testSetIsActivated() {
		this.repres.setIsActivated(this.isActivated+"New");
		assertEquals(this.isActivated+"New", this.repres.getIsActivated());
	}
	
	@Test
	public void testSetIsIssueStrategy() {
		this.repres.setIsIssueStrategy(this.isIssueStrategy+"New");
		assertEquals(this.isIssueStrategy+"New", this.repres.getIsIssueStrategy());
	}
}
