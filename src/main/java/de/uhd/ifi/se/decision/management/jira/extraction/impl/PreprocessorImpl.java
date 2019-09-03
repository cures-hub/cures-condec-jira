package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import de.uhd.ifi.se.decision.management.jira.extraction.Preprocessor;
import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.Lemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PreprocessorImpl implements Preprocessor {

    public static String LANGUAGE_MODEL_PATH = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "classifier" + File.separator
            + "language_models" + File.separator;

    private Tokenizer tokenizer;
    private Lemmatizer lemmatizer;
    private POSTaggerME tagger;
    private String[] posTags;
    private NameFinderME nameFinder;
    private Integer nGramN;

    public PreprocessorImpl(Tokenizer tokenizer, Lemmatizer lemmatizer, POSTaggerME tagger, NameFinderME nameFinder) {
        this.tokenizer = tokenizer;
        this.tagger = tagger;
        this.lemmatizer = lemmatizer;
        this.nameFinder = nameFinder;
        this.nGramN = 3;
    }


    public PreprocessorImpl() {
        InputStream lemmatizerModel = null;
        TokenizerModel tokenizerModel = null;
        InputStream modelIn = null;
        POSModel posModel = null;
        this.nGramN = 3;
        try {
            lemmatizerModel = new FileInputStream(LANGUAGE_MODEL_PATH + "lemmatizer.dict");
            this.lemmatizer = new DictionaryLemmatizer(lemmatizerModel);
            //lemmatizerModel = new LemmatizerModel(modelIn);

            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "token.bin");
            tokenizerModel = new TokenizerModel(modelIn);
            this.tokenizer = new TokenizerME(tokenizerModel);

            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "pos.bin");
            posModel = new POSModel(modelIn);
            this.tagger = new POSTaggerME(posModel);

            modelIn = new FileInputStream(LANGUAGE_MODEL_PATH + "person.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            this.nameFinder = new NameFinderME(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return Arrays.asList(this.lemmatizer.lemmatize(
                Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class),
                this.posTags
        ));
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
            gram.add(tokens.get(i));
        return gram;
    }

    @Override
    public List convertToNumbers(List<String> tokens) {
        List<List<Double>> numberTokens = new ArrayList<>();
        PreTrainedGlove glove = PreTrainedGlove.getInstance();
        for (String wordToken : tokens) {
            //TODO: only add if word is known!
            numberTokens.add(glove.getWordVector(wordToken));
        }
        //DO NOT FLATTEN!!!!! this needs to happen somewhere else!
        return numberTokens;
                /*
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

                 */
    }


    @Override
    public List<Double> preprocess(String sentence) {
        sentence = sentence.toLowerCase();
        sentence = this.replaceUsingRegEx(sentence, Preprocessor.NUMBER_PATTERN, Preprocessor.NUMBER_TOKEN.toLowerCase());
        sentence = this.replaceUsingRegEx(sentence, Preprocessor.URL_PATTERN, Preprocessor.URL_TOKEN.toLowerCase());
        sentence = this.replaceUsingRegEx(sentence, Preprocessor.WHITESPACE_CHARACTERS_PATTERN, Preprocessor.WHITESPACE_CHARACTERS_TOKEN.toLowerCase());

        List<String> tokens = this.tokenize(sentence);


        this.posTags = this.tagger.tag(Arrays.copyOf(tokens.toArray(), tokens.size(), String[].class));

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
}

class PreTrainedGlove {

    private static PreTrainedGlove instance;
    private Map<String, Double[]> map;
    private static String GLOVE_FILE_PATH = "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "classifier" + File.separator
            + "language_models" + File.separator;
    ;

    private PreTrainedGlove(Integer dimensions) {
        this.map = new HashMap<>();
        String fullFilename = GLOVE_FILE_PATH + "glove.6b." + dimensions + "d.csv";
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fullFilename),
                StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(" ");

                List<Double> vector = new ArrayList<Double>();
                //Skip first entry because that is the word.
                for (int i = 1; i < attributes.length; i++) {
                    vector.add(Double.parseDouble(attributes[i]));
                }
                map.put(attributes[0], Arrays.copyOf(vector.toArray(), vector.size(), Double[].class));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private PreTrainedGlove() {
        this(50);
    }

    public static PreTrainedGlove getInstance() {
        if (PreTrainedGlove.instance == null) {
            PreTrainedGlove.instance = new PreTrainedGlove();
        }
        return PreTrainedGlove.instance;
    }

    public static PreTrainedGlove getInstance(Integer dimensions) {
        if (PreTrainedGlove.instance == null) {
            PreTrainedGlove.instance = new PreTrainedGlove(dimensions);
        }
        return PreTrainedGlove.instance;
    }

    List<Double> getWordVector(String word) {
        try{
            return Arrays.asList(this.map.get(word));
        } catch (Exception e){
            return new ArrayList<>();
        }

    }
}

