package de.uhd.ifi.se.decision.documentation.jira.config;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import de.uhd.ifi.se.decision.documentation.jira.ComponentGetter;
import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.*;
import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeProject;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestProjects extends TestSetUp {

    private EntityManager entityManager;
    private ProjectManager projectManager;


    private  Projects projects;

    @Before
    public void setUp(){
        projectManager = new MockProjectManager();
        new MockComponentWorker().init().addMock(ProjectManager.class, projectManager);
        new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
        projects = new Projects();
    }

    @Test
    public void testGetProjectMapNoProject(){
       Map<String, DecisionKnowledgeProject> map = new HashMap<String, DecisionKnowledgeProject>();
        assertEquals(map, this.projects.getProjectsMap());
    }

    @Test
    public void testGetProjectMapProjects(){
        Project project = new MockProject(1, "TEST");
        ((MockProject) project).setKey("TEST");
        ((MockProjectManager) projectManager).addProject(project);
        assertTrue(this.projects.getProjectsMap().size()>=0);
    }
}
