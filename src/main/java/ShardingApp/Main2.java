package ShardingApp;

import ShardingApp.Model.Article;
import com.google.gson.Gson;
import com.sun.tools.doclets.standard.Standard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main2 {
    public static void main(String args[]) throws IOException, ClassNotFoundException {

//        File file = new File("/Users/rutvikmavani/Downloads/dbs/db1/metadata/idToPosition1.ser");
//        file.createNewFile();
//
//
//        FileOutputStream fileOutputStream = new FileOutputStream("/Users/rutvikmavani/Downloads/dbs/db1/metadata/idToPosition1.ser");
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//        Map<Long,Long> idToPosition = new HashMap<>();
//        idToPosition.put(1L,1L);
//        idToPosition.put(2L,2L);
//        idToPosition.put(3L,3L);
//        objectOutputStream.writeObject(idToPosition);
//        objectOutputStream.close();
//

        FileInputStream fileInputStream = new FileInputStream("/Users/rutvikmavani/Downloads/dbs/db1/metadata/idToPosition.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Long,Long> idToPosition = (HashMap<Long, Long>) objectInputStream.readObject();
        System.out.println(idToPosition.size() + idToPosition.toString());
        objectInputStream.close();

//        File file = new File("/Users/rutvikmavani/Downloads/wikipedia2json-master/out.json");
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

    }
}
