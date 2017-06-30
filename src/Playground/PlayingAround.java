package Playground;

import Database.ModsOnlineParser;
import SwePub.Record;
import WebApp.ClassProbPair;
import WebApp.FetchFromDiVA;
import net.openhft.hashing.LongHashFunction;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cristian on 2017-04-18.
 */
public class PlayingAround {


    public static void main(String[] arg) throws IOException, XMLStreamException {


        long hash = LongHashFunction.xx().hashChars("hello200");

        System.out.println(hash);

        String regex = "\\d{1,20}";
        Pattern r = java.util.regex.Pattern.compile(regex);

        Matcher m = r.matcher("43535435" );
        if (m.find( )) {
            String divaNumber = m.group(0);

            System.out.println(divaNumber);
        }



    }
}
