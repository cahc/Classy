package misc.Parsers;

import misc.Stemmers.Stemmer;
import misc.stopwordLists.Stopwordlist;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cristian on 2016-10-27.
 */
public class SimpleParser {


    private static final Pattern extractBasic1 = Pattern.compile("\\p{L}+"); //sqeuence of characters UTF-8
    private static final Pattern extractBasic2 = Pattern.compile("[0-9]{1,4}-[^\\p{Z}]+\\p{L}+"); //starting with number-
    private static final Pattern extractBasic3 = Pattern.compile("\\p{L}+-\\p{L}+-?\\p{L}+"); //sequence starting with character containing - ending with charachter

    //contains at least two "normal" adjacent characters?
    private static final Pattern containsAtLeastTwoAdjacentCharacters = Pattern.compile("[a-z]{2,20}", Pattern.CASE_INSENSITIVE);

    // contains shit?
    public static final Pattern noise = Pattern.compile("[=\\*&><\\/|\\\\]");

    private static List<String> initialParser(String s) {
        List<String> list = new ArrayList<>();

        Matcher matcher = extractBasic1.matcher(s);

        while (matcher.find()) {

            list.add(matcher.group(0));
        }

        matcher = extractBasic2.matcher(s);

        while (matcher.find()) {

            list.add(matcher.group(0));
        }

        matcher = extractBasic3.matcher(s);

        while (matcher.find()) {

            list.add(matcher.group(0));
        }


        return list;
    }


    private static final boolean toShort(String s) {

        if (s.length() <= 2) return true;
        return false;

    }

    private static final boolean notAWord(String s) {

        Matcher matcher = noise.matcher(s);

        if (matcher.find()) return true;

        Matcher matcher1 = containsAtLeastTwoAdjacentCharacters.matcher(s);

        if (matcher1.find()) return false;


        return true;

    }

    public static final boolean isBalanced(String s) {

        long opening = s.chars().filter(ch -> ch == '(').count();
        long closing = s.chars().filter(ch -> ch == ')').count();

        long opening2 = s.chars().filter(ch -> ch == '[').count();
        long closing2 = s.chars().filter(ch -> ch == ']').count();

        return ((opening == closing) && (opening2 == closing2));

    }


    public static List<String> parse(String rawInput, boolean tolowercase, Stopwordlist stopwordlist, Stemmer stem) {


        List<String> potentialTerms = initialParser(rawInput);

        ArrayList<String> termsFinal = new ArrayList<>(20);

        for (String s : potentialTerms) {

            if (toShort(s)) continue;
            if( notAWord(s) ) continue;
            if( !isBalanced(s) ) continue;


            if (tolowercase) {
                String finalTerm = s.toLowerCase();
                finalTerm = stem.stem(finalTerm);
                if (!stopwordlist.isStopword(finalTerm)) termsFinal.add(finalTerm);

            } else {
                s = stem.stem(s);
                if (!stopwordlist.isStopword(s)) termsFinal.add(s);
            }

        }

            return termsFinal;
        }


    }


