package Database;

import de.bwaldvogel.liblinear.FeatureNode;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
import java.util.*;

public class SerializeToMVStorage {


        private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
        private final MVStore store;
        private MVMap<Integer,byte[]> featureLists;
        private MVMap<Integer,byte[]> lableSet;


        public SerializeToMVStorage(String databaseFile) {

            this.store = new MVStore.Builder().cacheSize(256).autoCommitBufferSize(2048).fileName(databaseFile).open();
            this.store.setVersionsToKeep(0);
            this.store.setReuseSpace(true);
            this.featureLists = store.openMap("records");
            this.lableSet = store.openMap("labels");


            this.conf.registerClass(FeatureNode.class);
            this.conf.registerClass(HashSet.class);
            this.conf.registerClass(ArrayList.class);
            this.conf.registerClass(Integer.class);

        }

        private byte[] featureListToBytes(ArrayList<FeatureNode> featureNodeList) {


            return this.conf.asByteArray(featureNodeList);

        }

    private byte[] labelSetToBytes(HashSet<Integer> labelSet) {


        return this.conf.asByteArray(labelSet);

    }



        public FeatureNode[] bytesToFeatureList(byte[] bytes) {

            ArrayList<FeatureNode> featureNodeList = (ArrayList<FeatureNode>) this.conf.asObject(bytes);

            return featureNodeList.toArray( new FeatureNode[0]);

        }


    public HashSet<Integer> bytesToLabelSet(byte[] bytes) {

        HashSet<Integer> labelSet = (HashSet<Integer>) this.conf.asObject(bytes);

        return labelSet;

    }




        public void saveInstance(Integer id, ArrayList<FeatureNode> doc, HashSet<Integer> labels) {

            this.featureLists.put(id,  featureListToBytes(doc)  );
            this.lableSet.put(id, labelSetToBytes(labels));

        }

        public FeatureNode[] retrieveInstanceFeatures(Integer id) {

            byte[] serializedRecord =  featureLists.get(id);
            if(serializedRecord == null) return null;

            return bytesToFeatureList(serializedRecord);
        }

    public HashSet<Integer> retrieveInstanceLabels(Integer id) {

        byte[] serializedRecord =  lableSet.get(id);
        if(serializedRecord == null) return null;

        return bytesToLabelSet(serializedRecord);
    }


        public void removeInstance(Integer id) {

            featureLists.remove(id);
            lableSet.remove(id);
        }

        public void forceCommit() {

            store.commit();
        }

        public void compact() {

            store.compactMoveChunks();
        }

        public void compactFully() {

            store.compactRewriteFully();

        }


        public Set<Map.Entry<Integer,byte[]>> getEntrySet() {

            return featureLists.entrySet();

        }

        public int dbSize() {

            return featureLists.size();
        }

        public void close() {

            store.close();
        }

        public static void main(String arg[]) throws IOException {



        }

    }

