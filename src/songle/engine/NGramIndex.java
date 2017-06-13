package songle.engine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates n-gram index for all dictionary terms
 * and provides closest words based on edit distance
 *
 * @author Team 6
 */
public class NGramIndex implements Serializable{
    private double jaccardThreshold;
    public Map<String, ArrayList<String>> ngramIndex;
    private String indexPath;

    public NGramIndex(){
        indexPath = "C:/Projects/RIT/KPT/songle2/index";
        jaccardThreshold = 0.5;
        ngramIndex = new HashMap<>();
    }

    /**
     * Generates n-grams for a given term and index the n-grams in a HashMap
     *
     * @param word the term to be processed and indexed
     */
    public void generateKgrams(String word){
        if (word.length() > 1){
            for (int i = 0; i < word.length()-1; i++) {
                String bigram = word.substring(i, i+2);
                ArrayList<String> termList = new ArrayList<>();

                if(ngramIndex.containsKey(bigram)){
                    termList = ngramIndex.get(bigram);

                    if(!termList.contains(word)){
                        termList.add(word);
                    }


                } else {
                    termList.add(word);
                }

                ngramIndex.put(bigram, termList);
            }
        }
    }

//    public void addNgram(String ngram, ArrayList<String> termList){
//        ngramIndex.put(ngram, termList);
//    }
    /**
     * Gets list of closest terms/words based on jaccard coefficient.
     * Computes edit distance for the terms whose score is greater than the threshold
     *
     * @param word the term to be corrected by closest words
     * @return
     */
    public ArrayList<String> getNearestWords(String word){
        ArrayList<String> nearestWords = new ArrayList<>();

        HashMap<String, Integer> matches = new HashMap<>();

        for (int i = 0; i < word.length()-1; i++) {
            String bigram = word.substring(i, i + 2);

            File f = new File(indexPath+"/ngrams/"+bigram+".ser");
//            if(ngramIndex.containsKey(bigram)){
            if(f.exists()){
//                ArrayList<String> termList = ngramIndex.get(bigram);
                ArrayList<String> termList = (ArrayList<String>) IndexHelper.loadData(f);

                for (String term: termList) {
                    int matchOcurrs = 1;
                    if (matches.containsKey(term)){
                        matchOcurrs = matches.get(term)+1;
                    }

                    matches.put(term, matchOcurrs);
                }
            }
        }

        for (HashMap.Entry<String, Integer> set: matches.entrySet()) {
            int matchOcurrs = set.getValue();
            int union = (((set.getKey().length() + word.length())-2) - matchOcurrs);
            double score = matchOcurrs * 1.0 / union;

            if(score >= jaccardThreshold && score < 1){
                nearestWords.add(set.getKey());
            }
        }

        return nearestWords;
    }

    /**
     * Computes edit distance using dynamic programming
     *
     * @param s1 original query term
     * @param s2 suggested corrected term
     * @return minimum edit distance distance between the terms
     */
    public int editDistance(String s1, String s2){
        int[][] m = new int[s1.length()+1][s2.length()+1];

        for (int i=1; i < m.length; i++){
            m[i][0] = i;
        }

        for (int j=1; j < m[0].length; j++){
            m[0][j] = j;
        }

        for (int i=1; i < m.length; i++){
            for (int j=1; j < m[i].length; j++){
                int insertD = m[i-1][j-1] + (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1);

                m[i][j] = Math.min(insertD, Math.min(m[i-1][j]+1, m[i][j-1]+1));
            }
        }

        return m[m.length-1][m[0].length-1];
    }

    /**
     * Returns the suggested term with the smallest edit distance score
     *
     * @param word original query term for which suggestions must be found
     * @return closest word, word with the smallest edit distance score
     */
    public String getSuggestion(String word){
        int lastScore = Integer.MAX_VALUE;
        String suggestion = "";
        ArrayList<String> overlapped = getNearestWords(word);
        for (String term: overlapped){
            int jscore = editDistance(word, term);
            if (jscore < lastScore){
                lastScore = jscore;
                suggestion = term;
            }
        }

        return !suggestion.isEmpty() ? suggestion : null;
    }

    /**
     * Creates an string representation of the NGramIndex object
     *
     * @return the generated string representing the NGramIndex object
     */
    public String toString(){
        String outString = "";
        for (Map.Entry<String, ArrayList<String>> set: ngramIndex.entrySet()) {
            outString += set.getKey()+":\t";

            for (String word: set.getValue()) {
                outString += word+", ";
            }
            outString += "\n";

        }
        return outString;
    }
}
