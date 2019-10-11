package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessorImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPreprocessorImpl extends TestSetUp {

    private static final String testSentence = "The quick brown fox jumps over the lazy dog.";

    private PreprocessorImpl pp;

    @Before
    public void setUp() {
        init();
        initClassifierPaths();
        pp = new PreprocessorImpl();
    }

    @Test
    public void testTokenizingWorks() {
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        assertEquals(pp.tokenize(TestPreprocessorImpl.testSentence), tokenizedTestSentence);
    }

    @Test
    public void testLemmatizationWorksStandalone() {
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        pp.setPosTags(pp.calculatePosTags(tokenizedTestSentence));
        assertEquals(Arrays.asList("the", "quick", "brown", "fox", "jump", "over", "the", "lazy", "dog", "."),
                pp.lemmatize(tokenizedTestSentence));
    }

    @Test
    public void testReplaceUsingRegExWorks() {
        assertEquals("I put it on master branch and also linked it in the Marketplace.",
                pp.replaceUsingRegEx("I put it on master branch and also linked it in the Marketplace.\r\n\r\n", Preprocessor.WHITESPACE_CHARACTERS_PATTERN, ""));
    }

    @Test
    public void testPreprocessingWorks() {
        assertEquals(pp.preprocess(TestPreprocessorImpl.testSentence).size(), 8);
    }

}
