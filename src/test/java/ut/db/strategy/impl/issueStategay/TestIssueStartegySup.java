package ut.db.strategy.impl.issueStategay;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;

import ut.mocks.MockIssueLinkManager;
import ut.mocks.MockIssueLinkTypeManager;
import ut.mocks.MockIssueManager;
import ut.mocks.MockIssueService;

public class TestIssueStartegySup {

	protected IssueStrategy issueStrat;
	private ProjectManager projectManager;
	private IssueManager issueManager;
	
	@Before
	public void setUp(){
		
				
		projectManager = new MockProjectManager();		
		issueManager= new MockIssueManager();	
		
		creatingProjectIssueStructure();
				
		ConstantsManager constManager = new  MockConstantsManager();
		IssueService issueService=new MockIssueService();
		this.issueStrat=new IssueStrategy();
		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
		.addMock(IssueLinkManager.class, new MockIssueLinkManager())
		.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
		.addMock(IssueService.class,issueService)
		.addMock(ProjectManager.class,projectManager)
		.addMock(ConstantsManager.class,constManager);
	}
	
	private void creatingProjectIssueStructure() {
		Project project = new MockProject(1,"TEST");
		((MockProject)project).setKey("TEST");			
		((MockProjectManager) projectManager).addProject(project);
		
		ArrayList<String> types= new ArrayList<>();
		types.add("Bug");
		types.add("Decision");
		types.add("Question");
		types.add("Issue");
		types.add("Goal");
		types.add("Solution");
		types.add("Alternative");
		types.add("Claim");
		types.add("Context");
		types.add("Assumption");
		types.add("Constraint");
		types.add("Implication");
		types.add("Assessment");
		types.add("Argument");
		
		
		for(int i=2;i<types.size()+2;i++) {
			MutableIssue issue = new MockIssue(i,"TEST-"+i);
			((MockIssue)issue).setProjectId(project.getId());
			((MockIssue)issue).setProjectObject(project);
			IssueType issueType = new MockIssueType(i, types.get(i-2));
			((MockIssue)issue).setIssueType(issueType);
			((MockIssueManager)issueManager).addIssue(issue);
			if(i>types.size()) {
				((MockIssue)issue).setParentId((long) 3);
			}
		}
		MutableIssue issue = new MockIssue(50,"TEST-50");
		((MockIssue)issue).setProjectId(project.getId());
		((MockIssue)issue).setProjectObject(project);
		IssueType issueType = new MockIssueType(50, "Class");
		((MockIssue)issue).setIssueType(issueType);
		((MockIssueManager)issueManager).addIssue(issue);
		((MockIssue)issue).setParentId((long) 3);
	}
}
