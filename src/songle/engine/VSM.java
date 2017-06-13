package songle.engine;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;

/**
 * Vector Space Model
 *
 * @author Team 6
 */
public class VSM implements Serializable{

    public Map<String, ArrayList<Doc>> indexMap;
    private NGramIndex ngramIndex;
    public HashMap<Integer, String[]> documents;
    public double[] docLength;
    private String[] lastQuery;
    private String[] querySuggestion;
    private HashMap<String, String> correctedTerms;
    public String splitPattern;
    private String indexPath;
    private String searchGenre;
    public int fullSearchSize;
    public HashMap<Integer, ArrayList<String>> clusters;

    /**
     * Constructs vsm object and initializes class properties
     *
     * @param indexFolder
     */
    public VSM(String indexFolder){
        indexPath = indexFolder;//"C:/Projects/RIT/KPT/songle2/index";
        splitPattern = "[^a-zA-Z0-9']+";//"[ ¡=~`@#_\\^\\/'\\n\\r\":;,.?!$%()\\-\\*\\+\\[\\]\\{\\}]+";
        indexMap = new HashMap<>();
        ngramIndex = new NGramIndex();
        //String filePath = "data/lyrics.csv";
        documents = new HashMap<>();
        //indexDocuments(filePath);

        ///clusters = (HashMap<Integer, ArrayList<String>>) IndexHelper.loadData(new File(indexFolder+"/clusters.ser"));
    }

    public HashMap<Integer, ArrayList<String>> getClusters(){
        clusters = (HashMap<Integer, ArrayList<String>>) IndexHelper.loadData(new File(indexPath+"/clusters.ser"));
        return clusters;
    }


    /**
     * Create index of documentIds and terms.
     * Also creates n-gram index for spelling correction and tolerant retrieval

     * @param filePath
     * @see NGramIndex#generateKgrams(String)
     *
     */
    public void indexDocuments(String filePath) {
        try{
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String [] nextLine;
            try{
                int i = 0;
                String[] header = reader.readNext();
                while ((nextLine = reader.readNext()) != null) {
                    if(nextLine[5].trim().equals("")) continue;
                    i++;

                    String[] resultSet = {nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4]};
                    documents.put(i, resultSet);
                    String lyrics = nextLine[1]+" "+nextLine[5];
                    String[] terms = lyrics.split(splitPattern);//.split(" ");

                    //dumps lyric content for future usage
                    FileWriter fw = new FileWriter(IndexHelper.indexPath+"/lyrics/"+nextLine[0]+".txt");
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(lyrics);
                    bw.close();

                    for (int j=0; j < terms.length; j++){
                        if (terms[j].trim().equals("")) continue;

                        Doc doc = new Doc(i);
                        doc.genre = nextLine[4];
                        String term = terms[j];
                        term = term.toLowerCase();
                        ngramIndex.generateKgrams(term);

                        if(!indexMap.containsKey(term)){
                            ArrayList<Doc> docList = new ArrayList<>();
                            docList.add(doc);
                            indexMap.put(term, docList);

                        } else {
                            ArrayList<Doc> docList = indexMap.get(term);

                            Comparator<Doc> c = new Comparator<Doc>() {
                                public int compare(Doc d1, Doc d2) {
                                    return new Integer(d1.docId).compareTo(d2.docId);
                                }
                            };

                            int index = Collections.binarySearch(docList, doc, c);
                            if (index < 0){
                                docList.add(doc);
                                indexMap.put(term, docList);
                            } else {
                                doc = docList.get(index);
                                doc.tw += 1;
                                doc.insertPosition(j);
                            }

                        }
                    }
                }

                int N = i;
                docLength = new double[N+1];
                for (ArrayList<Doc> docList: indexMap.values()) {

                    for (Doc doc : docList) {
                        double tfidf = (1 + Math.log10(doc.tw)) * Math.log10(N * 1.0/ docList.size());
                        doc.tw = tfidf;
                        docLength[doc.docId] += Math.pow(tfidf, 2);
                    }
                }

                for(int j=0;j<N;j++) {
                    docLength[j] = Math.sqrt(docLength[j]);
                }

            } catch (IOException e){
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dumps created indexes to a physical location for future usage
     *
     */
    public void dumpIndexes(){

        for (Map.Entry<String, ArrayList<Doc>> set: indexMap.entrySet()) {

            IndexHelper.dumpFile(IndexHelper.indexPath+"/vsm/" + set.getKey() + ".ser", set.getValue());
        }

        for (Map.Entry<String, ArrayList<String>> set: ngramIndex.ngramIndex.entrySet()) {
            IndexHelper.dumpFile(IndexHelper.indexPath+"/ngrams/" + set.getKey() + ".ser", set.getValue());
        }

        IndexHelper.dumpFile(IndexHelper.indexPath+"/vsm/l2Norm.ser", docLength);

        //HashMap<Integer, String[]>
        for (Map.Entry<Integer, String[]> set: documents.entrySet()) {
            IndexHelper.dumpFile(IndexHelper.indexPath+"/documents/" + set.getKey() + ".ser", set.getValue());
        }
    }

    /**
     * Searches for documents withing an specific genre
     * @param textQuery
     * @param genre
     * @return
     */
    public List<Result> search(String textQuery, int start, int limit, String genre) {
        searchGenre = genre;
        return search(textQuery, start, limit);
    }

    /**
     * Performs phrase query search and ranked search and combine the results
     * @param textQuery
     * @return a list of result objects
     */
    public List<Result> search(String textQuery, int start, int limit) {

        List<Result> posQueryResults = phraseQuery(textQuery);
        List<Result> rankedResults = rankSearch(textQuery);

        if(posQueryResults.size()>0) {
            for (Result rankedRes: rankedResults) {

                boolean duplicated = false;
                for(Result posRes: posQueryResults){
                    if (rankedRes.docId == posRes.docId) {
                        duplicated = true;
                    }
                }

                if(!duplicated) posQueryResults.add(rankedRes);

            }
        } else {
            posQueryResults = rankedResults;
        }

        fullSearchSize = posQueryResults.size();
        posQueryResults = fullSearchSize > limit ? posQueryResults.subList(start, limit) : posQueryResults;

        for (Result res: posQueryResults) {
            res.lyrics = IndexHelper.loadLyrics(IndexHelper.indexPath+"/lyrics/"+res.songId+".txt");
        }

        return posQueryResults;
    }

    /**
     * Search for documents in the created VSM index that matches a certain query
     *
     * @param textQuery
     * @return a list of result objects
     */
    public List<Result> rankSearch(String textQuery) {

        correctedTerms = new HashMap<>();
        String[] query = textQuery.split(splitPattern);
        HashMap<Integer, Result> results = new HashMap<>();

        docLength = (double[]) IndexHelper.loadData(new File(indexPath+"/vsm/l2Norm.ser"));
        int N = docLength.length;
        ArrayList<Doc> docList;

        //get term frequency for query
        HashMap<String, Double> qTf = new HashMap<>();
        for(int i=0; i<query.length; i++){
            String term = query[i].toLowerCase();
            query[i] = term;
            if(qTf.containsKey(term)){
                qTf.put(term, qTf.get(term)+1);
            } else {
                qTf.put(term, 1.0);
            }
        }

        lastQuery = query;

        //length normalization for query
        double queryL2Norm = 0.0;
        for (Map.Entry<String, Double> set: qTf.entrySet()) {
            String term = set.getKey();

            File f = new File(indexPath+"/vsm/"+term+".ser");
            if(f.exists()) {
                docList = (ArrayList<Doc>) IndexHelper.loadData(f);
                double queryTfidf = 1 + Math.log10(set.getValue()) * Math.log10(N * 1.0 / docList.size());
                set.setValue(queryTfidf);
                queryL2Norm += Math.pow(queryTfidf, 2);
                indexMap.put(term, docList);
            }
        }
        queryL2Norm = Math.sqrt(queryL2Norm);


        for (Map.Entry<String, Double> set: qTf.entrySet()) {
            //
            String term = set.getKey();
            Double qtfidf = set.getValue();
            if (!indexMap.containsKey(term)){
                String suggestedTerm = ngramIndex.getSuggestion(term);
                if(suggestedTerm != null){
                    correctedTerms.put(term, suggestedTerm);
                } //? suggestedTerm : term;
            }

            if (indexMap.containsKey(term)){
                docList = indexMap.get(term);
                for (Doc doc: docList) {

                    if(searchGenre == null || doc.genre.equals(searchGenre)){
                        double normalizedTfidf = doc.tw / docLength[doc.docId];
                        double normalizedQueryTfidf = qtfidf / queryL2Norm;
                        double score = normalizedTfidf * normalizedQueryTfidf;

                        if (!results.containsKey(doc.docId)){
                            String[] attributes = (String[]) IndexHelper.loadData(new File(indexPath+"/documents/" + doc.docId + ".ser"));//documents.get(doc.docId);
                            Result resDoc = new Result();
                            resDoc.docId = doc.docId;
                            resDoc.score = score;
                            resDoc.songId = Integer.parseInt(attributes[0]);
                            resDoc.title = attributes[1];
                            resDoc.year = attributes[2];
                            resDoc.artist = attributes[3];
                            resDoc.genre = attributes[4];

                            results.put(doc.docId, resDoc);
                        } else {
                            Result resDoc = results.get(doc.docId);
                            resDoc.score += score;
                            results.put(doc.docId, resDoc);
                        }
                    }

                }
            }
        }

        List<Result> entryList = new ArrayList<>(results.values());
        Collections.sort( entryList);
        return entryList;
    }

    /**
     * Suggest a corrected version of the original query
     * if some of the query terms is not found on the dictionary
     *
     * @return Suggested query or null
     */
    public String getQuerySuggestion(){

        String querySuggestion = null;
        if(correctedTerms.size() > 0){
            querySuggestion = "";
            for (String term: lastQuery) {

                querySuggestion += correctedTerms.containsKey(term)
                        ? correctedTerms.get(term) : term;

                querySuggestion += " ";
            }

            querySuggestion = querySuggestion.trim();
        }

        return querySuggestion;
    }


    /**
     * Creates an string representation of the VSM index object
     *
     * @return the generated string representing the VSM object
     */
    public String toString() {
        String outString = new String();

        for (Map.Entry<String, ArrayList<Doc>> entry : indexMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            outString += String.format("%-15s", entry.getKey());
            outString += entry.getValue() + "\t";

            for (Doc doc: entry.getValue()) {
                outString += doc + "\t";
            }
            outString += "\n";
        }
        return outString;
    }

    /**
     * Merges two posting lists with matching documentIds and with
     * positional difference less than k=1
     *
     * @param post1 first postings
     * @param post2 second postings
     * @return merged result of two postings
     */
    public ArrayList<Doc> intersect(ArrayList<Doc> post1, ArrayList<Doc> post2)
    {
        int p1 = 0;
        int p2 = 0;
        int k = 1;
        ArrayList<Doc> mergedPostings = new ArrayList<>();

        while (p1 < post1.size() && p2 < post2.size()){

            Doc doc1 = post1.get(p1);
            Doc doc2 = post2.get(p2);
            Doc answer = new Doc(doc1.docId);

            if (doc1.docId == doc2.docId){
                  //  && (searchGenre == null || (doc1.genre.equals(searchGenre)/* && doc2.genre.equals(searchGenre)*/))) {
                for (int pos1: doc1.positionList) {
                    for (int pos2 : doc2.positionList){
                        int diff = pos2 - pos1;
                        if (diff > 0 && diff <= k) {
                            answer.positionList.add(pos2);
                        } else if (pos2 > pos1){
                            break;
                        }
                    }
                }

                if (answer.positionList.size() > 0) {
                    mergedPostings.add(answer);
                }

                p1++;
                p2++;
            } else if (doc1.docId < doc2.docId){
                p1++;
            } else {
                p2++;
            }
        }

        return mergedPostings;
    }

    /**
     * Gets all documents that match all query terms and their positions
     *
     * @param txtQuery a phrase query that consists of any number of terms in the sequential order
     * @return ids of documents that contain the phrase
     */
    public List<Result> phraseQuery(String txtQuery)
    {
        correctedTerms = new HashMap<>();
        String[] query = txtQuery.split(splitPattern);
        ArrayList<Result> results = new ArrayList<>();
        ArrayList<Doc> matchingDocs = new ArrayList<>();

        int i = 0;
        do{
            String term = query[i++];
            File f = new File(indexPath+"/vsm/"+term+".ser");
            if(f.exists()) {
                ArrayList<Doc> docList = (ArrayList<Doc>) IndexHelper.loadData(f);

                if(matchingDocs.size()>0){

                    matchingDocs = intersect(matchingDocs, docList);
                } else{
                    matchingDocs = docList;
                }
            } else{
                String suggestedTerm = ngramIndex.getSuggestion(term);
                if(suggestedTerm != null){
                    correctedTerms.put(term, suggestedTerm);
                }
            }

        }while (i < query.length);

        for (Doc doc: matchingDocs) {

            String[] attributes = (String[]) IndexHelper.loadData(new File(indexPath+"/documents/" + doc.docId + ".ser"));

            if(searchGenre == null || attributes[4].equals(searchGenre)){
                Result resDoc = new Result();
                resDoc.docId = doc.docId;
                resDoc.score = 1.0;
                resDoc.songId = Integer.parseInt(attributes[0]);
                resDoc.title = attributes[1];
                resDoc.year = attributes[2];
                resDoc.artist = attributes[3];
                resDoc.genre = attributes[4];

                results.add(resDoc);
            }

        }

        return results;
    }

    /**
     * Retrieves list of documents withing a cluster
     *
     * @param clusterId the id of the cluster
     * @param start
     * @param limit
     * @return
     */
    public List<Result> getClusterDocs(int clusterId, int start, int limit){
        File f = new File(IndexHelper.indexPath+"/clusters_docs.ser");
        HashMap<Integer, ArrayList<Integer>> clusterDocs = (HashMap<Integer, ArrayList<Integer>>) IndexHelper.loadData(f);
        List<Result> results = new ArrayList<>();

        for (Integer docId: clusterDocs.get(clusterId)) {
            String[] attributes = (String[]) IndexHelper.loadData(new File(indexPath+"/documents/" + docId + ".ser"));

                Result resDoc = new Result();
                resDoc.docId = docId;
//                resDoc.score = 1.0;
                resDoc.songId = Integer.parseInt(attributes[0]);
                resDoc.title = attributes[1];
                resDoc.year = attributes[2];
                resDoc.artist = attributes[3];
                resDoc.genre = attributes[4];
                resDoc.lyrics = IndexHelper.loadLyrics(IndexHelper.indexPath+"/lyrics/"+attributes[0]+".txt");
                results.add(resDoc);
        }

        fullSearchSize = results.size();
        results = fullSearchSize > limit ? results.subList(start, limit) : results;
        return results;
    }
}
