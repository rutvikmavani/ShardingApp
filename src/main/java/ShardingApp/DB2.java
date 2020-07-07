package ShardingApp;

import ShardingApp.Model.Article;
import com.google.gson.Gson;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DB2 {

    private String dbpath;
    private int numberOfShards;
    private Gson gson = new Gson();
    private BufferedWriter bufferedWriters[];

    public DB2(String dbpath,int numberOfShards) throws IOException, ClassNotFoundException {
        this.dbpath = dbpath;
        this.numberOfShards = numberOfShards;
        createFiles();
    }


    public void insertJsonFile(String jsonFilePath) throws IOException, ClassNotFoundException {
        File jsonFile = new File(jsonFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));

        BufferedWriter bufferedWriters[] = getBufferedWriters();
        Map<Long,Long> idToPosition = getMap();

        long startPos = 0;
        String jsonString = "";
        while ((jsonString = bufferedReader.readLine()) != null) {
            Article article = gson.fromJson(jsonString, Article.class);
            int index = (int) (article.getId() % numberOfShards);
            bufferedWriters[index].write(jsonString);

            idToPosition.put(article.getId(),startPos);
            startPos = startPos + jsonString.length() + 1;
        }

        saveMap(idToPosition);
        close(bufferedWriters);
    }

    public void clear() {
        File file = new File(dbpath + "/shards");
        for (File shards: file.listFiles()) {
            shards.delete();
        }
    }

    /*--------------------------------------- helper methods ---------------------------------------*/

    private void createFiles() throws IOException, ClassNotFoundException {
        // make root directory
        new File(dbpath).mkdirs();

        // make shards directoty
        new File(dbpath + "/shards").mkdirs();

        // make shards
        for (int i=0;i<numberOfShards;i++) {
            File file = new File(dbpath + "/shards/shard" + i + ".txt");
            file.createNewFile();
        }

        // make metadata directory
        new File(dbpath + "/metadata").mkdirs();

        // make idToPosition hashmap file
        File hashMapFile = new File(dbpath + "/metadata/idToPosition.ser");
        hashMapFile.createNewFile();
        Map<Long,Long> hashMap = new HashMap<>();
        saveMap(hashMap);
    }

    private void close(Closeable closeable[]) throws IOException {
        for(int i=0;i<closeable.length;i++) {
            closeable[i].close();
        }
    }

    private BufferedWriter[] getBufferedWriters() throws IOException {
        BufferedWriter bufferedWriters[] = new BufferedWriter[numberOfShards];
        for(int i=0;i<numberOfShards;i++) {
            File file1 = new File(dbpath + "/shards/shard" + i + ".txt");
            FileWriter fileWriter = new FileWriter(file1, true);
            bufferedWriters[i] = new BufferedWriter(fileWriter);
        }
        return bufferedWriters;
    }

    private Map<Long,Long> getMap() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(dbpath + "/metadata/idToPosition.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Long,Long> idToPosition = (HashMap<Long, Long>) objectInputStream.readObject();
        objectInputStream.close();
        return idToPosition;
    }

    private void saveMap(Map<Long,Long> idToPosition) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbpath + "/metadata/idToPosition.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(idToPosition);
        objectOutputStream.close();
    }
}
