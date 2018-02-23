package ut.de.uhd.ifi.se.decision.documentation.jira.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Core;

/**
 * @author Tim Kuchenbuch
 */
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
