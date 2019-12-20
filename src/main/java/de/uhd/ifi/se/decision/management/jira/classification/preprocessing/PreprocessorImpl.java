package de.uhd.ifi.se.decision.management.jira.classification.preprocessing;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreprocessorImpl implements Preprocessor {

    public static String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory()
            .getAbsolutePath() + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

    private Tokenizer tokenizer;
    private Lemmatizer lemmatizer;
    private POSTaggerME tagger;
    private PreTrainedGloveSingleton glove;
    private String[] posTags;
    //private NameFinderME nameFinder;
    private Integer nGramN;

    public PreprocessorImpl() {
        File lemmatizerFile = new File(PreprocessorImpl.DEFAULT_DIR + "lemmatizer.dict");
        File tokenizerFile = new File(PreprocessorImpl.DEFAULT_DIR + "token.bin");
        File posFile = new File(PreprocessorImpl.DEFAULT_DIR + "pos.bin");
        this.nGramN = 3;

        try {
            InputStream lemmatizerModelIn = new FileInputStream(lemmatizerFile);
            this.lemmatizer = new DictionaryLemmatizer(lemmatizerModelIn);
            //lemmatizerModel = new LemmatizerModel(modelIn);

            InputStream tokenizerModelIn = new FileInputStream(tokenizerFile);
            TokenizerModel tokenizerModel = new TokenizerModel(tokenizerModelIn);
            this.tokenizer = new TokenizerME(tokenizerModel);

            InputStream posModelIn = new FileInputStream(posFile);
            POSModel posModel = new POSModel(posModelIn);
            this.tagger = new POSTaggerME(posModel);

            //modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "person.bin");
            //TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            //this.nameFinder = new NameFinderME(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.glove = PreTrainedGloveSingleton.getInstance();

    }


    @Override
    public List<String> tokenize(String sentence) {
        return Arrays.asList(this.tokenizer.tokenize(sentence));
    }

    @Override
    public String replaceUsingRegEx(String sentence, String regex, String replaceToken) {
        return sentence.replaceAll(regex, replaceToken);
    }

    @Override
    public List<String> lemmatize(List<String> tokens) {
        try {
            List<String> lemmatizedTokens = Arrays.asList(this.lemmatizer.lemmatize(
                    tokens.toArray(new String[tokens.size()]),
                    this.posTags
            ));
            // Unknown words are replaced by "O" by the lemmatizer.
            // To give the methode for mapping words to numbers a
            // chance we leave unknown words as is.
            // e.g.: tokens: {"jira" "does" "not" "work"}
            // --lemmatize--> {"O" "do" "not" "work"}
            // --next line returns--> {"jira" "do" "not" "work"}
            return IntStream.range(0, lemmatizedTokens.size())
                    .mapToObj(i -> lemmatizedTokens.get(i).equals("O") ? tokens.get(i) : lemmatizedTokens.get(i))
                    .collect(Collectors.toList());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List generateNGram(List tokens, Integer N) {
        List<List<Double>> nGrams = new ArrayList<>();
        for (int i = 0; i < tokens.size() - N + 1; i++)
            nGrams.add(concat(tokens, i, i + N));
        return nGrams;
    }

    private List concat(List tokens, int start, int end) {
        List gram = new ArrayList();
        for (int i = start; i < end; i++)
            gram.addAll((List<Double>) tokens.get(i));
        return gram;
    }

    @Override
    public List convertToNumbers(List<String> tokens) {
        List<List<Double>> numberTokens = new ArrayList<>();
        for (String wordToken : tokens) {
            numberTokens.add(glove.getWordVector(wordToken));
        }
        return numberTokens;
    }

    public String[] calculatePosTags(List<String> tokens){
        return this.tagger.tag(Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class));
    }


    @Override
    public synchronized List preprocess(String sentence) {
        String cleaned_sentence = this.replaceUsingRegEx(sentence, Preprocessor.NUMBER_PATTERN, Preprocessor.NUMBER_TOKEN.toLowerCase());
        cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, Preprocessor.URL_PATTERN, Preprocessor.URL_TOKEN.toLowerCase());
        cleaned_sentence = this.replaceUsingRegEx(cleaned_sentence, Preprocessor.WHITESPACE_CHARACTERS_PATTERN, Preprocessor.WHITESPACE_CHARACTERS_TOKEN.toLowerCase());
        //replace long words and possible methods!
        cleaned_sentence = cleaned_sentence.toLowerCase();

        List<String> tokens = this.tokenize(cleaned_sentence);


        this.posTags = this.calculatePosTags(tokens);

        /* TODO: if time is sufficient
        Span[] spans = this.nameFinder.find((String[]) tokens.toArray());
        for (Span span : spans) {
            span.getType();

        }
        this.nameFinder.clearAdaptiveData();
         */

        tokens = this.lemmatize(tokens);

        List<List<Double>> numberTokens = this.convertToNumbers(tokens);

        return this.generateNGram(numberTokens, this.nGramN);
    }

    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }
}

