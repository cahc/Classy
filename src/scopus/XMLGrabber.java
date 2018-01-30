package scopus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by crco0001 on 1/18/2018.
 */
public class XMLGrabber {

    private final Pattern pattern = Pattern.compile("2-s2\\.0-[0-9]+", Pattern.CASE_INSENSITIVE);

    static class UnexpectedCodeException extends Exception
    {
        // Parameterless Constructor
        public UnexpectedCodeException() {}

        // Constructor that accepts a message
        public UnexpectedCodeException(String message)
        {
            super("Unexpected code returned: " + message);
        }
    }

    static class QuotaExceededException extends Exception
    {
        // Parameterless Constructor
        public QuotaExceededException() {}

        // Constructor that accepts a message
        public QuotaExceededException(String message)
        {
            super("Quota exceeded! Aborting!");
        }
    }


    OkHttpClient client;
    private static int maxRetries = 3;

    public List<String> readEidsFromFile(String file) throws IOException {

        List<String> eids = new ArrayList<>();

        File file1 = new File(file);
        if(!file1.exists()) return Collections.emptyList();

        BufferedReader reader = new BufferedReader( new FileReader( file1 ));

        String line;
        while(  (line = reader.readLine()) != null  ) {


            if(line.startsWith("#")) continue;

            Matcher matcher = pattern.matcher(line);

            if(matcher.matches()) {

                eids.add( matcher.group(0) );

            }


        }



        reader.close();
        return eids;

    }

    public XMLGrabber(boolean useLocalProxy) {

          client = new OkHttpClient.Builder()
                  .connectTimeout(15, TimeUnit.SECONDS)
                  .writeTimeout(10, TimeUnit.SECONDS)
                  .readTimeout(30, TimeUnit.SECONDS)
                  .build();


        if(useLocalProxy) {

            System.setProperty("http.proxyHost", "127.0.0.1");
            System.setProperty("http.proxyPort", "8888");

            //need to fix with lib\security\cacerts for this to work..
          //  System.setProperty("https.proxyHost", "127.0.0.1");
          //  System.setProperty("https.proxyPort", "8888");
        }



    }

    public String generatURL(String eid, String key) {

     return "http://api.elsevier.com/content/abstract/eid/" + eid + "?apiKey=" + key +"&VIEW=FULL";

    }

    public String run(String url) throws InterruptedException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0.2 Safari/604.4.7")
                .url(url)
                .build();

        int count = 0;
        while(true) {
            try (Response response = this.client.newCall(request).execute()) {


                //429 : quota exceeded
                //200 : OK
                //500 : generic error
                //404 : resource not found

                int responseCode = response.code();

                if(responseCode == 429) throw new QuotaExceededException("Quota exeeded :-(");

                if(responseCode != 200) throw new UnexpectedCodeException(String.valueOf(responseCode));

                //break out or return here


                String responseText =  response.body().string();

                if(responseText == null) throw new UnexpectedCodeException("Response body to text was null!" );

                return responseText;
            }

            catch (QuotaExceededException exp) {

                System.out.println(exp.getLocalizedMessage());
                System.exit(0); // nothing to do..
            }


            catch (UnexpectedCodeException exp) {

                count++;
                System.out.println(exp.getLocalizedMessage());
                if(count >= maxRetries) { System.out.println("giving up"); System.exit(0); }

                Thread.sleep((long)(1000*10)); //sleep for 10 seconds
            }

            catch(Exception expOther) {

                    count++;
                    System.out.println("Something went wrong: " + expOther.getMessage() );
                    if(count >= maxRetries) { System.out.println("Giving up now"); System.exit(0); }
                    Thread.sleep((long)(1000*10)); //sleep for 10 seconds

                }



          } //while-loop

    }




    public static void main(String[] args) throws IOException, InterruptedException {
        XMLGrabber client = new XMLGrabber(false);


        if(args.length != 2) { System.out.println("Supply file with eids and a file with the APIkey"); System.exit(0); }

        BufferedReader reader = new BufferedReader( new FileReader(args[1]));

        String key = reader.readLine().trim();

         List<String> eids = client.readEidsFromFile(args[0]);

       System.out.println("Eids read: " + eids.size());

       int counter = 1;
       for(String eid : eids) {

           String url = client.generatURL(eid,key);

           String stringOfBody = client.run(url);

           BufferedWriter writer = new BufferedWriter( new FileWriter( new File(eid +".xml")));
           writer.write(stringOfBody);
           writer.flush();
           writer.close();
           System.out.println(eid + " downloaded (" + counter +") in total..");
           counter++;
           Thread.sleep(1*1500); //sleep for one second

       }


    }


}

