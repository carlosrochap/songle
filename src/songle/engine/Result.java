package songle.engine;

/**
 * Represents query result documents as java object,
 * for easier manipulation and sorting
 *
 * @author Team 6
 */
public class Result implements Comparable<Result>{
    public int docId;
    public double score;
    public int songId;
    public String title;
    public String year;
    public String artist;
    public String genre;
    public String lyrics;

    public Result(){}

    public Result(int docId, int songId){
        this.docId = docId;
        this.songId = songId;
    }

    /**
     * Retrieves the document id
     * @return
     */
    public int getDocId() {
        return docId;
    }

    /**
     * Retrieves the computed similarity score
     * @return
     */
    public double getScore() {
        return score;
    }

    /**
     * Retrieves the lyrics title
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the lyrics year
     * @return
     */
    public String getYear() {
        return year;
    }

    /**
     * Retrieves the lyrics artist
     * @return
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Retrieves the lyrics Genre
     * @return
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Retrieves the lyrics song id
     * @return
     */
    public int getSongId(){ return songId;}


    /**
     * Compares this object with another Result object using the computed score
     * @return
     */
    @Override
    public int compareTo(Result o) {
        return Double.compare(o.score, this.score);
    }
}
