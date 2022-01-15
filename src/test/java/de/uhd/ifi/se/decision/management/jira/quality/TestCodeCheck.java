package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.CodeFiles;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCheck extends TestSetUp {

	private CodeCheck codeCheck;

	@Before
	public void setUp() {
		init();
		codeCheck = new CodeCheck();
	}

	@Test
	@NonTransactional
	public void testFulfillsDoD() {
		assertTrue(codeCheck.execute(CodeFiles.getTestCodeFileDone()));
		assertFalse(codeCheck.getCoverageQuality(CodeFiles.getTestCodeFileDone(), new FilterSettings())
				.isCriterionViolated());
		assertTrue(codeCheck.getQualityCheckResult(CodeFiles.getTestCodeFileDone(), new DefinitionOfDone()).isEmpty());
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertTrue(codeCheck.getCoverageQuality(CodeFiles.getCodeFileNotDone(), new FilterSettings())
				.isCriterionViolated());
	}

	@Test
	@NonTransactional
	public void testSmallFile() {
		assertFalse(codeCheck.getCoverageQuality(CodeFiles.getSmallCodeFileDone(), new FilterSettings())
				.isCriterionViolated());
	}

	@Test
	@NonTransactional
	public void testNoCodeKnowledgeType() {
		assertTrue(codeCheck.getCoverageQuality(KnowledgeElements.getTestKnowledgeElement(), new FilterSettings())
				.isCriterionViolated());
	}

}