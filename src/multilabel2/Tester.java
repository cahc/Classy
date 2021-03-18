package multilabel2;

import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

public class Tester {

    public static void main(String[] arg) {


        IntIntMap IDF = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(); //bug default value

        IDF.addValue(0,1);

        IDF.addValue(1,1);

        IDF.addValue(0,1);
        for(IntIntCursor cur = IDF.cursor(); cur.moveNext();) {

            int index = cur.key();
            int freq = cur.value();

            System.out.println("index: " + index + " IDF: " + freq    );

        }



    }

}
