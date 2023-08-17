package StarSpace;

import Database.FileHashDB;
import Database.JsonSwePubParser;
import SwePub.HsvCodeToName;
import SwePub.Record;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PreProcessSwePub {


    public static String toStarSpace(Record record, int level, String language, boolean addAffiliationFeatures) {

        if (record.isAutoClassedBySwepub()) return null;

        Set<Integer> classLabels = record.getClassificationCodes();
        Set<Integer> finalClassLabels = new HashSet<>();

        for (Integer label : classLabels) {

            if (level == 5) {

                Integer levelFiveLabel = HsvCodeToName.firstFiveDigitsOrNull(label);
                if (levelFiveLabel != null) finalClassLabels.add(levelFiveLabel);

            } else if (level == 3) {

                Integer levelThreeLabel = HsvCodeToName.firstThreeDigitsOrNull(label);
                if (levelThreeLabel != null) finalClassLabels.add(levelThreeLabel);
            }

        }



        if (level == 5 && language.equals("eng")) {

            if (!(record.containsLevel5Classification() && record.isFullEnglishText())) return null;


            List<String> terms = record.getLanguageSpecificTerms("eng");
            if (addAffiliationFeatures) terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());

            StringBuilder stringBuilder = new StringBuilder();
            for(String t : terms) {


                stringBuilder.append(t.replaceAll(" ","_"));
                stringBuilder.append(" ");

            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\t");
            for(Integer c : finalClassLabels) {

                stringBuilder.append(c);
                stringBuilder.append(",");

            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            return stringBuilder.toString();


        } else if (level == 3 && language.equals("eng")) {

            if (!(record.containsLevel3Classification() && record.isFullEnglishText())) return null;

            List<String> terms = record.getLanguageSpecificTerms("eng");
            if (addAffiliationFeatures) terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());


            StringBuilder stringBuilder = new StringBuilder();
            for(String t : terms) {


                stringBuilder.append(t.replaceAll(" ","_"));
                stringBuilder.append(" ");

            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append(" ");
            for(Integer c : finalClassLabels) {

                stringBuilder.append("__label__"+c);
                stringBuilder.append(" ");

            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            return stringBuilder.toString();


        } else if (level == 5 && language.equals("swe")) {

            if (!(record.containsLevel5Classification() && record.isFullSwedishText())) return null;

            List<String> terms = record.getLanguageSpecificTerms("swe");
            if (addAffiliationFeatures) terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());
            ;

            StringBuilder stringBuilder = new StringBuilder();
            for(String t : terms) {


                stringBuilder.append(t.replaceAll(" ","_"));
                stringBuilder.append(" ");

            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\t");
            for(Integer c : finalClassLabels) {

                stringBuilder.append(c);
                stringBuilder.append(",");

            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            return stringBuilder.toString();


        } else if (level == 3 && language.equals("swe")) {

            if (!(record.containsLevel3Classification() && record.isFullSwedishText())) return null;


            List<String> terms = record.getLanguageSpecificTerms("swe");
            if (addAffiliationFeatures) terms.addAll(record.getTermsFromAffiliation());
            terms.addAll(record.getTermsFromHost());
            terms.addAll(record.getUnkontrolledKkeywords());
            String ISBN = record.getISBN();
            if (ISBN != null) terms.add(ISBN);
            terms.addAll(record.getIssn());

            StringBuilder stringBuilder = new StringBuilder();
            for(String t : terms) {


                stringBuilder.append(t.replaceAll(" ","_"));
                stringBuilder.append(" ");

            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("\t");
            for(Integer c : finalClassLabels) {

                stringBuilder.append(c);
                stringBuilder.append(",");

            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            return stringBuilder.toString();

        }


        return null;

    }


    public static void main(String[] arg) throws IOException, InterruptedException {

        /*
        FileHashDB fileHashDBSwePub = new FileHashDB();
        //fileHashDB.setPathToFile("E:\\Desktop\\JSON_SWEPUB\\SWEPUB20210421.db");
        fileHashDBSwePub.setPathToFile("E:\\swepub_20220405\\swepub20220405.db");
        fileHashDBSwePub.create();

        JsonSwePubParser jsonSwePubParser = new JsonSwePubParser("E:\\swepub_20220405\\swepub-deduplicated.jsonl");
        jsonSwePubParser.parse(fileHashDBSwePub);

        fileHashDBSwePub.closeDatabase();

         */

        FileHashDB fileHashDB = new FileHashDB();
        fileHashDB.setPathToFile("E:\\swepub_20220405\\\\swepub20220405.db");
        fileHashDB.createOrOpenDatabase();


        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( new File("sparSpaceR.txt") ), StandardCharsets.UTF_8) );

        //int count=0;
        for (Map.Entry<String, Record> r : fileHashDB.database.entrySet()) {

            Record record = r.getValue();

            if(record.isAutoClassedBySwepub()) continue; // 2 to 3 % is autoclassed by swepub or around 33,000 records
            String s = toStarSpace(record,3,"eng",false) ;
            if(s == null) continue;
            writer.write( s  );
            writer.newLine();


          //  count++;
           // if(count >= 20) break;
        }

        writer.flush();
        writer.close();
        fileHashDB.closeDatabase();

    }
}
