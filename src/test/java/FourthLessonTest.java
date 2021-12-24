import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FourthLessonTest {

    private static final String FILE_PATH = "nlp/text.txt";
    private static final String RESULT_DIRECTORY_PATH = "nlp/result/";
    private static final String TOKENS_PATH = RESULT_DIRECTORY_PATH + "tokens.txt";
    private static final String SENTENCES_PATH = RESULT_DIRECTORY_PATH + "sentences.txt";
    private static final String LEMMAS_PATH = RESULT_DIRECTORY_PATH + "lemmas.txt";

    /**
     * Задание. Сделать скрипт для анализа текстов.
     */
    @Test
    public void testNlp() throws IOException {
        FileWorker fileWorker = new FileWorker();
        NlpWorker nlpWorker = new NlpWorker();
        String text = fileWorker.getTextFromFile(FILE_PATH);
        fileWorker.createResultDirectory();
        fileWorker.writeToFile(fileWorker, TOKENS_PATH, nlpWorker.getTokensFromText(text));
        fileWorker.writeToFile(fileWorker, SENTENCES_PATH, nlpWorker.getSentences(text));
        fileWorker.writeToFile(fileWorker, LEMMAS_PATH, nlpWorker.getLemmasAndStemers(text));
    }


    private static class FileWorker {

        public String getTextFromFile(String filePath) throws IOException {
            File file = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder text = new StringBuilder();
            try {
                String line = bufferedReader.readLine();
                do {
                    text.append(line)
                            .append(System.lineSeparator());
                    line = bufferedReader.readLine();
                } while ((line != null));
            } finally {
                bufferedReader.close();
            }
            return text.toString();
        }

        public void writeToFile(String text, String filePath) throws IOException {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(text);
            bufferedWriter.close();
        }

        public void createResultDirectory() {
            new File("nlp/result").mkdir();
        }

        private void writeToFile(FileWorker worker, String filePath, String[] array) throws IOException {
            String sb = Arrays.stream(array).map(str -> str + "\n").collect(Collectors.joining());
            worker.writeToFile(sb, filePath);
        }

        private void writeToFile(FileWorker worker, String filePath, Map<String, String> map) throws IOException {
            StringBuilder sb = new StringBuilder();
            map.forEach((stemmer, lemma) -> sb.append(String.format("Stemmer: %s Lemma: %s\n", stemmer, lemma)));
            worker.writeToFile(sb.toString(), filePath);
        }

    }

    private static class NlpWorker {

        private final SentenceDetectorME sentenceDetectorME;
        private final TokenizerME tokenizerME;
        private final POSModel posModel;
        private final DictionaryLemmatizer dictionaryLemmatizer;

        public NlpWorker() throws IOException {
            InputStream is = new FileInputStream("nlp/bins/en-sent.bin");
            sentenceDetectorME = new SentenceDetectorME(new SentenceModel(is));
            InputStream inputStream = new FileInputStream("nlp/bins/en-token.bin");
            tokenizerME = new TokenizerME(new TokenizerModel(inputStream));
            InputStream inputStreamPOSTagger = new FileInputStream("nlp/bins/en-pos-maxent.bin");
            posModel = new POSModel(inputStreamPOSTagger);
            InputStream dictLemmatizer = new FileInputStream("nlp/bins/en-lemmatizer.dict");
            dictionaryLemmatizer = new DictionaryLemmatizer(dictLemmatizer);
        }


        public String[] getSentences(String text) {
            return sentenceDetectorME.sentDetect(text);
        }

        public String[] getTokensFromText(String text) {
            return tokenizerME.tokenize(text);
        }

        public Map<String, String> getLemmasAndStemers(String text) {
            Map<String, String> result = new TreeMap<>();
            POSTaggerME posTagger = new POSTaggerME(posModel);
            String[] tokens = getTokensFromText(text);
            String[] tags = posTagger.tag(tokens);
            String[] lemmas = dictionaryLemmatizer.lemmatize(tokens, tags);
            for (int i = 0; i < lemmas.length && i < tags.length; i++) {
                if (!lemmas[i].equals("O")) {
                    result.put(tokens[i], lemmas[i]);
                }
            }
            return result;
        }
    }
}
