package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

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
		ChangedFile file = extract.getCodeClasses().get(0);
		Set<String> list = new HashSet<>();
		KnowledgeElement element = extract.createKnowledgeElementFromFile(file, list);
		assertNotNull(element);
		assertEquals(element.getSummary(), file.getName());
	}

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFileFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClasses());
		assertFalse(extract.getCodeClasses().isEmpty());
		Set<String> list = new HashSet<>();
		assertNull(extract.createKnowledgeElementFromFile(null, list));
	}

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFileKeysNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		assertNotNull(extract.getCodeClasses());
		assertFalse(extract.getCodeClasses().isEmpty());
		ChangedFile file = extract.getCodeClasses().get(0);
		assertNull(extract.createKnowledgeElementFromFile(file, null));
	}

	@Test
	@NonTransactional
	public void testCreateKnowledgeElementFromFileProjectKeyNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor(null);
		ChangedFile file = new ChangedFile("somePath");
		Set<String> list = new HashSet<>();
		list.add("Test");
		assertNull(extract.createKnowledgeElementFromFile(file, list));
	}

}
