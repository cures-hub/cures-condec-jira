package ut.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Core;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

public class TestCreateCore extends TestIssueStrategySetUp {
	
	@Test
	public void testProjectNull() {
		assertEquals(null,issueStrategy.createCore(null));
	}
	
	@Test
	public void testProjectExist() {
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey("TEST");
		assertEquals(Core.class, issueStrategy.createCore(project).getClass());
	}
	
	@Test
	public void testCatchblog() {
		Project project = new MockProject(10);
		assertEquals(Core.class, issueStrategy.createCore(project).getClass());
	}
}
