package songle.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Generates the VSM, NGrams, and Positional indexes and dump them into a local path
 *
 */
public class IndexGenerator {


    public static void main(String[] args){

        List<String> stopWords = new ArrayList<>();

        try{
            File file = new File(IndexHelper.stopWords);

            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                stopWords.add(scan.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }


        String[] indexFolders = {"vsm", "ngrams", "documents", "lyrics"};
        for (String folder: indexFolders) {
            File dir = new File(IndexHelper.indexPath+"/"+folder);
            dir.mkdir();
        }
        VSM vsm = new VSM(IndexHelper.indexPath);
        vsm.indexDocuments(IndexHelper.dataFile);

        Kmeans kmeans = new Kmeans(10, vsm);
        double lastRSS = 0.0;
        while (true){
            double rss = kmeans.clusterDocuments();


            if(rss == lastRSS) break;
            else{
                lastRSS = rss;
            }

            kmeans.repositionCentroids();

        }

        HashMap<Integer, ArrayList<String>> clusters = new HashMap<>();
        for (Centroid c: kmeans.centroids) {

            String prevTerm = "";
            String prevTerm2 = "";
            String prevTerm3 = "";
            String prevTerm4 = "";
            double higestTfidf = 0.0;
            double higestTfidf2 = 0.0;
            double higestTfidf3 = 0.0;
            for (String term: c.tfidfs.keySet()) {
                if(c.tfidfs.get(term) > higestTfidf && !stopWords.contains(term) && !term.matches("[0-9]+") && term.length() > 3){

                    prevTerm4 = prevTerm3;
                    prevTerm3 = prevTerm2;
//                    higestTfidf3 = higestTfidf2;
                    prevTerm2 = prevTerm;
                    higestTfidf2 = higestTfidf;
                    higestTfidf = c.tfidfs.get(term);
                    prevTerm = term;
                }
            }

            ArrayList<String> value = new ArrayList<>();
            int cSize = kmeans.clusterAssignments.containsKey(c.centroidId)
                    ? kmeans.clusterAssignments.get(c.centroidId).size() : 0;
            value.add(Integer.toString(cSize));
            value.add(prevTerm+" "+prevTerm2+" "+prevTerm3+" "+prevTerm4);
            clusters.put(c.centroidId,  value);
            System.out.println("Centroid #"+c.centroidId+": "+prevTerm+" => "+ higestTfidf+" - "+prevTerm2+"=>"+higestTfidf2);
        }

        IndexHelper.dumpFile(IndexHelper.indexPath+"/clusters.ser", clusters);
        IndexHelper.dumpFile(IndexHelper.indexPath+"/clusters_docs.ser", kmeans.clusterAssignments);

        for (String term: vsm.indexMap.keySet()) {
            for (Doc doc: vsm.indexMap.get(term)) {
                doc.cluster = kmeans.docClusterMap.get(doc.docId);
            }
        }

        vsm.dumpIndexes();
    }

}
