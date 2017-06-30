package misc.Stemmers;

/**
 * Created by Cristian on 2016-10-21.
 *
 *
 * Based on Report on CLEF-2013 Monolingual tracks
 *
 */
public class SwedishStemmer implements Stemmer {

    /**
     * Returns true if the character array ends with the suffix.
     *
     * @param s Input Buffer
     * @param len length of input buffer
     * @param suffix Suffix string to test
     * @return true if <code>s</code> ends with <code>suffix</code>
     */
    private final static boolean endsWith(char s[], int len, String suffix) {
        final int suffixLen = suffix.length();
        if (suffixLen > len)
            return false;
        for (int i = suffixLen - 1; i >= 0; i--)
            if (s[len -(suffixLen - i)] != suffix.charAt(i))
                return false;

        return true;
    }

    /**
     * Returns true if the character array ends with the suffix.
     *
     * @param s Input Buffer
     * @param len length of input buffer
     * @param suffix Suffix string to test
     * @return true if <code>s</code> ends with <code>suffix</code>
     */
    private final static boolean endsWith(char s[], int len, char suffix[]) {
        final int suffixLen = suffix.length;
        if (suffixLen > len)
            return false;
        for (int i = suffixLen - 1; i >= 0; i--)
            if (s[len -(suffixLen - i)] != suffix[i])
                return false;

        return true;
    }


    private final static int stem(char s[], int len) {
        if (len > 4 && s[len-1] == 's')
            len--;

        if (len > 7 &&
                (endsWith(s, len, "elser") ||
                        endsWith(s, len, "heten")))
            return len - 5;

        if (len > 6 &&
                (endsWith(s, len, "arne") ||
                        endsWith(s, len, "erna") ||
                        endsWith(s, len, "ande") ||
                        endsWith(s, len, "else") ||
                        endsWith(s, len, "aste") ||
                        endsWith(s, len, "orna") ||
                        endsWith(s, len, "aren")))
            return len - 4;

        if (len > 5 &&
                (endsWith(s, len, "are") ||
                        endsWith(s, len, "ast") ||
                        endsWith(s, len, "het")))
            return len - 3;

        if (len > 4 &&
                (endsWith(s, len, "ar") ||
                        endsWith(s, len, "er") ||
                        endsWith(s, len, "or") ||
                        endsWith(s, len, "en") ||
                        endsWith(s, len, "at") ||
                        endsWith(s, len, "te") ||
                        endsWith(s, len, "et")))
            return len - 2;

        //TODO fixme. This whole stemmer is half-broken
      //  if (len > 3)
      //      switch(s[len-1]) {
      //          case 't':
      //          case 'a':
      //          case 'e':
     //           case 'n': return len - 1;
     //       }

        return len;
    }



    public SwedishStemmer() {}

    public String stem(String s) {

        int length = s.length();
        char[] charArray = s.toCharArray();
        int newLength = stem(charArray,length);

        return s.substring(0,newLength);


    }


}
