package ShardingApp;

import ShardingApp.Model.Article;
import com.google.gson.Gson;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DB {

    private String dbpath;
    private int numberOfShards;
    private Gson gson = new Gson();

    public DB(String dbpath,int numberOfShards) throws IOException, ClassNotFoundException {
        this.dbpath = dbpath;
        this.numberOfShards = numberOfShards;
        createFiles();
    }

    public void insertJsonFile(String jsonFilePath) throws IOException, ClassNotFoundException {
        File jsonFile = new File(jsonFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));

        BufferedWriter bufferedWriters[] = getBufferedWriters();
        Map<Long,Long> idToPosition = getIdToPosition();
        Map<Long,Long> idToLength = getIdToLength();

        long startPos[] = new long[numberOfShards];
        String jsonString = "";
        while ((jsonString = bufferedReader.readLine()) != null) {
            Article article = gson.fromJson(jsonString, Article.class);
            int shardNum = (int) (article.getId() % numberOfShards);
            bufferedWriters[shardNum].write(jsonString);
            bufferedWriters[shardNum].write('\n');

            idToPosition.put(article.getId(),startPos[shardNum]);
            idToLength.put(article.getId(),(long) jsonString.length());
            startPos[shardNum] = startPos[shardNum] + jsonString.length() + 1;
        }

        saveIdToPosition(idToPosition);
        saveIdToLength(idToLength);

        bufferedReader.close();
        close(bufferedWriters);
    }

    public void createIndex() throws IOException {

        Map<String, LinkedList<Long> > wordToids = new HashMap<>();

        for(int i=0;i<numberOfShards;i++) {
            File shardFile = new File(dbpath + "/shards/shard" + i + ".txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(shardFile));

            String articleJsonString = null;
            while ((articleJsonString = bufferedReader.readLine()) != null) {

                Article article = gson.fromJson(articleJsonString, Article.class);
                String[] words = article.getRevision().getText().split(" ");
                Long id = article.getId();

                for (String word : words) {

                    if (wordToids.get(word) == null) {
                        LinkedList<Long> list = new LinkedList<>();
                        list.add(id);
                        wordToids.put(word,list);
                    }
                    else {
                        if (wordToids.get(word).getLast().compareTo(id) != 0) {
                            wordToids.get(word).add(id);
                        }
                    }
                }

            }
            bufferedReader.close();
        }

        saveWordToIds(wordToids);

    }

    public class Index {
        Map<String,LinkedList<Long> > wordToIds;
        Map<Long,Long> idToPosition;

        Index(Map<String,LinkedList<Long> > wordToIds,Map<Long,Long> idToPosition) {
            this.wordToIds = wordToIds;
            this.idToPosition = idToPosition;
        }

        public List<Long> getIdsByKeyword(String keyword) throws IOException, ClassNotFoundException {
            LinkedList<Long> idsByKeyword = wordToIds.get(keyword);
            return idsByKeyword;
        }

        public String getJsonDocumentById(Long id) throws IOException, ClassNotFoundException {
            return getJsonDcoumentById(id);
        }
    }


    public Index getIndex() throws IOException, ClassNotFoundException {
        Map<String,LinkedList<Long> > wordToIds = getWordToIds();
        Map<Long,Long> idToPosition = getIdToPosition();

        return new Index(wordToIds,idToPosition);
    }

    public String getJsonDcoumentById(Long id) throws IOException, ClassNotFoundException {
        int shardNum = (int)(id % numberOfShards);
        File file = new File(dbpath + "/shards/shard" + shardNum + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        int documentPosition = (int) (long) documentPosition(id);
        int documentSize = (int) (long) documentSize(id);

        bufferedReader.skip(documentPosition);
        String doc = bufferedReader.readLine();
        bufferedReader.close();
        return doc;
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
        File idToPositionFile = new File(dbpath + "/metadata/idToPosition.ser");
        if(idToPositionFile.exists() == false) {
            idToPositionFile.createNewFile();
            Map<Long,Long> hashMap = new HashMap<>();
            saveIdToPosition(hashMap);
        }

        // make idToLength hashmap file
        File idToLength = new File(dbpath + "/metadata/idToLength.ser");
        if(idToLength.exists() == false) {
            idToLength.createNewFile();
            Map<Long,Long> hashMap = new HashMap<>();
            saveIdToLength(hashMap);
        }

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

    private Map<Long,Long> getIdToPosition() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(dbpath + "/metadata/idToPosition.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Long,Long> idToPosition = (HashMap<Long,Long>) objectInputStream.readObject();
        objectInputStream.close();
        return idToPosition;
    }

    private void saveIdToPosition(Map<Long,Long> idToPosition) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbpath + "/metadata/idToPosition.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(idToPosition);
        objectOutputStream.close();
    }

    private Map<String,LinkedList<Long> > getWordToIds() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(dbpath + "/metadata/wordToIds.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<String,LinkedList<Long> > idToPosition = (HashMap<String,LinkedList<Long> >) objectInputStream.readObject();
        objectInputStream.close();
        return idToPosition;
    }

    private void saveWordToIds(Map<String,LinkedList<Long> > idToPosition) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbpath + "/metadata/wordToIds.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(idToPosition);
        objectOutputStream.close();
    }


    private Map<Long,Long> getIdToLength() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(dbpath + "/metadata/idToLength.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Long,Long> idToLength = (HashMap<Long,Long>) objectInputStream.readObject();
        objectInputStream.close();
        return idToLength;
    }

    private void saveIdToLength(Map<Long,Long> idToLength) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(dbpath + "/metadata/idToLength.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(idToLength);
        objectOutputStream.close();
    }

    private Long documentPosition(Long id) throws IOException, ClassNotFoundException {
        Map<Long,Long> map = getIdToPosition();
        return map.get(id);
    }

    private Long documentSize(Long id) throws IOException, ClassNotFoundException {
        Map<Long,Long> map = getIdToLength();
        return map.get(id);
    }
}
