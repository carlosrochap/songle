package songle.engine;
import java.util.HashMap;

/**
 * Represents a cluster centroid
 */
public class Centroid {
    public int docId;
    public int centroidId;
    double l2Norm;
    HashMap<String, Double> tfidfs;

    public Centroid(int did, int cid){
        docId = did;
        centroidId = cid;
        tfidfs = new HashMap<>();
        l2Norm = 0.0;
    }

    /**
     * returns a String representation of this object
     * @return
     */
    public String toString(){
        return centroidId+","+docId+" - "+l2Norm+":"+tfidfs;
    }
}
