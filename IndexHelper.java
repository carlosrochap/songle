package songle.engine;
import java.io.*;
import java.util.Scanner;

/**
 * Created by Carlos on 4/28/2017.
 */
public class IndexHelper {

    public static String dataFile = "C:\\Projects\\RIT\\KPT\\data\\cluster.csv";
    public static String indexPath = "C:\\Projects\\RIT\\KPT\\indexes";

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
}
