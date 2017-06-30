package misc.LanguageTools;
import me.champeau.ld.UberLanguageDetector;

import java.util.HashSet;

/**
 * Created by Cristian on 2016-10-27.
 */
public class LanguageGuesser {

    private static final UberLanguageDetector detector = UberLanguageDetector.getInstance();


    private static final  HashSet<String> onlyEnglishAndSwedish = new HashSet<>();
     static {
         onlyEnglishAndSwedish.add("sv");
         onlyEnglishAndSwedish.add("en");
    }



    public static String getLanguage(String s) {

        return detector.detectLang(s);

    }

    public static String getRestrictedLanguage(String s) {

        return detector.detectLang(s,onlyEnglishAndSwedish);

    }

}
