package SwePub;

import java.util.*;

public class SSIF2025ADAPTER {

    HashMap<Integer,SSIF2011toSSIF2025> map2011to2015 = new HashMap<>();

    public SSIF2025ADAPTER() {

        //FLYTTADE POSTER

        map2011to2015.put(10306, new SSIF2011toSSIF2025(10306, new HashSet<>(Arrays.asList(10301)),"10306 (SSIF2011) mapped to 10301 (SSIF2025)" ) );
        map2011to2015.put(10603, new SSIF2011toSSIF2025(10603, new HashSet<>(Arrays.asList(10307)),"10603 (SSIF2011) mapped to 10307 (SSIF2025)" ) );
        map2011to2015.put(20108, new SSIF2011toSSIF2025(20108, new HashSet<>(Arrays.asList(20102)),"20108 (SSIF2011) mapped to 20102 (SSIF2025)" ) );
        map2011to2015.put(20303, new SSIF2011toSSIF2025(20303, new HashSet<>(Arrays.asList(20302)),"20303 (SSIF2011) mapped to 20302 (SSIF2025)" ) );
        map2011to2015.put(20308, new SSIF2011toSSIF2025(20308, new HashSet<>(Arrays.asList(20399)),"20308 (SSIF2011) mapped to 20399 (SSIF2025)" ) );
        map2011to2015.put(20404, new SSIF2011toSSIF2025(20404, new HashSet<>(Arrays.asList(20499)),"20404 (SSIF2011) mapped to 20499 (SSIF2025)" ) );
        map2011to2015.put(20701, new SSIF2011toSSIF2025(20701, new HashSet<>(Arrays.asList(20703)),"20701 (SSIF2011) mapped to 20703 (SSIF2025)" ) );
        map2011to2015.put(20706, new SSIF2011toSSIF2025(20706, new HashSet<>(Arrays.asList(20705)),"20706 (SSIF2011) mapped to 20705 (SSIF2025)" ) );
        map2011to2015.put(20804, new SSIF2011toSSIF2025(20804, new HashSet<>(Arrays.asList(20899)),"20804 (SSIF2011) mapped to 20899 (SSIF2025)" ) );
        map2011to2015.put(20907, new SSIF2011toSSIF2025(20907, new HashSet<>(Arrays.asList(20901)),"20907 (SSIF2011) mapped to 20901 (SSIF2025)" ) );
        map2011to2015.put(20908, new SSIF2011toSSIF2025(20908, new HashSet<>(Arrays.asList(20905)),"20908 (SSIF2011) mapped to 20905 (SSIF2025)" ) );
        map2011to2015.put(30302, new SSIF2011toSSIF2025(30302, new HashSet<>(Arrays.asList(30116,30311)),"30302 (SSIF2011) mapped to 30116,30311 (SSIF2025)" ) );
        map2011to2015.put(40108, new SSIF2011toSSIF2025(40108, new HashSet<>(Arrays.asList(40505)),"40108 (SSIF2011) mapped to 40505 (SSIF2025)" ) );
        map2011to2015.put(40304, new SSIF2011toSSIF2025(40304, new HashSet<>(Arrays.asList(40399)),"40304 (SSIF2011) mapped to 40399 (SSIF2025)" ) );
        map2011to2015.put(40501, new SSIF2011toSSIF2025(40501, new HashSet<>(Arrays.asList(20904)),"40501 (SSIF2011) mapped to 20904 (SSIF2025)" ) );
        map2011to2015.put(40503, new SSIF2011toSSIF2025(40503, new HashSet<>(Arrays.asList(40599)),"40503 (SSIF2011) mapped to 40599 (SSIF2025)" ) );
        map2011to2015.put(50403, new SSIF2011toSSIF2025(50403, new HashSet<>(Arrays.asList(50401)),"50403 (SSIF2011) mapped to 50401 (SSIF2025)" ) );
        map2011to2015.put(50602, new SSIF2011toSSIF2025(50602, new HashSet<>(Arrays.asList(50905)),"50602 (SSIF2011) mapped to 50905 (SSIF2025)" ) );
        map2011to2015.put(50603, new SSIF2011toSSIF2025(50603, new HashSet<>(Arrays.asList(50703)),"50603 (SSIF2011) mapped to 50703 (SSIF2025)" ) );
        map2011to2015.put(50802, new SSIF2011toSSIF2025(50802, new HashSet<>(Arrays.asList(50801)),"50802 (SSIF2011) mapped to 50801 (SSIF2025)" ) );
        map2011to2015.put(50803, new SSIF2011toSSIF2025(50803, new HashSet<>(Arrays.asList(50804)),"50803 (SSIF2011) mapped to 50804 (SSIF2025)" ) );
        map2011to2015.put(60305, new SSIF2011toSSIF2025(60305, new HashSet<>(Arrays.asList(60104)),"60305 (SSIF2011) mapped to 60104 (SSIF2025)" ) );
        map2011to2015.put(60401, new SSIF2011toSSIF2025(60401, new HashSet<>(Arrays.asList(60411)),"60401 (SSIF2011) mapped to 60411 (SSIF2025)" ) );
        map2011to2015.put(60402, new SSIF2011toSSIF2025(60402, new HashSet<>(Arrays.asList(60412)),"60402 (SSIF2011) mapped to 60412 (SSIF2025)" ) );
        map2011to2015.put(60403, new SSIF2011toSSIF2025(60403, new HashSet<>(Arrays.asList(60413)),"60403 (SSIF2011) mapped to 60413 (SSIF2025)" ) );
        map2011to2015.put(60404, new SSIF2011toSSIF2025(60404, new HashSet<>(Arrays.asList(60414)),"60404 (SSIF2011) mapped to 60414 (SSIF2025)" ) );
        map2011to2015.put(60405, new SSIF2011toSSIF2025(60405, new HashSet<>(Arrays.asList(60415)),"60405 (SSIF2011) mapped to 60415 (SSIF2025)" ) );
        map2011to2015.put(60406, new SSIF2011toSSIF2025(60406, new HashSet<>(Arrays.asList(60416)),"60406 (SSIF2011) mapped to 60416 (SSIF2025)" ) );
        map2011to2015.put(60501, new SSIF2011toSSIF2025(60501, new HashSet<>(Arrays.asList(60105)),"60501 (SSIF2011) mapped to 60105 (SSIF2025)" ) );

        //FÖRGRENINGAR

        map2011to2015.put(10209, new SSIF2011toSSIF2025(10209, new HashSet<>(Arrays.asList(10202)),"10209 (SSIF2011) mapped to 10202 (SSIF2025)" ) );
        map2011to2015.put(10602, new SSIF2011toSSIF2025(10602, new HashSet<>(Arrays.asList(10408,10616)),"10602 (SSIF2011) mapped to 10408,10616 (SSIF2025)" ) );
        map2011to2015.put(20401, new SSIF2011toSSIF2025(20401, new HashSet<>(Arrays.asList(20405,20406)),"20401 (SSIF2011) mapped to 20405,20406 (SSIF2025)" ) );
        map2011to2015.put(21001, new SSIF2011toSSIF2025(21001, new HashSet<>(Arrays.asList(210)),"21001 (SSIF2011) mapped to 210 (SSIF2025)" ) );
        map2011to2015.put(21101, new SSIF2011toSSIF2025(21101, new HashSet<>(Arrays.asList(20909,20407)),"21101 (SSIF2011) mapped to 20909,20407 (SSIF2025)" ) );
        map2011to2015.put(21102, new SSIF2011toSSIF2025(21102, new HashSet<>(Arrays.asList(21199)),"21102 (SSIF2011) mapped to 21199 (SSIF2025)" ) );
        map2011to2015.put(21103, new SSIF2011toSSIF2025(21103, new HashSet<>(Arrays.asList(21199)),"21103 (SSIF2011) mapped to 21199 (SSIF2025)" ) );
        map2011to2015.put(30210, new SSIF2011toSSIF2025(30210, new HashSet<>(Arrays.asList(30225,30226)),"30210 (SSIF2011) mapped to 30225,30226 (SSIF2025)" ) );
        map2011to2015.put(30214, new SSIF2011toSSIF2025(30214, new HashSet<>(Arrays.asList(30228, 30229)),"30214 (SSIF2011) mapped to 30228, 30229 (SSIF2025)" ) );
        map2011to2015.put(50303, new SSIF2011toSSIF2025(50303, new HashSet<>(Arrays.asList(50301,50302)),"50303 (SSIF2011) mapped to 50301,50302 (SSIF2025)" ) );
        map2011to2015.put(50502, new SSIF2011toSSIF2025(50502, new HashSet<>(Arrays.asList(50406,50503)),"50502 (SSIF2011) mapped to 50406,50503 (SSIF2025)" ) );
        map2011to2015.put(50901, new SSIF2011toSSIF2025(50901, new HashSet<>(Arrays.asList(50999)),"50901 (SSIF2011) mapped to 50999 (SSIF2025)" ) );

        //FLYTTADE DELMÄNGDER - ENDAST INFO

        map2011to2015.put(10106, new SSIF2011toSSIF2025(10106, new HashSet<>(Arrays.asList(10106)),"Delmängden Statistik med medicinska aspekter flyttar ur Sannolikhetsteori och statistik 10106. Se 30118 och 50907 (SSIF2025" ) );
        map2011to2015.put(10106, new SSIF2011toSSIF2025(10106, new HashSet<>(Arrays.asList(10106)),"Delmängden Statistik med samhällsvetenskapliga aspekter flyttar ur Sannolikhetsteori och statistik 10106. Se 50907 (SSIF2025)" ) );
        map2011to2015.put(10207, new SSIF2011toSSIF2025(10207, new HashSet<>(Arrays.asList(10207)),"Delmängden Datavetenskapliga aspekter flyttar ur Datorseende och robotik (autonoma system) 10207. Se 20208 (SSIF2025)" ) );
        map2011to2015.put(20306, new SSIF2011toSSIF2025(20306, new HashSet<>(Arrays.asList(20306)),"Delmängden Akustik flyttar ur Strömningsmekanik och akustik 20306. Se 20399 (SSIF2025)" ) );
        map2011to2015.put(20399, new SSIF2011toSSIF2025(20399, new HashSet<>(Arrays.asList(20399)),"Delmängden Industriell ekonomi flyttar ur Annan maskinteknik 20399. Se 20310 (SSIF2025)" ) );
        map2011to2015.put(20601, new SSIF2011toSSIF2025(20601, new HashSet<>(Arrays.asList(20601)),"Delmängden Mätteknik flyttar ur Medicinsk laboratorie- och mätteknik 20601. Se 20604 (SSIF2025)" ) );
        map2011to2015.put(40107, new SSIF2011toSSIF2025(40107, new HashSet<>(Arrays.asList(40107)),"Delmängden Fiskpopulationsekologi flyttar ur Fisk- och akvakulturforskning 40107. Se 40502 (SSIF2025)" ) );
        map2011to2015.put(50401, new SSIF2011toSSIF2025(50401, new HashSet<>(Arrays.asList(50401)),"Delmängden Demografi flyttar ur Sociologi 50401. Se 50405 (SSIF2025)" ) );
        map2011to2015.put(50702, new SSIF2011toSSIF2025(50702, new HashSet<>(Arrays.asList(50702)),"Delmängden Turism flyttar ur Ekonomisk geografi 50702. Se 50703 (SSIF2025)" ) );




    }


    public static class SSIF2011toSSIF2025 {

        private final Integer SSIF2011;

        private final Set<Integer> SSIF2025;

        String info;


        public SSIF2011toSSIF2025(Integer SSIF2011, Set<Integer> SSIF2025, String info) {
            this.SSIF2011 = SSIF2011;
            this.SSIF2025 = SSIF2025;
            this.info = info;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SSIF2011toSSIF2025 that = (SSIF2011toSSIF2025) o;
            return Objects.equals(SSIF2011, that.SSIF2011) && Objects.equals(SSIF2025, that.SSIF2025) && Objects.equals(info, that.info);
        }

        @Override
        public int hashCode() {
            return Objects.hash(SSIF2011, SSIF2025, info);
        }


        @Override
        public String toString() {
            return "SSIF2011toSSIF2025{" +
                    "SSIF2011=" + SSIF2011 +
                    ", SSIF2025=" + SSIF2025 +
                    ", info='" + info + '\'' +
                    '}';
        }

        public Integer getSSIF2011() {
            return SSIF2011;
        }

        public Set<Integer> getSSIF2025() {
            return SSIF2025;
        }

        public String getInfo() {
            return info;
        }
    }


    public SSIF2011toSSIF2025 get2011to2025Mapping(Integer oldCode) {


        return this.map2011to2015.get(oldCode);


    }



    public static void main(String[] arg) {





    }





}
