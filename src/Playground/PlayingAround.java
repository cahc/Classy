package Playground;

import Database.ModsOnlineParser;
import SwePub.Record;
import WebApp.ClassProbPair;
import WebApp.FetchFromDiVA;
import misc.LanguageTools.RemoveCopyRightFromAbstract;
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


        String newString = RemoveCopyRightFromAbstract.cleanedAbstract("Air pollution levels (NO2, PAHs,O-3) were investigated, before (BLE)and after (ALE) leaf emergence, in the urban landscape of Gothenburg, Sweden. The aims were to study the 1) spatial and temporal variation in pollution levels between urban green areas, 2) effect of urban vegetation on air pollution levels at the same distance from a major emission source (traffic route), 3) improvement of urban air quality in urban parks compared to adjacent sites near traffic, 4) correlation between air pollution and noise in a park. O-3 varied little over the urban landscape. NO2 varied strongly and was higher in situations strongly influenced by traffic. Four PAH variables were included: total PAH, total particle-bound PAH, the quantitatively important gaseous phenanthrene and the highly toxic particle-bound benzo(a)pyrene. The variation of PAHs was similar to NO2, but for certain PAHs the difference between highly and less polluted sites was larger than for NO2. At a vegetated site, NO2 and particulate PAH levels were lower than at a non-vegetated site at a certain distance from a busy traffic route. This effect was significantly larger ALE compared to BLE for NO2, indicating green leaf area to be highly significant factor for air quality improvement. For particulate PAHs, the effect was similar BLE and ALE, indicating that tree bark and branches also could be an important factor in reducing air pollution. Parks represented considerably cleaner local environments (park effect), which is likely to be a consequence of both a dilution (distance effect) and deposition. Noise and air pollution (NO2 and PAH) levels were strongly correlated. Comparison of noise levels BLE and ALE also showed that the presence of leaves significantly reduced noise levels. Our results are evidence that urban green spaces are beneficial for urban environmental quality, which is important to consider in urban planning. (C) 2017 Elsevier B.V. All rights reserved.");

        System.out.println(newString);

    }
}
