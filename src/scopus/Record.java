package scopus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by crco0001 on 1/10/2018.
 */
public class Record implements Serializable {


    //from <coredata>
    private String eid;
    private String doi;
    private String title;
    private String summaryText; //abstract..
    private String publicationName;
    private String srctype;
    private String URL;
    private String language;

    private String aggregationType;

    private String sourceId;
    private int citedBy;
    private int publicationYear;
    private String documentType;


    private List<Author> authorList;
    private List<CitedReference> citedReferences;
    private List<AffiliationLevel1> affiliationLevel1List;
    private List<String> authorKeywords;
    private List<String> indexTerms;


    public List<String> getAuthorKeywords() {
        return (authorKeywords==null) ? Collections.emptyList() : authorKeywords;
    }

    public void addAuthorKeyword(String keyword) {

        if(authorKeywords == null) authorKeywords = new ArrayList<>();

        authorKeywords.add(keyword);

    }


    public void addIndexTerms(String term) {

        if(indexTerms == null) indexTerms = new ArrayList<>();
        this.indexTerms.add(term);


    }


    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public void setAuthorKeywords(List<String> authorKeywords) {
        this.authorKeywords = authorKeywords;
    }

    public List<String> getIndexTerms() {
        return (indexTerms==null) ? Collections.emptyList() : indexTerms;
    }

    public void setIndexTerms(List<String> indexTerms) {
        this.indexTerms = indexTerms;
    }

    public int getNrRefs() {

        return this.citedReferences.size();
    }

    public int getNrStandardizedAffils() {

        return this.affiliationLevel1List.size();

    }


    public List<AffiliationLevel1> getAffiliationLevel1List() {
        return (affiliationLevel1List==null) ? Collections.emptyList() : affiliationLevel1List;
    }

    public void setAffiliationLevel1List(List<AffiliationLevel1> affiliationLevel1List) {
        this.affiliationLevel1List = affiliationLevel1List;
    }

    public List<CitedReference> getCitedReferences() {
        return (citedReferences==null) ? Collections.emptyList() : citedReferences;
    }

    public void setCitedReferences(List<CitedReference> citedReferences) {
        this.citedReferences = citedReferences;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getPublicationName() {
        return publicationName;
    }

    public void setPublicationName(String publicationName) {
        this.publicationName = publicationName;
    }

    public String getSrctype() {
        return srctype;
    }

    public void setSrctype(String srctype) {
        this.srctype = srctype;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getCitedBy() {
        return citedBy;
    }

    public void setCitedBy(int citedBy) {
        this.citedBy = citedBy;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<Author> getAuthorList() {

        return (authorList == null) ? Collections.emptyList() : authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }


    @Override
    public String toString() {
        return "Record{" +
                "eid='" + eid + '\'' +
                ", doi='" + doi + '\'' +
                ", title='" + title + '\'' +
                ", summaryText='" + summaryText + '\'' +
                ", publicationName='" + publicationName + '\'' +
                ", srctype='" + srctype + '\'' +
                ", URL='" + URL + '\'' +
                ", language='" + language + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", citedBy=" + citedBy +
                ", publicationYear=" + publicationYear +
                ", authorList=" + authorList +
                ", citedReferences=" + citedReferences +
                ", affiliationLevel1List=" + affiliationLevel1List +
                ", authorKeywords=" + authorKeywords +
                ", indexTerms=" + indexTerms +
                '}';
    }
}
