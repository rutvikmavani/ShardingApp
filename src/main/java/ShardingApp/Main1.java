package ShardingApp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main1 {
    public static void main(String args[]) throws IOException, ClassNotFoundException {
        DB db = new DB("/Users/rutvikmavani/Downloads/dbs/db1",16);
//        db.insertJsonFile("/Users/rutvikmavani/Downloads/wikipedia2json-master/out.json");
//        db.insertJsonFile("/Users/rutvikmavani/Downloads/wikipedia2json-master/enwiki.json");

        System.out.println(db.getJsonDcoumentById(10L));
        System.out.println(db.getJsonDcoumentById(13L));
        System.out.println(db.getJsonDcoumentById(14L));

        System.out.println(db.getJsonDcoumentById(15L));
        System.out.println(db.getJsonDcoumentById(18L));
        System.out.println(db.getJsonDcoumentById(19L));

        System.out.println(db.getJsonDcoumentById(20L));
        System.out.println(db.getJsonDcoumentById(21L));
        System.out.println(db.getJsonDcoumentById(23L));

//        db.createIndex();

        DB.Index index = db.getIndex();
        System.out.println("done");

        System.out.println(index.getIdsByKeyword("from"));
        System.out.println(index.getJsonDocumentById(13L));
        System.out.println(index.getIdsByKeyword("is"));


    }
}
