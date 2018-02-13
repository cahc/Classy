package Playground;

import Database.ModsOnlineParser;
import Diva.VectorAndSim;
import Diva.VectorWithID;
import SwePub.Record;
import WebApp.ClassProbPair;
import WebApp.FetchFromDiVA;
import com.google.common.collect.MinMaxPriorityQueue;
import jsat.linear.SparseVector;
import misc.LanguageTools.RemoveCopyRightFromAbstract;
import net.openhft.hashing.LongHashFunction;
import smile.data.SparseDataset;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cristian on 2017-04-18.
 */
public class PlayingAround {


    public static void main(String[] arg) throws IOException, XMLStreamException {



        Integer[] docFreq = new Integer[ 10 ];

        for(int i=0; i<docFreq.length; i++ ) docFreq[i] = 0;

        docFreq[5]++;

        docFreq[9]++;
        docFreq[9]++;
         System.out.println( Arrays.asList(docFreq) );



        }


    }
