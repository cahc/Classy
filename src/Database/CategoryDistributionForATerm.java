package Database;

import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Cristian on 2017-01-12.
 */
public class CategoryDistributionForATerm {

    class Pair implements Comparable<Pair> {

        public Pair(int cat, int occ) {

            this.category = cat;
            this.occurances = occ;
        }

        int category;
        int occurances;

        @Override
        public int compareTo(Pair other) {

            if(this.category > other.category) return 1;
            if(this.category < other.category) return -1;
            return 0;

        }

        @Override
        public String toString() {

            return this.category + " " + this.occurances;
        }

    }

    private int totalFrequency = 0;
    IntIntMap categoryToFrequency;

    CategoryDistributionForATerm(int level) throws MyOwnException {

        if (level != 3 && level != 5) throw new MyOwnException("level must be 3 or 5");

        categoryToFrequency = HashIntIntMaps.getDefaultFactory().withDefaultValue(0).newMutableMap(level > 3 ? 300 : 60);

    }


    public void incrementCategorySize(int category) {

        this.categoryToFrequency.addValue(category, 1);
        totalFrequency++;
    }

    public int getTotalFrequency() {

        return totalFrequency;
    }

    public ArrayList<Pair> printCategoryDistributionForATerm() {

        ArrayList<Pair> pairs = new ArrayList<>(30);

        for (IntIntCursor cur = this.categoryToFrequency.cursor(); cur.moveNext(); ) {

              int category = cur.key();
              int occurance = cur.value();

              pairs.add( new Pair(category,occurance) );

        }

        Collections.sort(pairs);

        return pairs;
    }

}
