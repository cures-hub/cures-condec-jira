package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import de.uhd.ifi.se.decision.management.jira.extraction.Preprocessor;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.apache.commons.lang.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PreprocessorImpl implements Preprocessor {

    public static String LANGUAGE_MODEL_PATH = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
            .getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator
            + "language_models" + File.separator;

    private Tokenizer tokenizer;
    private Lemmatizer lemmatizer;
    private POSTaggerME tagger;
    private String[] posTags;

    public PreprocessorImpl(Tokenizer tokenizer, Lemmatizer lemmatizer, POSTaggerME tagger) {
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.lemmatizer = lemmatizer;
    }


    public PreprocessorImpl() {
        LemmatizerModel lemmatizerModel = null;
        TokenizerModel tokenizerModel = null;
        InputStream modelIn = null;
        POSModel posModel = null;
        try {
            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "lemmatizer.bin");
            lemmatizerModel = new LemmatizerModel(modelIn);

            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "token.bin");
            tokenizerModel = new TokenizerModel(modelIn);

            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "pos.bin");
            posModel = new POSModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tokenizer = new TokenizerME(tokenizerModel);
        this.lemmatizer = new LemmatizerME(lemmatizerModel);
        this.tagger = new POSTaggerME(posModel);

    }


    @Override
    public List tokenize(String sentence) {
        return Arrays.asList(this.tokenizer.tokenize(sentence));
    }

    @Override
    public String removeUsingRegEx(String sentence, Pattern regex) {
        return null;
    }

    @Override
    public List<String> lemmatize(List<String> tokens) {
        return Arrays.asList(this.lemmatizer.lemmatize((String[]) tokens.toArray(), this.posTags));
    }

    @Override
    public String lemmatize(String token) {
        throw new NotImplementedException();
    }

    @Override
    public List generateNGram(List tokens, Integer N) {
        List<String> nGrams = new ArrayList<String>();
        for (int i = 0; i < tokens.size() - N + 1; i++)
            nGrams.add(concat(tokens, i, i + N));
        return nGrams;
    }

    private String concat(List<String> tokens, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + tokens.get(i));
        return sb.toString();
    }

    @Override
    public List convertToNumbers(List<String> tokens) {
        return null;
    }

    @Override
    public Double convertToNumbers(String token) {
        return null;
    }

    @Override
    public List<Double> preprocess(String sentence) {
        List<String> tokens = this.tokenize(sentence);
        this.posTags = this.tagger.tag((String[]) tokens.toArray());
        return null;
    }
}
