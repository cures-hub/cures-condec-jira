package de.uhd.ifi.se.decision.documentation.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;

public class TestDecisionKnowledgeElementIssue {
	private int id;
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private DecisionKnowledgeElement repre;
	
	@Before 
	public void setUp() {		
		this.id=100;
		this.summary="Test";
		this.description="Test";
		this.type=KnowledgeType.SOLUTION;
		this.projectKey="TEST";
		
		IssueType issueType = new MockIssueType(2, type.toString().toLowerCase());
		
		Project project = new MockProject(1,projectKey);
		
		Issue issue = new MockIssue(id, "TEST-1");
		((MockIssue)issue).setProjectObject(project);
		((MockIssue)issue).setSummary(summary);
		((MockIssue)issue).setDescription(description);
		((MockIssue)issue).setIssueType(issueType);
		
		repre = new DecisionKnowledgeElement(issue);
	}
	
	@Test
	public void testgetId() {
		assertEquals(this.id, this.repre.getId(),0.0);
	}
	
	@Test
	public void testGetName() {
		assertEquals(this.summary, this.repre.getSummary());
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
		this.repre.setSummary(this.summary+"New");
		assertEquals(this.summary+"New", this.repre.getSummary());
	}
	
	@Test
	public void testSetDescription() {
		this.repre.setDescription(this.description+"New");
		assertEquals(this.description+"New", this.repre.getDescription());
	}
	
	@Test
	public void testSetType() {
		this.repre.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, this.repre.getType());
	}
	
	@Test
	public void tstSetPKey() {
		this.repre.setProjectKey(this.projectKey+"New");
		assertEquals(this.projectKey+"New", this.repre.getProjectKey());
	}

}
