package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.extraction.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.PreprocessorImpl;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPreprocessorImpl extends TestSetUp {

    private static String testSentence = "The quick brown fox jumps over the lazy dog.";

    @Test
    public void testTokenizingWorks() {
        Preprocessor pp = new PreprocessorImpl();
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        assertEquals(pp.tokenize(TestPreprocessorImpl.testSentence), tokenizedTestSentence);
    }

    @Test
    public void testLemmatizingWorks() {
        Preprocessor pp = new PreprocessorImpl();
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        System.out.println(pp.preprocess(TestPreprocessorImpl.testSentence));
        assertEquals(pp.tokenize(TestPreprocessorImpl.testSentence), tokenizedTestSentence);
    }


}
