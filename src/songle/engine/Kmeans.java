package songle.engine;

import java.math.BigDecimal;
import java.util.*;

/**
 * Kmeans implementation using Vector Space Model
 */
public class Kmeans {
    public ArrayList<Centroid> centroids;
    public HashMap<Integer, ArrayList<Integer>> clusterAssignments;
    private Random randomGenerator;
    public HashMap<Integer, Integer> docClusterMap;
    VSM vsm;

    /**
     * Kmeans constructor, initialize centroids at random positions
     *
     * @param k
     * @param vsModel
     */
    public Kmeans(int k, VSM vsModel){
        vsm = vsModel;
        centroids = new ArrayList<>();
        randomGenerator = new Random();
        clusterAssignments = new HashMap<>();
//        int[] assignedCentroids = new int[k];
        ArrayList<Integer> assignedCentroids = new ArrayList<>();
        docClusterMap = new HashMap<>();

        for (int i=0; i<k; i++){
            int index;
            do{
                index = randomGenerator.nextInt(vsm.documents.size());
            } while (assignedCentroids.contains(index));


            assignedCentroids.add(index);
            String[] lyricFields = vsm.documents.get(index);
            String lyrics = IndexHelper.loadText(IndexHelper.indexPath+"/lyrics/"+lyricFields[0]+".txt");
            String[] terms = lyrics.split(vsm.splitPattern);


            Centroid centroid = new Centroid(index, i);
            centroid.l2Norm = vsm.docLength[index];

            for (String term: terms) {
                if (term.trim().equals("")) continue;
                ArrayList<Doc> docList = vsm.indexMap.get(term);

                Comparator<Doc> c = new Comparator<Doc>() {
                    public int compare(Doc d1, Doc d2) {
                        return new Integer(d1.docId).compareTo(d2.docId);
                    }
                };

                int docIndex = Collections.binarySearch(docList, new Doc(index), c);
                Doc doc = docList.get(docIndex);

                centroid.tfidfs.put(term, (doc.tw /vsm.docLength[index]));
            }
            centroids.add(centroid);

        }

    }

    /**
     * Assings documents to closest centroid
     *
     * @return
     */
    public double clusterDocuments(){
        BigDecimal RSS = new BigDecimal(0.0);

        clusterAssignments.clear();
        docClusterMap.clear();

        HashMap<Integer, HashMap<Integer, Double>> centroidDocSimilarity = new HashMap<>();
        for (String term: vsm.indexMap.keySet()) {

            for (Doc doc: vsm.indexMap.get(term)) {

                for (Centroid c: centroids) {

                    double score = 0.0;
                    if(c.tfidfs.containsKey(term)){

                        double docTfidf = doc.tw / vsm.docLength[doc.docId];
                        double centroidTfidf = c.tfidfs.get(term);// / c.l2Norm;
                        score = docTfidf * centroidTfidf;
                    }

                    RSS = RSS.add(new BigDecimal(score) );// += score;

                    if(centroidDocSimilarity.containsKey(doc.docId)){

                        HashMap<Integer, Double> centroidsSimilarity = centroidDocSimilarity.get(doc.docId);
                        double cosineSim;
                        if(centroidsSimilarity.containsKey(c.centroidId)){
                            cosineSim = centroidsSimilarity.get(c.centroidId);
                            cosineSim += score;

                        } else {
                            cosineSim = score;
                        }
                        centroidsSimilarity.put(c.centroidId, cosineSim);

                    } else {
                        HashMap<Integer, Double> centroidsSimilarity = new HashMap<>();
                        centroidsSimilarity.put(c.centroidId, score);
                        centroidDocSimilarity.put(doc.docId, centroidsSimilarity);
                    }

                }
            }

        }

        for (Map.Entry<Integer, HashMap<Integer, Double>> set : centroidDocSimilarity.entrySet()) {
            int cId = 0;
            double maxScore = 0.0;
            for (Map.Entry<Integer, Double> cset: set.getValue().entrySet()) {
                if(cset.getValue() > maxScore){
                    cId = cset.getKey();
                    maxScore = cset.getValue();
                }
            }

            docClusterMap.put(set.getKey(), cId);
            ArrayList<Integer> docList = new ArrayList<>();
            if(clusterAssignments.containsKey(cId)){
                docList = clusterAssignments.get(cId);
            }

            docList.add(set.getKey());
            clusterAssignments.put(cId, docList);
        }

        return RSS.doubleValue();
    }

    /**
     * moves centroids locations using tfidf mean
     *
     */
    public void repositionCentroids(){

        for (String term: vsm.indexMap.keySet()) {

            for (Doc doc: vsm.indexMap.get(term)) {
                for(Integer ckey : clusterAssignments.keySet()){
                    if(clusterAssignments.get(ckey).contains(doc.docId)){
                        Centroid centroid = getCentroid(ckey);

                        if (centroid.tfidfs.containsKey(term)){
                            double newScore = centroid.tfidfs.get(term) +  doc.tw;
                            centroid.tfidfs.put(term, newScore);

                        } else {
                            //add tfidf because it should be center of all
                            centroid.tfidfs.put(term, doc.tw);
                        }

                    }
                }
            }
        }

        for (Centroid c: centroids) {
            if(!clusterAssignments.containsKey(c.centroidId)) continue;
            for (String term: c.tfidfs.keySet()) {
                double score = c.tfidfs.get(term);
                score /= clusterAssignments.get(c.centroidId).size();//centroidTfidfs.get(term);
                c.tfidfs.put(term, score);
            }

            c.l2Norm /= clusterAssignments.get(c.centroidId).size();
        }
    }

    /**
     * retrieves a centroid by its id
     * @param cid
     * @return
     */
    private Centroid getCentroid(int cid){

        Centroid centroid = null;
        for (Centroid c: centroids) {
            if (c.centroidId == cid) {
                centroid = c;
                break;
            }
        }

        return centroid;
    }

}
