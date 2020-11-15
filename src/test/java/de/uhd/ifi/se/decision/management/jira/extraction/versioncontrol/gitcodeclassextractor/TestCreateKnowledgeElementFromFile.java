package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.gitcodeclassextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
		KnowledgeElement element = file;
		assertNotNull(element);
		assertEquals("GodClass.java", element.getSummary());
		assertEquals(0, file.getJiraIssueKeys().size());
	}
}