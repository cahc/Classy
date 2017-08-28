package Visualization;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crco0001 on 8/28/2017.
 */
public class DistinctColours {


    List<Color> distinctColors64;

    //http://godsnotwheregodsnot.blogspot.se/2012/09/color-distribution-methodology.html
    // List<Color> colorList = new arrayList
    // Color apa = new Color(2,3,4);

    public DistinctColours() {
        distinctColors64 = new ArrayList<Color>();


                distinctColors64.add(new Color(0, 0, 0));
                distinctColors64.add(new Color(255, 0, 86));
                distinctColors64.add(new Color(213, 255, 0));
                distinctColors64.add(new Color(1, 0, 103));
                distinctColors64.add(new Color(158, 0, 142));
                distinctColors64.add(new Color(14, 76, 161));
                distinctColors64.add(new Color(255, 229, 2));
                distinctColors64.add(new Color(0, 95, 57));
                distinctColors64.add(new Color(0, 255, 0));
                distinctColors64.add(new Color(149, 0, 58));
                distinctColors64.add(new Color(255, 147, 126));
                distinctColors64.add(new Color(164, 36, 0));
                distinctColors64.add(new Color(0, 21, 68));
                distinctColors64.add(new Color(145, 208, 203));
                distinctColors64.add(new Color(98, 14, 0));
                distinctColors64.add(new Color(107, 104, 130));
                distinctColors64.add(new Color(0, 0, 255));
                distinctColors64.add(new Color(0, 125, 181));
                distinctColors64.add(new Color(106, 130, 108));
                distinctColors64.add(new Color(0, 174, 126));
                distinctColors64.add(new Color(194, 140, 159));
                distinctColors64.add(new Color(190, 153, 112));
                distinctColors64.add(new Color(0, 143, 156));
                distinctColors64.add(new Color(95, 173, 78));
                distinctColors64.add(new Color(255, 0, 0));
                distinctColors64.add(new Color(255, 0, 246));
                distinctColors64.add(new Color(255, 2, 157));
                distinctColors64.add(new Color(104, 61, 59));
                distinctColors64.add(new Color(255, 116, 163));
                distinctColors64.add(new Color(150, 138, 232));
                distinctColors64.add(new Color(152, 255, 82));
                distinctColors64.add(new Color(167, 87, 64));
                distinctColors64.add(new Color(1, 255, 254));
                distinctColors64.add(new Color(255, 238, 232));
                distinctColors64.add(new Color(254, 137, 0));
                distinctColors64.add(new Color(189, 198, 255));
                distinctColors64.add(new Color(1, 208, 255));
                distinctColors64.add(new Color(187, 136, 0));
                distinctColors64.add(new Color(117, 68, 177));
                distinctColors64.add(new Color(165, 255, 210));
                distinctColors64.add(new Color(255, 166, 254));
                distinctColors64.add(new Color(119, 77, 0));
                distinctColors64.add(new Color(122, 71, 130));
                distinctColors64.add(new Color(38, 52, 0));
                distinctColors64.add(new Color(0, 71, 84));
                distinctColors64.add(new Color(67, 0, 44));
                distinctColors64.add(new Color(181, 0, 255));
                distinctColors64.add(new Color(255, 177, 103));
                distinctColors64.add(new Color(255, 219, 102));
                distinctColors64.add(new Color(144, 251, 146));
                distinctColors64.add(new Color(126, 45, 210));
                distinctColors64.add(new Color(189, 211, 147));
                distinctColors64.add(new Color(229, 111, 254));
                distinctColors64.add(new Color(222, 255, 116));
                distinctColors64.add(new Color(0, 255, 120));
                distinctColors64.add(new Color(0, 155, 255));
                distinctColors64.add(new Color(0, 100, 1));
                distinctColors64.add(new Color(0, 118, 255));
                distinctColors64.add(new Color(133, 169, 0));
                distinctColors64.add(new Color(0, 185, 23));
                distinctColors64.add(new Color(120, 130, 49));
                distinctColors64.add(new Color(0, 255, 198));
                distinctColors64.add(new Color(255, 110, 65));
                distinctColors64.add(new Color(232, 94, 190));

            }


            public Color getColor(int cluster) {


                return this.distinctColors64.get(cluster);
            }


        }

