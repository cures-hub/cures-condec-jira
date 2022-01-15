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
	}

	@Test
	@NonTransactional
	public void testFulfillsDoD() {
		codeCheck = new CodeCheck(CodeFiles.getTestCodeFileDone());
		assertTrue(codeCheck.isDefinitionOfDoneFulfilled());
		assertFalse(codeCheck.getCoverageQuality(new FilterSettings()).isCriterionViolated());
		assertTrue(codeCheck.getQualityCheckResult(new DefinitionOfDone()).isEmpty());
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		codeCheck = new CodeCheck(CodeFiles.getCodeFileNotDone());
		assertTrue(codeCheck.getCoverageQuality(new FilterSettings()).isCriterionViolated());
	}

	@Test
	@NonTransactional
	public void testSmallFile() {
		codeCheck = new CodeCheck(CodeFiles.getSmallCodeFileDone());
		assertFalse(codeCheck.getCoverageQuality(new FilterSettings()).isCriterionViolated());
	}

	@Test
	@NonTransactional
	public void testNoCodeKnowledgeType() {
		codeCheck = new CodeCheck(KnowledgeElements.getTestKnowledgeElement());
		assertTrue(codeCheck.getCoverageQuality(new FilterSettings()).isCriterionViolated());
	}

}