package SwePub;
import Database.SwePubParser;
import misc.LanguageTools.HelperFunctions;
import misc.LanguageTools.LanguageGuesser;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.*;

/**
 * Created by Cristian on 28/09/16.
 * A parsed swePub record with information necessary for creating instances used for traning a HSV classifyer
 */
public class Record implements Serializable {


    private static final String separator = "\t";

    @Override
            public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.URI).append(separator);
        stringBuilder.append(this.supplier).append(separator);
        stringBuilder.append(this.language).append(separator);
        stringBuilder.append(this.containsSupportedLanguageText).append(separator);
        stringBuilder.append(this.containsEnglish).append(separator);
        stringBuilder.append(this.containsSwedish).append(separator);
        stringBuilder.append(isFullEnglishText()).append(separator);
        stringBuilder.append(isFullSwedishText()).append(separator);
        stringBuilder.append(this.containsLevel3Classification).append(separator);
        stringBuilder.append(this.containsLevel5Classification).append(separator);
        stringBuilder.append(this.publicationTypes).append(separator);
        stringBuilder.append(this.contentTypes).append(separator);
        stringBuilder.append(this.publisher).append(separator);
        stringBuilder.append(this.publishedYear).append(separator);
        stringBuilder.append(this.issn).append(separator);
        stringBuilder.append(this.ISBN).append(separator);
        stringBuilder.append(this.hostName).append(separator);
        stringBuilder.append( (getTermsFromHost().toString())).append(separator);
        stringBuilder.append(this.title).append(separator);
        stringBuilder.append(this.summary).append(separator);
        stringBuilder.append(this.termsFromTitleAbstract).append(separator);


        if(this.classificationCodes == null) {

          stringBuilder.append("no uka").append(separator);
        } else {

            Integer[] codes = classificationCodes.toArray( new Integer[ classificationCodes.size() ] );
            stringBuilder.append(Arrays.toString(codes)).append(separator);
        }


        if(this.unkontrolledKkeywords ==  null) {


            stringBuilder.append("no keywords").append(separator);
        } else {

            stringBuilder.append(this.unkontrolledKkeywords.toString()).append(separator);

        }


        if(this.affiliations == null) {

            stringBuilder.append("no affiliations").append(separator);
            stringBuilder.append("no affiliation terms to extract");
        } else {

            StringBuilder stringbuilder2 = new StringBuilder();
            int n= this.affiliations.size();
            int count =1;
            for(String s : this.affiliations) {

                if(count == n) stringbuilder2.append(s);
                if(count != n) stringbuilder2.append(s).append(";");
                count++;
            }



            //stringBuilder.append(this.affiliations.toString()).append(separator);
            stringBuilder.append(stringbuilder2.toString()).append(separator);
            stringBuilder.append( getTermsFromAffiliation() );
        }






        return stringBuilder.toString();
    }


    ////////////////not used as features in classification///////////////////
   // private Integer internalID;
    private String URI; //link to source
    private String diva2Id;
    private Integer publishedYear; // only available if the publication is formally published
    private ArrayList<String> supplier; //from which local repository

    private int mapDBKey;

    private boolean autoClassedBySwepub;

    //classification target
    private HashSet<Integer> classificationCodes; //HSV

    /////////////////source for features in classification///////////////

    private List<TextAndLang> title; // inc sub-title
    private List<TextAndLang> summary; //abstract
    private ArrayList<String> termsFromTitleAbstract;
    private List<String> termsFromAffiliation;
    private List<String> termFromHost;

    private boolean containsSupportedLanguageText = false;
    private boolean containsLevel3Classification = false;
    private boolean containsLevel5Classification = false;
    private boolean fullSwedishText = false;
    private boolean fullEnglishText = false;

    private boolean containsSwedish = false;
    private boolean containsEnglish = false;

    public void setMapDBKey(int key) {

        this.mapDBKey =key;
    }

    public int getMapDBKey() {

        return this.mapDBKey;
    }


    public String getDiva2Id() {
        return diva2Id;
    }


    public boolean isAutoClassedBySwepub() {
        return autoClassedBySwepub;
    }

    public void setAutoClassedBySwepub(boolean autoClassedBySwepub) {
        this.autoClassedBySwepub = autoClassedBySwepub;
    }

    public void setDiva2Id(String diva2Id) {
        this.diva2Id = diva2Id;
    }

    public boolean isFullSwedishText() {
        return fullSwedishText;
    }

    public void setFullSwedishText(boolean fullSwedishText) {
        this.fullSwedishText = fullSwedishText;
    }

    public boolean isFullEnglishText() {
        return this.fullEnglishText;
    }

    public void setFullEnglishText(boolean fullEnglishText) {
        this.fullEnglishText = fullEnglishText;
    }

    //build separate classifiers for swedish and english..
    private  ArrayList<String> language; //a record with repeated fields in different languages should constitut differens Records


    private HashSet<String> affiliations; // use affiliation data might be useful for creating features, ie. institution of *SOCIOLOGY* or *MICROBIOLOGY*
    private ArrayList<String> unkontrolledKkeywords; // mainly "controlled" keywords

    //Channel information
    //two major categories: "serial" and "monographic work"
    private List<String> publicationTypes;  //svep categories
    private List<String> contentTypes; //svep classification, ref, etc
    private List<String> issn; //issn and/or e-issn
    private String ISBN; //use publisher prefix for classification. A book can have more than one ISBN (e.g., print, e-pub) but in terms of prefix they should be the same
    private String hostName; // name

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (!URI.equals(record.URI)) return false;
        if (publishedYear != null ? !publishedYear.equals(record.publishedYear) : record.publishedYear != null)
            return false;
        if (supplier != null ? !supplier.equals(record.supplier) : record.supplier != null) return false;
        if (title != null ? !title.equals(record.title) : record.title != null) return false;
        return publicationTypes != null ? publicationTypes.equals(record.publicationTypes) : record.publicationTypes == null;

    }

    @Override
    public int hashCode() {
        int result = URI.hashCode();
        result = 31 * result + (publishedYear != null ? publishedYear.hashCode() : 0);
        result = 31 * result + (supplier != null ? supplier.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (publicationTypes != null ? publicationTypes.hashCode() : 0);
        return result;
    }

    private String publisher;






    public Record() {}


    public boolean containsLevel3Classification() {
        return containsLevel3Classification;
    }

    public void setContainsLevel3Classification(boolean containsLevel3Classification) {
        this.containsLevel3Classification = containsLevel3Classification;
    }

    public boolean containsLevel5Classification() {
        return containsLevel5Classification;
    }

    public void setContainsLevel5Classification(boolean containsLevel5Classification) {
        this.containsLevel5Classification = containsLevel5Classification;
    }



    public void setClassificationCodesAvaliability() {

        if(classificationCodes == null) return;

        for( Integer i : classificationCodes) {

            int code = i.intValue();
            if ((code > 99) && (code < 9999)) setContainsLevel3Classification(true);
            if (code > 9999) { setContainsLevel5Classification(true); setContainsLevel3Classification(true); }
        }
    }


    public ArrayList<String> getTermsFromTitleAbstract() {

        if(this.termsFromTitleAbstract == null) return new ArrayList<>();
        return this.termsFromTitleAbstract;
    }

    public boolean containsSupportedLanguageText() {
        return containsSupportedLanguageText;
    }


    public List<String> getTermsFromAffiliation() {

        if(this.termsFromAffiliation == null) return Collections.emptyList();

        return this.termsFromAffiliation;
    }


    public List<String> getTermsFromHost() {

        if(this.termFromHost == null) return Collections.emptyList();

        return this.termFromHost;
    }

    public void addTermsFromTitleAbstract(String term) {


        if(this.termsFromTitleAbstract == null) {

            this.termsFromTitleAbstract = new ArrayList<>();
            this.termsFromTitleAbstract.add(term);
        } else {

            this.termsFromTitleAbstract.add(term);
        }

    }


    public void addPublicationType(String s) {

        if(this.publicationTypes ==  null) {

            this.publicationTypes = new ArrayList<>(2);
            this.publicationTypes.add(s);
        } else {

            this.publicationTypes.add(s);
        }

    }

    public List<String> getPublicationTypes() {


        if(this.publicationTypes == null) return Collections.emptyList();

        return this.publicationTypes;

    }


    public void addContentType(String s) {

        if(this.contentTypes ==  null) {

            this.contentTypes = new ArrayList<>(2);
            this.contentTypes.add(s);
        } else {

            this.contentTypes.add(s);
        }

    }

    public List<String> getContentTypes() {


        if(this.contentTypes == null) return Collections.emptyList();

        return this.contentTypes;

    }

    public void setFinalLanguages() {

        //per title (can be multiple) and per abstract (can be multiple)

        if(this.language == null) { this.language = new ArrayList<>(); this.language.add("und"); }


        if(this.language.contains("eng") || this.language.contains("swe") || this.language.contains("und") || this.language.contains("other")) {

            List<TextAndLang> titles = this.getTitle();

            for(int i=0; i<titles.size(); i++) {

                TextAndLang textAndLang = titles.get(i);
                String title = textAndLang.getText();

                if(title != null) {

                    try {

                        String tmp;
                        if(this.language.contains("eng") || this.language.contains("swe")) { tmp = LanguageGuesser.getRestrictedLanguage(title);  } else {

                            tmp = LanguageGuesser.getLanguage(title);
                        }


                        if ( (tmp != null) && tmp.equals("en")) {
                            textAndLang.setLang("eng");
                            this.containsSupportedLanguageText = true;
                        }
                        if ( (tmp != null) && tmp.equals("sv")) {
                            textAndLang.setLang("swe");
                            this.containsSupportedLanguageText = true;
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Language detection failed" + e.getMessage());
                    }

                }


            } // for loop for title ends


            List<TextAndLang> summaries = this.getSummary();

            for(int i=0; i<summaries.size(); i++) {

                TextAndLang textAndLang = summaries.get(i);
                String summary = textAndLang.getText();

                if(summary != null) {

                    try {

                        String tmp;
                        if(this.language.contains("eng") || this.language.contains("swe")) { tmp = LanguageGuesser.getRestrictedLanguage(summary);  } else {

                            tmp = LanguageGuesser.getLanguage(summary);
                        }

                        if ( (tmp != null) && tmp.equals("en")) {
                            textAndLang.setLang("eng");
                            this.containsSupportedLanguageText = true;
                        }
                        if ( (tmp  != null) && tmp.equals("sv")) {
                            textAndLang.setLang("swe");
                            this.containsSupportedLanguageText = true;
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Language detection failed");
                    }

                }


            } // for loop for abstract ends



        } // else do nothing and let containsSupportedLanguageText remain false

    }



    public void makeFeaturesFromAffiliation() {

        if(this.affiliations == null) {

            this.termsFromAffiliation = Collections.emptyList();
            return;

        }
        HashSet<String> set = new HashSet<>();
            for(String string :  this.affiliations) {

                String temp = Normalizer.normalize(string, Normalizer.Form.NFD);
                temp = temp.replaceAll("[^\\p{ASCII}]","");
                temp = temp.replaceAll("[^a-zA-Z]"," ");
                temp = temp.toLowerCase();

                String[] temp2 = temp.split("\\s");

                for(String term : temp2) if(!HelperFunctions.affiliationStopwords.contains(term) && term.length() >= 3) set.add(SwePubParser.prefixFromAffiliation.concat(term));

            }


        this.termsFromAffiliation = new ArrayList<>(set);

        }



    public void makeFeaturesFromHost() {


        if(this.hostName == null) {

            this.termFromHost = Collections.emptyList();
            return;
        }

        String temp = Normalizer.normalize(this.hostName, Normalizer.Form.NFD);
        temp = temp.replaceAll("[^\\p{ASCII}]","");
        temp = temp.replaceAll("[^a-zA-Z]"," ");
        temp = temp.toLowerCase();
        String[] temp2 = temp.split("\\s");

        HashSet<String> set = new HashSet<>();
        for(String term : temp2) {

            if(!HelperFunctions.hostStopwords.contains(term) && term.length() >= 3) set.add(  SwePubParser.prefixFromHost.concat(term)  );

        }

        this.termFromHost = new ArrayList<>(set);



    }





    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }


    public void addAffiliations(List<String> affils) {

        if(affils.size() == 0) return;

        //create a string from the affiliation / name part (it should be in a hierarchy

        StringBuilder builder = new StringBuilder();

         for(String s : affils) {

             builder.append(s).append(" ");
         }

        if(this.affiliations == null) {

            this.affiliations = new HashSet<>();
            this.affiliations.add(builder.toString().trim());
            //this.affiliations.addAll(affils);

        } else {

            this.affiliations.add(builder.toString().trim());
            //this.affiliations.addAll(affils);
        }

    }

    public Set<String> getAffiliations() {

        if(this.affiliations == null) return Collections.emptySet();

        return this.affiliations;

    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public ArrayList<String> getSupplier() {
        return supplier;
    }

    public void setSupplier(ArrayList<String> supplier) {
        this.supplier = supplier;
    }

    public List<TextAndLang> getTitle() {
        if(this.title == null) return Collections.emptyList();

        return title;
    }

    public void setTitle(String title) {

        if(this.title == null) {

            this.title = new ArrayList<>(1);

            this.title.add( new TextAndLang(title) );

        } else {

            this.title.add( new TextAndLang(title) );
        }





    }

    public List<TextAndLang> getSummary() {
        if(this.summary == null) return Collections.emptyList();
        return summary;
    }

    public void setSummary(String summary) {
        if(this.summary == null) {

            this.summary = new ArrayList<>(1);
            this.summary.add(new TextAndLang(summary));

        } else {

            this.summary.add(new TextAndLang(summary));
        }
    }

    public HashSet<Integer> getClassificationCodes() {

        if( classificationCodes != null) return classificationCodes;

        return new HashSet<>();

    }

    public void addClassificationCodes(Integer classificationCode) {

        if(this.classificationCodes == null) {

            this.classificationCodes = new HashSet<>();
            this.classificationCodes.add(classificationCode);
        } else {

            this.classificationCodes.add(classificationCode);
        }

    }


    public ArrayList<String> getLanguage() {
        return language;
    }

    public void setLanguage(ArrayList<String> language) {
        this.language = language;
    }

    public List<String> getIssn() {
        if(issn ==null) return Collections.emptyList();

        return issn;
    }

    public void addIssn(List<String> list) {

        if(list.size() == 0) return;

        if(this.issn == null) {

            this.issn = new ArrayList<>(2);
            this.issn.addAll(list);
        } else {

            this.issn.addAll(list);
        }

    }

    public void addIssn(String issn) {

        if(this.issn == null) {

            this.issn = new ArrayList<>(2);
            this.issn.add(issn);
        } else {

            this.issn.add(issn);
        }

    }

    public String getISBN() {
        return ISBN;
    }

    public void addISBN(String ISBN) {

     this.ISBN = ISBN;

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getUnkontrolledKkeywords() {

        if(this.unkontrolledKkeywords == null) {

           return Collections.emptyList();

        }

        return this.unkontrolledKkeywords;
    }

    public void addUnkontrolledKkeywords(String keyword ) {
        //todo check input and check language

        if(this.unkontrolledKkeywords == null) {

            this.unkontrolledKkeywords = new ArrayList<>();
            this.unkontrolledKkeywords.add(keyword);
        } else {

            this.unkontrolledKkeywords.add(keyword);
        }

    }


    public ArrayList<String> getLanguageSpecificTerms(String lang) {



        List<String> allTerms = this.getTermsFromTitleAbstract();
        ArrayList<String> restrictedTerms = new ArrayList<>();
        for(int i=0; i<allTerms.size(); i++ ) {

            String t = allTerms.get(i);
            if(lang.equals("eng") && t.startsWith("TE@")) restrictedTerms.add(t);
            if(lang.equals("swe") && t.startsWith("TS@")) restrictedTerms.add(t);
        }

        return restrictedTerms;

    }

    public void addUnkontrolledKkeywords(List<String> keyword ) {
        //todo check input and check language

        if(this.unkontrolledKkeywords == null) {

            this.unkontrolledKkeywords = new ArrayList<>();
            this.unkontrolledKkeywords.addAll(keyword);
        } else {

            this.unkontrolledKkeywords.addAll(keyword);
        }

    }


    public boolean isContainsSwedish() {
        return containsSwedish;
    }

    public void setContainsSwedish(boolean containsSwedish) {
        this.containsSwedish = containsSwedish;
    }

    public boolean isContainsEnglish() {
        return containsEnglish;
    }

    public void setContainsEnglish(boolean containsEnglish) {
        this.containsEnglish = containsEnglish;
    }
}
