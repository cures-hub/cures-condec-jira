package de.uhd.ifi.se.decision.management.jira.releasenotes;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestReleaseNote {
	private ReleaseNote note;
	private long id;
	private String title;
	private DecisionKnowledgeProject project;
	private String content;
	@Before
	public void setUp(){
		note=new ReleaseNoteImpl();
		id=312;
		title="version 1.0 Great title";
		String projectKey = "test";
		project = new DecisionKnowledgeProjectImpl(projectKey);
		content="<h1>verision</h2>&/=)(09(=)(=)&%kjhkjhaksdjlklöajlaksdfalsdj";
	}

	@Test
	public void testId() {
		note.setId(id);
		assertEquals(id,note.getId());
	}


	@Test
	public void testTitle() {
		note.setTitle(title);
		assertEquals(title,note.getTitle());
	}

	@Test
	public void testProject() {
		note.setProject(project);
		assertEquals(project, note.getProject());
		String projectKey2="test2";
		note.setProject(projectKey2);
		assertEquals(projectKey2, note.getProject().getProjectKey());
	}


	@Test
	public void testContent() {
		note.setContent(content);
		assertEquals(content, note.getContent());
	}

}