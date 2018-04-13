package de.uhd.ifi.se.decision.documentation.jira.config;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueService;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueTypeManager;

public class TestPluginInitializer extends TestSetUp {
	
	private PluginInitializer listener;

	@Before 
	public void setUp() {		
		listener = new PluginInitializer();
	}
	
	@Test(expected = java.lang.NullPointerException.class)
	public void testExecutionAfterProp() throws Exception {
		initialisation();
		listener.afterPropertiesSet();
	}
	
	@Test
	public void testExecutionAfterPropNoInit() throws Exception {
		ProjectManager projectManager = new MockProjectManager();		
		IssueManager issueManager= new MockIssueManager();	
		ConstantsManager constManager = new  MockConstantsManager();
		IssueService issueService=new MockIssueService();
		
		
		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
		.addMock(IssueTypeManager.class, new MockIssueTypeManager())
		.addMock(IssueLinkManager.class, new MockIssueLinkManager())
		.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager(true))
		.addMock(IssueService.class,issueService)
		.addMock(ProjectManager.class,projectManager)
		.addMock(ConstantsManager.class,constManager);
		
		listener.afterPropertiesSet();
	}

	@Test
	public void testExecutionAfterPropInit() throws Exception {
		ProjectManager projectManager = new MockProjectManager();
		IssueManager issueManager= new MockIssueManager();
		ConstantsManager constManager = new  MockConstantsManager();
		IssueService issueService=new MockIssueService();


		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueTypeManager.class, new MockIssueTypeManager())
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager(false))
				.addMock(IssueService.class,issueService)
				.addMock(ProjectManager.class,projectManager)
				.addMock(ConstantsManager.class,constManager);

		//Adding all Issue Types
		constManager.insertIssueType("Decision", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Alternative", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Argument", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Assessment", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Assumption", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Claim", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Constraint", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Context", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Goal", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Implication", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Issue", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Problem", (long) 20, "Test", "Test", (long) 12290);
		constManager.insertIssueType("Solution", (long) 20, "Test", "Test", (long) 12290);


		listener.afterPropertiesSet();
	}
	
}
