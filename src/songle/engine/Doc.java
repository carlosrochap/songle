package songle.engine;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * Document class that contains the document id and the position list
 */
class Doc implements Serializable{
    //To be completed
    int docId;
    int tf;
    double tw;
    String genre;
    int cluster;

    ArrayList<Integer> positionList;


    //public Doc(int did, double tw) {
     public Doc(int did){
        docId = did;
        this.tw = 1.0;
        tf = 1;
        cluster = 0;

        positionList = new ArrayList<>();
        positionList.add(tf);
    }

    /**
     * Inserts a term position
     * @param position the position to insert
     */
    public void insertPosition(int position)
    {
        positionList.add(position);
    }

    /**
     * Creates a text representation of this object
     * @return
     */
    public String toString()
    {
        String docIdString = ""+docId + "-"+tf+", "+tw+":<";
        for(Integer pos:positionList)
            docIdString += pos + ",";
        docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
        return docIdString;
    }

//    public String toString() {
//        return docId + ":" + tf+", "+tw;
//    }
}