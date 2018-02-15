package ut.DecisionDocumentation.util;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.util.PluginListener;
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

import ut.mocks.MockIssueLinkManager;
import ut.mocks.MockIssueLinkTypeManager;
import ut.mocks.MockIssueManager;
import ut.mocks.MockIssueService;
import ut.mocks.MockIssueTypeManager;
import ut.testsetup.TestSetUp;

/**
 * @author Tim Kuchenbuch
 */
public class TestPluginListener extends TestSetUp {
	
	private PluginListener listener;

	@Before 
	public void setUp() {		
		listener = new PluginListener();
	}
	
	@Test
	public void testExecutionAferProp() throws Exception {
		initialisation();
		listener.afterPropertiesSet();
	}
	
	@Test
	public void testExecutionAferPropNoInit() throws Exception {
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
	
}
