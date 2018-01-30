package scopus;

import java.io.Serializable;

/**
 * Created by crco0001 on 1/10/2018.
 */
public class CitedReference implements Serializable{

    String refFulltext;
    String refID;
    String refIdType;


    //parts

    String refTitle;
    String refSourceTitle;
    String pubYear;
    String firstAuthor;


    public String getRefFulltext() {
        return refFulltext;
    }

    public void setRefFulltext(String refFulltext) {
        this.refFulltext = refFulltext;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

    public String getRefIdType() {
        return refIdType;
    }

    public void setRefIdType(String refIdType) {
        this.refIdType = refIdType;
    }

    public String getRefTitle() {
        return refTitle;
    }

    public void setRefTitle(String refTitle) {
        this.refTitle = refTitle;
    }

    public String getRefSourceTitle() {
        return refSourceTitle;
    }

    public void setRefSourceTitle(String refSourceTitle) {
        this.refSourceTitle = refSourceTitle;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getFirstAuthor() {
        return firstAuthor;
    }

    public void setFirstAuthor(String firstAuthor) {
        this.firstAuthor = firstAuthor;
    }


    @Override
    public String toString() {
        return "CitedReference{" +
                "refFulltext='" + refFulltext + '\'' +
                ", refID='" + refID + '\'' +
                ", refIdType='" + refIdType + '\'' +
                ", refTitle='" + refTitle + '\'' +
                ", refSourceTitle='" + refSourceTitle + '\'' +
                ", pubYear='" + pubYear + '\'' +
                ", firstAuthor='" + firstAuthor + '\'' +
                '}';
    }
}
