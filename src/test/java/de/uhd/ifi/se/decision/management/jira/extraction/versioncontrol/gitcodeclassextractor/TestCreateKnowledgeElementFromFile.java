package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitCodeClassExtractor;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TestCreateKnowledgeElementFromFile {

	@Test
	public void testCreateKnowledgeElementFromFile() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		List<String> list = extract.getIssuesKeysForFile(extract.getCodeClassFiles().get(0));
		File file = extract.getCodeClassFiles().get(0);
		KnowledgeElement element = extract.createKnowledgeElementFromFile(file, list);
		assertNotNull(element);
		assertEquals(element.getSummary(), file.getName());
	}

	@Test
	public void testCreateKnowledgeElementFromFileFileNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		List<String> list = extract.getIssuesKeysForFile(extract.getCodeClassFiles().get(0));
		assertNull(extract.createKnowledgeElementFromFile(null, list));
	}

	@Test
	public void testCreateKnowledgeElementFromFileKeysNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor("TEST");
		File file = extract.getCodeClassFiles().get(0);
		assertNull(extract.createKnowledgeElementFromFile(file, null));
	}

	@Test
	public void testCreateKnowledgeElementFromFileProjectKeyNull() {
		GitCodeClassExtractor extract = new GitCodeClassExtractor(null);
		File file = new File("somePath");
		List<String> list = new ArrayList<>();
		list.add("Test");
		assertNull(extract.createKnowledgeElementFromFile(file, list));

	}


}
