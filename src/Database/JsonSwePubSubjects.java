package Database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class JsonSwePubSubjects {



    private final HashSet<Integer> ukaLevel2 = new HashSet<>();
    private final HashSet<Integer> ukaLevel3 = new HashSet<>();

    private final ArrayList<String> keywords = new ArrayList<>();



    public boolean hasLevel2Codes() {

        return this.ukaLevel2.size() > 0;

    }

    public boolean hasLevel3Codes() {

        return this.ukaLevel3.size() > 0;

    }

    public JsonSwePubSubjects() {}


    public HashSet<Integer> getUkaLevel2() {
        return ukaLevel2;
    }

    public void addUkaLevel2(Integer ukaLevel2) {
        this.ukaLevel2.add(ukaLevel2);
    }

    public void addUkaLevel3(Integer ukaLevel3) {
        this.ukaLevel3.add(ukaLevel3);
    }

    public HashSet<Integer> getUkaLevel3() {
        return this.ukaLevel3;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void addKeywords(String keywords) {
        this.keywords.add(keywords);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonSwePubSubjects)) return false;
        JsonSwePubSubjects that = (JsonSwePubSubjects) o;
        return Objects.equals(ukaLevel2, that.ukaLevel2) &&
                Objects.equals(ukaLevel3, that.ukaLevel3) &&
                Objects.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ukaLevel2, ukaLevel3, keywords);
    }


    @Override
    public String toString() {
        return "JsonSwePubSubjects{" +
                "ukaLevel2=" + ukaLevel2 +
                ", ukaLevel3=" + ukaLevel3 +
                ", keywords=" + keywords +
                '}';
    }
}
