package songle.engine;
import java.io.*;
import java.util.Scanner;

/**
 * Helper functions for saving and retrieving data from physical locations
 */
public class IndexHelper {

    public static String dataFile = "C:\\Projects\\RIT\\KPT\\data\\test.csv";
    public static String indexPath = "C:\\Projects\\RIT\\KPT\\indexes";
    public static String stopWords = "C:\\Projects\\RIT\\KPT\\data\\stopwords.txt";

    /**
     * Dump file a serialized object to a local file
     *
     * @param filePath the file location
     * @param data the object to be saved
     */
    public static void dumpFile(String filePath, Serializable data){
        try {
            //String json = new Gson().toJson(set);
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a serialized object from a local file
     *
     * @param file the file location
     * @return a de-serialized version of the object
     */
    public static Object loadData(File file){
        Object result = null;

        try{
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = ois.readObject();
            ois.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves plain text data from a local file
     *
     * @param filePath the file location
     * @return the content of the file
     */
    public static String loadText(String filePath){
        String text = "";

        try {
            Scanner scan = new Scanner(new File(filePath));
            while(scan.hasNextLine()){
                text += scan.nextLine().toLowerCase()+" ";
            }

            scan.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * Retrieves lyrics with html lines break
     *
     * @param filePath the file location
     * @return the content of the lyrics
     */
    public static String loadLyrics(String filePath){
        String text = "";

        try {
            Scanner scan = new Scanner(new File(filePath));
            while(scan.hasNextLine()){
                text += scan.nextLine().toLowerCase()+" <br />";
            }

            scan.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return text;
    }
}
