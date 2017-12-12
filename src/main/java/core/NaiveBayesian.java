package core;

import configuration.Category;
import entity.Sentiment;
import entity.Word;
import entity.WordValue;

import java.util.*;
import java.util.logging.Logger;

public interface NaiveBayesian {
     final static Logger LOGGER_MAPPER = Logger.getLogger(Mapper.class.getName());
     final static Logger LOGGER_REDUCER = Logger.getLogger(Reducer.class.getName());

     Sentiment negative = new Sentiment(Category.NEGATIVE_NAME,Category.NEGATIVE_LABEL);
     Sentiment neutral = new Sentiment(Category.NEUTRAL_NAME,Category.NEUTRAL_LABEL);
     Sentiment positive = new Sentiment(Category.POSITIVE_NAME,Category.POSITIVE_LABEL);


    class Mapper extends MapReduce.Mapper<Word>{
        static long startTime = System.currentTimeMillis();
        public List<Word> map(String data) {
            LOGGER_MAPPER.info(this.getClass().getCanonicalName());
            LOGGER_MAPPER.info("Mapper Started");
            List<Word> words = new ArrayList();
            String line = data.toString();
            String[] sentences=line.split("\n");
            LOGGER_MAPPER.info("Data training : "+String.valueOf(sentences.length)+" tweets");
            LOGGER_MAPPER.info("Parsing to sentiment and sentence");
            for(String sentence: sentences ) {
                String[] subsentence = sentence.split(":");
                subsentence[1] = Reducer.removeUnusedCharacter(subsentence[1]);
                Sentiment sentiment = null;
                String s = subsentence[0].toLowerCase();
                if (s.equalsIgnoreCase(Category.NEGATIVE_NAME)) {
                    sentiment = negative;
                } else if (s.equalsIgnoreCase(Category.POSITIVE_NAME)) {
                    sentiment = positive;
                } else if (s.equalsIgnoreCase(Category.NEUTRAL_NAME)) {
                    sentiment = neutral;
                }
                words.add(new Word(sentiment,subsentence[1].toLowerCase()));
            }
            LOGGER_MAPPER.info("Parsing from sentence to bag of words");
            List<Word> result = new ArrayList<Word>();
            for(Word word: words){
                String[] wordparsers = word.getContent().split("\\s+");
                for(String wordparser : wordparsers ){
                    if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEGATIVE_NAME)) {
                        Reducer.sum_negative++;
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.POSITIVE_NAME)) {
                        Reducer.sum_positive++;
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEUTRAL_NAME)) {
                        Reducer.sum_neutral++;
                    }
                    result.add(new Word(word.getSentiment(),wordparser));
                }
            }
            LOGGER_MAPPER.info("Total word parsed : "+String.valueOf(result.size()));
            return result;
        }
    }

    class Reducer extends MapReduce.Reducer<String, WordValue, Word>{
        static int sum_negative, sum_positive, sum_neutral;
        static long endTime = System.currentTimeMillis();
        public Map<String, WordValue> reduce(List<Word> data) {
            LOGGER_REDUCER.info("Reducer Started");
            Map<String, WordValue> result = new HashMap<String, WordValue>();
            LOGGER_REDUCER.info("Grouping for same word and counting sentiment for each words");
            for(Word word : data){
                if(!result.containsKey(word.getContent())){
                    WordValue wordValue = null;
                    if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEGATIVE_NAME)) {
                        wordValue = new WordValue(1,0,0);
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.POSITIVE_NAME)) {
                        wordValue = new WordValue(0,1,0);
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEUTRAL_NAME)) {
                        wordValue = new WordValue(0,0,1);
                    }
                    result.put(word.getContent(),wordValue);
                }else{
                    WordValue wordValue = result.get(word.getContent());
                    if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEGATIVE_NAME)) {
                        wordValue.setNegative(wordValue.getNegative()+1);
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.POSITIVE_NAME)) {
                        wordValue.setPositive(wordValue.getPositive()+1);
                    } else if (word.getSentiment().getDescription().equalsIgnoreCase(Category.NEUTRAL_NAME)) {
                        wordValue.setNeutral(wordValue.getNeutral()+1);
                    }
                    result.put(word.getContent(),wordValue);
                }
            }
            LOGGER_REDUCER.info("Word counted : "+String.valueOf(result.size()));
            return result;
        }
        public static float accuracy = 0;
        public List<Word> calculate(String content, Map<String, WordValue> training){
            LOGGER_REDUCER.info("Calculating for each word probability sentiment");
            float total = Reducer.sum_negative+Reducer.sum_neutral+Reducer.sum_positive;
            List<Word> result = new ArrayList<Word>();
            float true_result = 0;
            float neg_sent_prob = Reducer.sum_negative/(total);
            float pos_sent_prob = Reducer.sum_positive/(total);
            float neu_sent_prob = Reducer.sum_neutral/(total);

            String line = content.toString();
            String[] sentences=line.split("\n");

            LOGGER_REDUCER.info("Data testing : "+String.valueOf(sentences.length)+" tweet");

            for(String sentence: sentences ) {
                String[] subsentence = sentence.split(":");
                subsentence[1] = removeUnusedCharacter(subsentence[1]);
                String[] words = subsentence[1].split(" ");

                float neg_prob = 1;
                float pos_prob = 1;
                float neu_prob = 1;

                for(String word : words){
                    WordValue wordValue = training.get(word);
                    if(wordValue==null){
                        neg_prob*=1;
                        pos_prob*=1;
                        neu_prob*=1;
                    }else{
                        neg_prob *= (float)wordValue.getNegative()/Reducer.sum_negative;
                        pos_prob *= (float)wordValue.getPositive()/Reducer.sum_positive;
                        neu_prob *= (float)wordValue.getNeutral()/Reducer.sum_neutral;
                    }

                }
                neg_prob *= neg_sent_prob;
                pos_prob *= pos_sent_prob;
                neu_prob *= neu_sent_prob;
                Sentiment sentiment = (neg_prob > pos_prob && neg_prob > neu_prob) ? negative :
                        (pos_prob > neu_prob) ? positive : neutral;
                true_result = sentiment.getDescription().equalsIgnoreCase(subsentence[0]) ? ++true_result : true_result;
                result.add(new Word(sentiment, subsentence[0]+":"+subsentence[1]));
            }
            accuracy = (true_result/(sentences.length+1)*100);
            LOGGER_REDUCER.info("Classification done.");
            LOGGER_REDUCER.info("Accuracy : "+accuracy+" %");
            LOGGER_REDUCER.info((int)true_result+" correct classification from "+String.valueOf(sentences.length)+" sentence");
            return result;
        }
        public static String removeUnusedCharacter(String data){
            String result = data;
            result.replaceAll("\\?","");
            result.replaceAll("!","");
            result.replaceAll("#","");
            result.replaceAll("&amp","");
            result.replaceAll("\\;","");
            return result;
        }
    }



}