package ut.db.strategy.impl.issueStategay;

import org.junit.Before;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.ProjectManager;

import ut.mocks.MockIssueLinkManager;
import ut.mocks.MockIssueService;

public class TestIssueStartegySup {

	protected IssueStrategy issueStrat;
	
	@Before
	public void setUp(){
		ProjectManager projectManager = new MockProjectManager();
		((MockProjectManager) projectManager).addProject(new MockProject(1,"TEST"));
		
		ConstantsManager constManager = new  MockConstantsManager();
		IssueService issueService=new MockIssueService();
		this.issueStrat=new IssueStrategy();
		new MockComponentWorker().init().addMock(IssueLinkManager.class, new MockIssueLinkManager())
		.addMock(IssueService.class,issueService)
		.addMock(ProjectManager.class,projectManager)
		.addMock(ConstantsManager.class,constManager);
	}
}
