package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessorImpl;
import org.junit.Test;

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
    public void testLemmatizationWorksStandalone() {
        Preprocessor pp = new PreprocessorImpl();
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        assertEquals(pp.lemmatize(tokenizedTestSentence), new ArrayList<>());
    }

    @Test
    public void testReplaceUsingRegExWorks() {
        Preprocessor pp = new PreprocessorImpl();
        assertEquals("I put it on master branch and also linked it in the Marketplace.",
                pp.replaceUsingRegEx("I put it on master branch and also linked it in the Marketplace.\r\n\r\n", Preprocessor.WHITESPACE_CHARACTERS_PATTERN, ""));
    }

    @Test
    public void testPreprocessingWorks() {
        Preprocessor pp = new PreprocessorImpl();
        assertEquals(pp.preprocess(TestPreprocessorImpl.testSentence).size(), 8);
    }

}
