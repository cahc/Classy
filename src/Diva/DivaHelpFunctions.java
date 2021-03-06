package Diva;

import org.jsoup.Jsoup;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by crco0001 on 5/11/2016.
 */
public class DivaHelpFunctions {

    //split authors from the name field
    private final static Pattern splitAutAndAffil = Pattern.compile(";(?! |\\))(?![^\\(]*\\))");
    private final static Pattern extractControledDivaAffiliation = Pattern.compile("(?<=\\().+?\\[\\d+?\\](?=\\))");
    private final static Pattern umuAdressNumnerLowestInTheHierarchy = Pattern.compile("(?<=\\[)\\d+(?=\\]$)"  );
    private final static Pattern extractISBNprefix = Pattern.compile("97\\d-\\d+\\-\\d+");
    private final static Pattern extractCas = Pattern.compile("(?<=\\[)[a-z]+[0-9]+(?=\\])",Pattern.CASE_INSENSITIVE);
    private final static Pattern ISSN = Pattern.compile("\\d{4}-\\d{3}[\\d|x|X]");
    private final static Pattern seriesName = Pattern.compile("[^;]+");
    private final static Pattern divaISBN = Pattern.compile("[0-9xX-]+");
    private final static Pattern replaceMultipleLeftParenthesis = Pattern.compile("\\({2,}");
    private final static Pattern replaceMultipleRightParenthesis = Pattern.compile("\\){2,}(?!;)");
    private final static Pattern extractName = Pattern.compile("^.+?(?=\\[|\\(|$)");


    public static boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }


    public static String simplifyString(String s) {

        //based on this: https://blog.mafr.de/2015/10/10/normalizing-text-in-java/
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        temp = temp.replaceAll("[^A-Za-z]", "");
        return temp.toLowerCase();

    }

    public static String stripHtmlTags(String html) {
        return Jsoup.parse(html).text();
    }

    public static String[] splitAuthorInformation(String name) {

        return splitAutAndAffil.split(name);

    }



    public static List<String> extractUmUadress(String name) {

        List<String> result = null;
        Matcher matcher = extractControledDivaAffiliation.matcher(name);

        if(matcher.find()) {

            result = new ArrayList<>(3);
            result.add(matcher.group());

            while(matcher.find()) {

                result.add(matcher.group());
            }

        }

        return result;
    }

    public static List<String> extractISSN(String input) {

        List<String> result = null;

        Matcher matcher = ISSN.matcher(input);

        if(matcher.find()) {

            result = new ArrayList<>(1);
            result.add(matcher.group());

            while(matcher.find()) {

                result.add(matcher.group());
            }

        }

        return result;
    }

    public static String extractSeriesName(String input) {

        Matcher matcher = seriesName.matcher(input);

        if(matcher.find()) { return matcher.group();} else return null;

    }

    public static Integer extractUmUAddressNumber(String address) {

        Integer number = null;
        Matcher m = umuAdressNumnerLowestInTheHierarchy.matcher(address);

        if(m.find()) number = Integer.valueOf(m.group());


        return number;
    }

    public static String extractCas(String name) {

        String cas = null;
        Matcher matcher = extractCas.matcher(name);

        if(matcher.find()) cas = matcher.group().toLowerCase();

        return cas;

    }

    public static String extractName(String name) {

        String authorName = null;

        Matcher matcher = extractName.matcher(name);

        if(matcher.find()) authorName = matcher.group();

        return authorName;
    }

    public static String fixMessyNameFieldInDiva(String s) {

       s = replaceMultipleLeftParenthesis.matcher(s).replaceAll("(");
       s= replaceMultipleRightParenthesis.matcher(s).replaceAll(")");

        int leftParenthesis = 0;
        int rightParenthesis = 0;

        StringBuilder builder = new StringBuilder(s);

        for (int currentIndex = 0; currentIndex < builder.length(); currentIndex++) {

            char currentChar = builder.charAt(currentIndex);
            if (currentChar == '(') leftParenthesis++;
            if (currentChar == ')') rightParenthesis++;

            if (currentChar == ';' && leftParenthesis != rightParenthesis) {
                builder.setCharAt(currentIndex, ','); // replace with ,
            }


        }

        return builder.toString();
    }

    public static boolean matchedCas(String name, String cas) {

        /*

        this is a function that checks if an author is connected to a CAS that of interest, returns true if so, otherwise false!

         */

        return name.toLowerCase().contains(cas.toLowerCase());


    }



}

