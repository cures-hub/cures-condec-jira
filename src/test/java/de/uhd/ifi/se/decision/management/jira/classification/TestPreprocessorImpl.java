package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessorImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPreprocessorImpl extends TestSetUp {

    private static String testSentence = "The quick brown fox jumps over the lazy dog.";

    public static final String PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator
            + "classifier" + File.separator;

    private Preprocessor pp;

    @Before
    public void setUp() {
        init();
        this.pp = new PreprocessorImpl(
                new File(PATH + "lemmatizer.dict"),
                new File(PATH + "token.bin"),
                new File(PATH + "pos.bin"),
                new File(PATH + "glove.6b.50d.csv")
        );
    }

    @Test
    public void testTokenizingWorks() {
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        assertEquals(pp.tokenize(TestPreprocessorImpl.testSentence), tokenizedTestSentence);
    }

    @Test
    public void testLemmatizationWorksStandalone() {
        List<String> tokenizedTestSentence = Arrays.asList("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", ".");
        assertEquals(pp.lemmatize(tokenizedTestSentence), new ArrayList<>());
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
