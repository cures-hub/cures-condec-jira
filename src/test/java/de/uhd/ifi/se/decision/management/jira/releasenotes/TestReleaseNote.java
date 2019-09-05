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
	private String projectKey;
	private String content;
	@Before
	public void setUp(){
		note=new ReleaseNoteImpl(null,null,null);
		id=312;
		title="version 1.0 Great title";
		projectKey = "test";
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
		note.setProjectKey(projectKey);
		assertEquals(projectKey, note.getProjectKey());
		String projectKey2="test2";
		note.setProjectKey(projectKey2);
		assertEquals(projectKey2, note.getProjectKey());
	}


	@Test
	public void testContent() {
		note.setContent(content);
		assertEquals(content, note.getContent());
	}

}