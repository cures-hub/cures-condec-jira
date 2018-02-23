package ut.de.uhd.ifi.se.decision.documentation.jira.util;

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

<<<<<<< HEAD:src/test/java/ut/DecisionDocumentation/util/TestPluginListener.java
<<<<<<< Updated upstream:src/test/java/ut/DecisionDocumentation/util/TestPluginListener.java
import ut.mocks.MockIssueLinkManager;
import ut.mocks.MockIssueLinkTypeManager;
import ut.mocks.MockIssueManager;
import ut.mocks.MockIssueService;
import ut.mocks.MockIssueTypeManager;
import ut.testsetup.TestSetUp;
=======
=======
>>>>>>> master:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/util/TestPluginListener.java
import de.uhd.ifi.se.decision.documentation.jira.config.PluginInitializer;
import ut.de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueLinkTypeManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueService;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockIssueTypeManager;
<<<<<<< HEAD:src/test/java/ut/DecisionDocumentation/util/TestPluginListener.java
>>>>>>> Stashed changes:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/util/TestPluginListener.java
=======
>>>>>>> master:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/util/TestPluginListener.java

/**
 * @author Tim Kuchenbuch
 */
public class TestPluginListener extends TestSetUp {
	
	private PluginInitializer listener;

	@Before 
	public void setUp() {		
		listener = new PluginInitializer();
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
