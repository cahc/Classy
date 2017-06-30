package misc.LanguageTools;

import java.util.HashMap;

/**
 * Created by Cristian on 2016-11-24.
 */
public class HtmlUnescaper {

    //NOTE: Swepub data is fucked..

    private static HashMap<String, String> htmlEntities;

    static {
        htmlEntities = new HashMap<String, String>();
        //for wierd stuff in Swepub data
        //htmlEntities.put("&amp;aring;", "å");
       // htmlEntities.put("&amp;Aring;", "Å");
       // htmlEntities.put("&amp;auml;","ä");
       // htmlEntities.put("&amp;Auml;","Ä");
       // htmlEntities.put("&amp;ouml;", "ö");
       // htmlEntities.put("&amp;Ouml;", "Ö");
       //NO, see solution with i insted of i+1 insted
        //some normal..

        htmlEntities.put("&lt;", "<");
        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&agrave;", "à");
        htmlEntities.put("&Agrave;", "À");
        htmlEntities.put("&acirc;", "â");
        htmlEntities.put("&auml;", "ä");
        htmlEntities.put("&Auml;", "Ä");
        htmlEntities.put("&Acirc;", "Â");
        htmlEntities.put("&aring;", "å");
        htmlEntities.put("&Aring;", "Å");
        htmlEntities.put("&aelig;", "æ");
        htmlEntities.put("&AElig;", "Æ");
        htmlEntities.put("&ccedil;", "ç");
        htmlEntities.put("&Ccedil;", "Ç");
        htmlEntities.put("&eacute;", "é");
        htmlEntities.put("&Eacute;", "É");
        htmlEntities.put("&egrave;", "è");
        htmlEntities.put("&Egrave;", "È");
        htmlEntities.put("&ecirc;", "ê");
        htmlEntities.put("&Ecirc;", "Ê");
        htmlEntities.put("&euml;", "ë");
        htmlEntities.put("&Euml;", "Ë");
        htmlEntities.put("&iuml;", "ï");
        htmlEntities.put("&Iuml;", "Ï");
        htmlEntities.put("&ocirc;", "ô");
        htmlEntities.put("&Ocirc;", "Ô");
        htmlEntities.put("&ouml;", "ö");
        htmlEntities.put("&Ouml;", "Ö");
        htmlEntities.put("&oslash;", "ø");
        htmlEntities.put("&Oslash;", "Ø");
        htmlEntities.put("&szlig;", "ß");
        htmlEntities.put("&ugrave;", "ù");
        htmlEntities.put("&Ugrave;", "Ù");
        htmlEntities.put("&ucirc;", "û");
        htmlEntities.put("&Ucirc;", "Û");
        htmlEntities.put("&uuml;", "ü");
        htmlEntities.put("&Uuml;", "Ü");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");
        htmlEntities.put("&rdquo;","\"");
        htmlEntities.put("&ldquo;","\"");

    }


    /*
   Here the original recursive version.
   It is fine unless you pass a big string then a Stack Overflow is possible :-(
 */

  public static final String unescapeHTML1(String source, int start){
     int i,j;

     i = source.indexOf("&", start);
     if (i > -1) {
        j = source.indexOf(";" ,i);
        if (j > i) {
           String entityToLookFor = source.substring(i , j + 1);
           String value = htmlEntities.get(entityToLookFor);
           if (value != null) {
             source = new StringBuffer().append(source.substring(0 , i))
                                   .append(value)
                                   .append(source.substring(j + 1))
                                   .toString();
             return unescapeHTML1(source, i ); // recursive call should be i+1 but this works for fucked up Swepub data as in: &amp;ouml;
           } else {return unescapeHTML1(source,i+1); }
         }
     }
     return source;
  }


    /*
   M. McNeely Jr. has sent a version with do...while()loop which is more robust.
   Thanks to him!
*/

    public static final String unescapeHTML2(String source) {
        int i, j;

        boolean continueLoop;
        int skip = 0;
        do {
            continueLoop = false;
            i = source.indexOf("&", skip);
            if (i > -1) {
                j = source.indexOf(";", i);
                if (j > i) {
                    String entityToLookFor = source.substring(i, j + 1);
                    String value = htmlEntities.get(entityToLookFor);
                    if (value != null) {
                        source = source.substring(0, i)
                                + value + source.substring(j + 1);
                        continueLoop = true;
                    }
                    else if (value == null){
                        skip = i+1;
                        continueLoop = true;
                    }
                }
            }
        } while (continueLoop);
        return source;
    }

}