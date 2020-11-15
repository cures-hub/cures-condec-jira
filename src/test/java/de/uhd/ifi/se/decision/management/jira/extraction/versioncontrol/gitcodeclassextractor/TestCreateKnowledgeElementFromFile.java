package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCreateKnowledgeElementFromFile extends TestSetUpGit {

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFile() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClasses());
		assertFalse(extract.getCodeClasses().isEmpty());
		ChangedFile file = extract.getCodeClasses().get(1);
		KnowledgeElement element = extract.createKnowledgeElementFromFile(file);
		assertNotNull(element);
		assertEquals("GodClass.java", element.getSummary());
		assertEquals(0, file.getJiraIssueKeys().size());
	}

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFileFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClasses());
		assertFalse(extract.getCodeClasses().isEmpty());
		assertNull(extract.createKnowledgeElementFromFile(null));
	}

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFileProjectKeyNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor(null);
		ChangedFile file = new ChangedFile("somePath");
		assertNull(extract.createKnowledgeElementFromFile(file));
	}
}