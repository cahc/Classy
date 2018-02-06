package Playground;

import Database.ModsOnlineParser;
import SwePub.Record;
import WebApp.ClassProbPair;
import WebApp.FetchFromDiVA;
import misc.LanguageTools.RemoveCopyRightFromAbstract;
import net.openhft.hashing.LongHashFunction;
import smile.data.SparseDataset;

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


        SparseDataset test = new SparseDataset();

        test.set(0,0,10);
        test.set(0,1,100);
        test.set(1,1,100);
        test.set(4,4,100);
        System.out.println(test.size() +" " + test.ncols());

        System.out.println( test.toSparseMatrix() );

        System.out.println( test.toSparseMatrix().transpose() );

    }
}
