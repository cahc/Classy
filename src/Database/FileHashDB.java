package Database;

import SwePub.Record;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Cristian on 27/09/16.
 */
public class FileHashDB {

    private String pathToFile = "Records.db";
    public ConcurrentMap<Integer, Record> database;
    private DB dbPersist;
    public void setPathToFile(String path) {

        this.pathToFile = path;
    }
    public void createOrOpenDatabase() {

        File persist = new File(pathToFile);

        this.dbPersist = DBMaker.fileDB(persist)
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()   // Make mmap file faster
                .allocateStartSize(32*1024*1024) // 32MB
                .allocateIncrement(64*1024*1024) // 64MB
                .executorEnable() // Enables background executor
                .closeOnJvmShutdown()
                .make();

        this.database = dbPersist.hashMap("swepub")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.ELSA )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .createOrOpen();



    }
    public void create() {

        File persist = new File(pathToFile);

        this.dbPersist = DBMaker.fileDB(persist)
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()   // Make mmap file faster
                .allocateStartSize(32*1024*1024) // 32MB
                .allocateIncrement(64*1024*1024) // 64MB
                .executorEnable() // Enables background executor
                .closeOnJvmShutdown()
                .make();

        this.database = dbPersist.hashMap("swepub")
                .counterEnable()
                .keySerializer(   Serializer.INTEGER  )
                .valueSerializer( Serializer.ELSA )
                .layout(8,24,4)  //Maximal Hash Table Size is calculated as: segment * node size ^ level count: here: 2654208
                .create();

    }
    public void closeDatabase() {

        this.dbPersist.close();
    }
    public void put(Integer i, Record r) {

        this.database.put(i,r);

    }

    public Record get(Integer i) {

        return this.database.get(i);

    }
    public int size() {

        return this.database.size();
    }




}
