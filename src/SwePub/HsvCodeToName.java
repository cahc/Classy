package SwePub;

import java.util.HashMap;

/**
 * Created by Cristian on 2016-10-13.
 */
public class HsvCodeToName {



    public static Integer firstOneDigitOrNull(Integer x) {

        while(x > 9) {

            x = x/10;
        }

        if(x.compareTo(0) > 0) return x;
        return null; // null if input was less than three digits to begin with

    }

    public static Integer firstThreeDigitsOrNull(Integer x) {

        while (x > 999) {
            // while "more than 3 digits", "throw out last digit"
            x = x / 10;
        }


        if(x.compareTo(99) > 0) return x;
        return null; // null if input was less than three digits to begin with
    }

    public static Integer firstFiveDigitsOrNull(Integer x) {

        while (x > 99999) {
            // while "more than 5 digits", "throw out last digit"
            x = x / 10;
        }


        if(x.compareTo(9999) > 0) return x;
        return null; // null if input was less than five digits to begin with
    }

    private static final HashMap<Integer,ClassificationCategory> CODE_TO_CATEGORY = new HashMap<>();

    static {

        //Integer code --> Integer Code, int Level, String eng_name, String swe_name



        CODE_TO_CATEGORY.put(1, new ClassificationCategory(1, 1,"Natural sciences","Naturvetenskap"));
        CODE_TO_CATEGORY.put(101, new ClassificationCategory(101, 2,"Natural sciences --> Mathematics","Matematik"));
        CODE_TO_CATEGORY.put(10101, new ClassificationCategory(10101, 3,"Natural sciences --> Mathematics --> Mathematical Analysis","Matematisk analys"));
        CODE_TO_CATEGORY.put(10102, new ClassificationCategory(10102, 3,"Natural sciences --> Mathematics --> Geometry","Geometri"));
        CODE_TO_CATEGORY.put(10103, new ClassificationCategory(10103, 3,"Natural sciences --> Mathematics --> Algebra and Logic","Algebra och logik"));
        CODE_TO_CATEGORY.put(10104, new ClassificationCategory(10104, 3,"Natural sciences --> Mathematics --> Discrete Mathematics","Diskret matematik"));
        CODE_TO_CATEGORY.put(10105, new ClassificationCategory(10105, 3,"Natural sciences --> Mathematics --> Computational Mathematics","Beräkningsmatematik"));
        CODE_TO_CATEGORY.put(10106, new ClassificationCategory(10106, 3,"Natural sciences --> Mathematics --> Probability Theory and Statistics","Sannolikhetsteori och statistik"));
        CODE_TO_CATEGORY.put(10199, new ClassificationCategory(10199, 3,"Natural sciences --> Mathematics --> Other Mathematics","Annan matematik"));
        CODE_TO_CATEGORY.put(102, new ClassificationCategory(102, 2,"Natural sciences --> Computer and Information Sciences","Data- och informationsvetenskap (Datateknik)"));
        CODE_TO_CATEGORY.put(10201, new ClassificationCategory(10201, 3,"Natural sciences --> Computer and Information Sciences --> Computer Sciences","Datavetenskap (datalogi)"));
        CODE_TO_CATEGORY.put(10202, new ClassificationCategory(10202, 3,"Natural sciences --> Computer and Information Sciences --> Information Systems (Social aspects to be 50804)","Systemvetenskap, informationssystem och informatik (samhällsvetenskaplig inriktning under 50804)"));
        CODE_TO_CATEGORY.put(10203, new ClassificationCategory(10203, 3,"Natural sciences --> Computer and Information Sciences --> Bioinformatics (Computational Biology) (applications to be 10610)","Bioinformatik (beräkningsbiologi) (tillämpningar under 10610)"));
        CODE_TO_CATEGORY.put(10204, new ClassificationCategory(10204, 3,"Natural sciences --> Computer and Information Sciences --> Human Computer Interaction (Social aspects to be 50803)","Människa-datorinteraktion (interaktionsdesign) (Samhällsvetenskapliga aspekter under 50803)"));
        CODE_TO_CATEGORY.put(10205, new ClassificationCategory(10205, 3,"Natural sciences --> Computer and Information Sciences --> Software Engineering","Programvaruteknik"));
        CODE_TO_CATEGORY.put(10206, new ClassificationCategory(10206, 3,"Natural sciences --> Computer and Information Sciences --> Computer Engineering","Datorteknik"));
        CODE_TO_CATEGORY.put(10207, new ClassificationCategory(10207, 3,"Natural sciences --> Computer and Information Sciences --> Computer Vision and Robotics (Autonomous Systems)","Datorseende och robotik (autonoma system)"));
        CODE_TO_CATEGORY.put(10208, new ClassificationCategory(10208, 3,"Natural sciences --> Computer and Information Sciences --> Language Technology (Computational Linguistics)","Språkteknologi (språkvetenskaplig databehandling)"));
        CODE_TO_CATEGORY.put(10209, new ClassificationCategory(10209, 3,"Natural sciences --> Computer and Information Sciences --> Media and Communication Technology","Medieteknik"));
        CODE_TO_CATEGORY.put(10299, new ClassificationCategory(10299, 3,"Natural sciences --> Computer and Information Sciences --> Other Computer and Information Science","Annan data- och informationsvetenskap"));
        CODE_TO_CATEGORY.put(103, new ClassificationCategory(103, 2,"Natural sciences --> Physical Sciences","Fysik"));
        CODE_TO_CATEGORY.put(10301, new ClassificationCategory(10301, 3,"Natural sciences --> Physical Sciences --> Subatomic Physics","Subatomär fysik"));
        CODE_TO_CATEGORY.put(10302, new ClassificationCategory(10302, 3,"Natural sciences --> Physical Sciences --> Atom and Molecular Physics and Optics","Atom- och molekylfysik och optik"));
        CODE_TO_CATEGORY.put(10303, new ClassificationCategory(10303, 3,"Natural sciences --> Physical Sciences --> Fusion, Plasma and Space Physics","Fusion, plasma och rymdfysik"));
        CODE_TO_CATEGORY.put(10304, new ClassificationCategory(10304, 3,"Natural sciences --> Physical Sciences --> Condensed Matter Physics","Den kondenserade materiens fysik"));
        CODE_TO_CATEGORY.put(10305, new ClassificationCategory(10305, 3,"Natural sciences --> Physical Sciences --> Astronomy, Astrophysics and Cosmology","Astronomi, astrofysik och kosmologi"));
        CODE_TO_CATEGORY.put(10306, new ClassificationCategory(10306, 3,"Natural sciences --> Physical Sciences --> Accelerator Physics and Instrumentation","Acceleratorfysik och instrumentering"));
        CODE_TO_CATEGORY.put(10399, new ClassificationCategory(10399, 3,"Natural sciences --> Physical Sciences --> Other Physics Topics","Annan fysik"));
        CODE_TO_CATEGORY.put(104, new ClassificationCategory(104, 2,"Natural sciences --> Chemical Sciences","Kemi"));
        CODE_TO_CATEGORY.put(10401, new ClassificationCategory(10401, 3,"Natural sciences --> Chemical Sciences --> Analytical Chemistry","Analytisk kemi"));
        CODE_TO_CATEGORY.put(10402, new ClassificationCategory(10402, 3,"Natural sciences --> Chemical Sciences --> Physical Chemistry","Fysikalisk kemi"));
        CODE_TO_CATEGORY.put(10403, new ClassificationCategory(10403, 3,"Natural sciences --> Chemical Sciences --> Materials Chemistry","Materialkemi"));
        CODE_TO_CATEGORY.put(10404, new ClassificationCategory(10404, 3,"Natural sciences --> Chemical Sciences --> Inorganic Chemistry","Oorganisk kemi"));
        CODE_TO_CATEGORY.put(10405, new ClassificationCategory(10405, 3,"Natural sciences --> Chemical Sciences --> Organic Chemistry","Organisk kemi"));
        CODE_TO_CATEGORY.put(10406, new ClassificationCategory(10406, 3,"Natural sciences --> Chemical Sciences --> Polymer Chemistry","Polymerkemi"));
        CODE_TO_CATEGORY.put(10407, new ClassificationCategory(10407, 3,"Natural sciences --> Chemical Sciences --> Theoretical Chemistry","Teoretisk kemi"));
        CODE_TO_CATEGORY.put(10499, new ClassificationCategory(10499, 3,"Natural sciences --> Chemical Sciences --> Other Chemistry Topics","Annan kemi"));
        CODE_TO_CATEGORY.put(105, new ClassificationCategory(105, 2,"Natural sciences --> Earth and Related Environmental Sciences","Geovetenskap och miljövetenskap"));
        CODE_TO_CATEGORY.put(10501, new ClassificationCategory(10501, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Climate Research","Klimatforskning"));
        CODE_TO_CATEGORY.put(10502, new ClassificationCategory(10502, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Environmental Sciences (social aspects to be 507)","Miljövetenskap (Samhällsvetenskapliga aspekter under 507)"));
        CODE_TO_CATEGORY.put(10503, new ClassificationCategory(10503, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Geosciences, Multidisciplinary","Multidisciplinär geovetenskap"));
        CODE_TO_CATEGORY.put(10504, new ClassificationCategory(10504, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Geology","Geologi"));
        CODE_TO_CATEGORY.put(10505, new ClassificationCategory(10505, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Geophysics","Geofysik"));
        CODE_TO_CATEGORY.put(10506, new ClassificationCategory(10506, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Geochemistry","Geokemi"));
        CODE_TO_CATEGORY.put(10507, new ClassificationCategory(10507, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Physical Geography","Naturgeografi"));
        CODE_TO_CATEGORY.put(10508, new ClassificationCategory(10508, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Meteorology and Atmospheric Sciences","Meteorologi och atmosfärforskning"));
        CODE_TO_CATEGORY.put(10509, new ClassificationCategory(10509, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Oceanography, Hydrology and Water Resources","Oceanografi, hydrologi och vattenresurser"));
        CODE_TO_CATEGORY.put(10599, new ClassificationCategory(10599, 3,"Natural sciences --> Earth and Related Environmental Sciences --> Other Earth and Related Environmental Sciences","Annan geovetenskap och miljövetenskap"));
        CODE_TO_CATEGORY.put(106, new ClassificationCategory(106, 2,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4)","Biologi (Medicinska tillämpningar under 3 och lantbruksvetenskapliga under 4)"));
        CODE_TO_CATEGORY.put(10601, new ClassificationCategory(10601, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Structural Biology","Strukturbiologi"));
        CODE_TO_CATEGORY.put(10602, new ClassificationCategory(10602, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Biochemistry and Molecular Biology","Biokemi och molekylärbiologi"));
        CODE_TO_CATEGORY.put(10603, new ClassificationCategory(10603, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Biophysics","Biofysik"));
        CODE_TO_CATEGORY.put(10604, new ClassificationCategory(10604, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Cell Biology","Cellbiologi"));
        CODE_TO_CATEGORY.put(10605, new ClassificationCategory(10605, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Immunology (medical  to be 30110 and agricultural to be 40302)","Immunologi (medicinsk under 30110 och lantbruksvetenskaplig under 40302)"));
        CODE_TO_CATEGORY.put(10606, new ClassificationCategory(10606, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Microbiology (medical  to be 30109 and agricultural to be 40302)","Mikrobiologi (medicinsk under 30109 och lantbruksvetenskaplig under 40302)"));
        CODE_TO_CATEGORY.put(10607, new ClassificationCategory(10607, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Botany","Botanik"));
        CODE_TO_CATEGORY.put(10608, new ClassificationCategory(10608, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Zoology","Zoologi"));
        CODE_TO_CATEGORY.put(10609, new ClassificationCategory(10609, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Genetics (medical to be 30107 and agricultural to be 40402)","Genetik (medicinsk under 30107 och lantbruksvetenskaplig under 40402)"));
        CODE_TO_CATEGORY.put(10610, new ClassificationCategory(10610, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Bioinformatics and Systems Biology (methods development to be 10203)","Bioinformatik och systembiologi (metodutveckling under 10203)"));
        CODE_TO_CATEGORY.put(10611, new ClassificationCategory(10611, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Ecology","Ekologi"));
        CODE_TO_CATEGORY.put(10612, new ClassificationCategory(10612, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Biological Systematics","Biologisk systematik"));
        CODE_TO_CATEGORY.put(10613, new ClassificationCategory(10613, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Behavioural Sciences Biology","Etologi"));
        CODE_TO_CATEGORY.put(10614, new ClassificationCategory(10614, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Developmental Biology","Utvecklingsbiologi"));
        CODE_TO_CATEGORY.put(10615, new ClassificationCategory(10615, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Evolutionary Biology","Evolutionsbiologi"));
        CODE_TO_CATEGORY.put(10699, new ClassificationCategory(10699, 3,"Natural sciences --> Biological Sciences  (Medical to be 3 and Agricultural to be 4) --> Other Biological Topics","Annan biologi"));
        CODE_TO_CATEGORY.put(107, new ClassificationCategory(107, 2,"Natural sciences --> Other Natural Sciences","Annan naturvetenskap"));
        CODE_TO_CATEGORY.put(10799, new ClassificationCategory(10799, 3,"Natural sciences --> Other Natural Sciences --> Other Natural Sciences not elsewhere specified","Övrig annan naturvetenskap"));
        CODE_TO_CATEGORY.put(2, new ClassificationCategory(2, 1,"Engineering and Technology","Teknik"));
        CODE_TO_CATEGORY.put(201, new ClassificationCategory(201, 2,"Engineering and Technology --> Civil Engineering","Samhällsbyggnadsteknik"));
        CODE_TO_CATEGORY.put(20101, new ClassificationCategory(20101, 3,"Engineering and Technology --> Civil Engineering --> Architectural Engineering","Arkitekturteknik"));
        CODE_TO_CATEGORY.put(20102, new ClassificationCategory(20102, 3,"Engineering and Technology --> Civil Engineering --> Construction Management","Byggproduktion"));
        CODE_TO_CATEGORY.put(20103, new ClassificationCategory(20103, 3,"Engineering and Technology --> Civil Engineering --> Building Technologies","Husbyggnad"));
        CODE_TO_CATEGORY.put(20104, new ClassificationCategory(20104, 3,"Engineering and Technology --> Civil Engineering --> Infrastructure Engineering","Infrastrukturteknik"));
        CODE_TO_CATEGORY.put(20105, new ClassificationCategory(20105, 3,"Engineering and Technology --> Civil Engineering --> Transport Systems and Logistics","Transportteknik och logistik"));
        CODE_TO_CATEGORY.put(20106, new ClassificationCategory(20106, 3,"Engineering and Technology --> Civil Engineering --> Geotechnical Engineering","Geoteknik"));
        CODE_TO_CATEGORY.put(20107, new ClassificationCategory(20107, 3,"Engineering and Technology --> Civil Engineering --> Water Engineering","Vattenteknik"));
        CODE_TO_CATEGORY.put(20108, new ClassificationCategory(20108, 3,"Engineering and Technology --> Civil Engineering --> Environmental Analysis and  Construction Information Technology","Miljöanalys och bygginformationsteknik"));
        CODE_TO_CATEGORY.put(20199, new ClassificationCategory(20199, 3,"Engineering and Technology --> Civil Engineering --> Other Civil Engineering","Annan samhällsbyggnadsteknik"));
        CODE_TO_CATEGORY.put(202, new ClassificationCategory(202, 2,"Engineering and Technology --> Electrical Engineering, Electronic Engineering, Information Engineering","Elektroteknik och elektronik"));
        CODE_TO_CATEGORY.put(20201, new ClassificationCategory(20201, 3,"Engineering and Technology --> Electrical Engineering --> Robotics","Robotteknik och automation"));
        CODE_TO_CATEGORY.put(20202, new ClassificationCategory(20202, 3,"Engineering and Technology --> Electrical Engineering --> Control Engineering","Reglerteknik"));
        CODE_TO_CATEGORY.put(20203, new ClassificationCategory(20203, 3,"Engineering and Technology --> Electrical Engineering --> Communication Systems","Kommunikationssystem"));
        CODE_TO_CATEGORY.put(20204, new ClassificationCategory(20204, 3,"Engineering and Technology --> Electrical Engineering --> Telecommunications","Telekommunikation"));
        CODE_TO_CATEGORY.put(20205, new ClassificationCategory(20205, 3,"Engineering and Technology --> Electrical Engineering --> Signal Processing","Signalbehandling"));
        CODE_TO_CATEGORY.put(20206, new ClassificationCategory(20206, 3,"Engineering and Technology --> Electrical Engineering --> Computer Systems","Datorsystem"));
        CODE_TO_CATEGORY.put(20207, new ClassificationCategory(20207, 3,"Engineering and Technology --> Electrical Engineering --> Embedded Systems","Inbäddad systemteknik"));
        CODE_TO_CATEGORY.put(20299, new ClassificationCategory(20299, 3,"Engineering and Technology --> Electrical Engineering --> Other Electrical Engineering, Electronic Engineering, Information Engineering","Annan elektroteknik och elektronik"));
        CODE_TO_CATEGORY.put(203, new ClassificationCategory(203, 2,"Engineering and Technology --> Mechanical Engineering","Maskinteknik"));
        CODE_TO_CATEGORY.put(20301, new ClassificationCategory(20301, 3,"Engineering and Technology --> Mechanical Engineering --> Applied Mechanics","Teknisk mekanik"));
        CODE_TO_CATEGORY.put(20302, new ClassificationCategory(20302, 3,"Engineering and Technology --> Mechanical Engineering --> Aerospace Engineering","Rymd- och flygteknik"));
        CODE_TO_CATEGORY.put(20303, new ClassificationCategory(20303, 3,"Engineering and Technology --> Mechanical Engineering --> Vehicle Engineering","Farkostteknik"));
        CODE_TO_CATEGORY.put(20304, new ClassificationCategory(20304, 3,"Engineering and Technology --> Mechanical Engineering --> Energy Engineering","Energiteknik"));
        CODE_TO_CATEGORY.put(20305, new ClassificationCategory(20305, 3,"Engineering and Technology --> Mechanical Engineering --> Reliability and Maintenance","Tillförlitlighets- och kvalitetsteknik"));
        CODE_TO_CATEGORY.put(20306, new ClassificationCategory(20306, 3,"Engineering and Technology --> Mechanical Engineering --> Fluid Mechanics and Acoustics","Strömningsmekanik och akustik"));
        CODE_TO_CATEGORY.put(20307, new ClassificationCategory(20307, 3,"Engineering and Technology --> Mechanical Engineering --> Production Engineering, Human Work Science and Ergonomics","Produktionsteknik, arbetsvetenskap och ergonomi"));
        CODE_TO_CATEGORY.put(20308, new ClassificationCategory(20308, 3,"Engineering and Technology --> Mechanical Engineering --> Tribology (interacting surfaces including Friction, Lubrication and Wear)","Tribologi (ytteknik omfattande friktion, nötning och smörjning)"));
        CODE_TO_CATEGORY.put(20399, new ClassificationCategory(20399, 3,"Engineering and Technology --> Mechanical Engineering --> Other Mechanical Engineering","Annan maskinteknik"));
        CODE_TO_CATEGORY.put(204, new ClassificationCategory(204, 2,"Engineering and Technology --> Chemical Engineering","Kemiteknik"));
        CODE_TO_CATEGORY.put(20401, new ClassificationCategory(20401, 3,"Engineering and Technology --> Chemical Engineering --> Chemical Process Engineering","Kemiska processer"));
        CODE_TO_CATEGORY.put(20402, new ClassificationCategory(20402, 3,"Engineering and Technology --> Chemical Engineering --> Corrosion Engineering","Korrosionsteknik"));
        CODE_TO_CATEGORY.put(20403, new ClassificationCategory(20403, 3,"Engineering and Technology --> Chemical Engineering --> Polymer Technologies","Polymerteknologi"));
        CODE_TO_CATEGORY.put(20404, new ClassificationCategory(20404, 3,"Engineering and Technology --> Chemical Engineering --> Pharmaceutical Chemistry","Farmaceutisk synteskemi"));
        CODE_TO_CATEGORY.put(20499, new ClassificationCategory(20499, 3,"Engineering and Technology --> Chemical Engineering --> Other Chemical Engineering","Annan kemiteknik"));
        CODE_TO_CATEGORY.put(205, new ClassificationCategory(205, 2,"Engineering and Technology --> Materials Engineering","Materialteknik"));
        CODE_TO_CATEGORY.put(20501, new ClassificationCategory(20501, 3,"Engineering and Technology --> Materials Engineering --> Ceramics","Keramteknik"));
        CODE_TO_CATEGORY.put(20502, new ClassificationCategory(20502, 3,"Engineering and Technology --> Materials Engineering --> Composite Science and Engineering","Kompositmaterial och -teknik"));
        CODE_TO_CATEGORY.put(20503, new ClassificationCategory(20503, 3,"Engineering and Technology --> Materials Engineering --> Paper, Pulp and Fiber Technology","Pappers-, massa-  och fiberteknik"));
        CODE_TO_CATEGORY.put(20504, new ClassificationCategory(20504, 3,"Engineering and Technology --> Materials Engineering --> Textile, Rubber and Polymeric Materials","Textil-, gummi- och polymermaterial"));
        CODE_TO_CATEGORY.put(20505, new ClassificationCategory(20505, 3,"Engineering and Technology --> Materials Engineering --> Manufacturing, Surface and Joining Technology","Bearbetnings-, yt- och fogningsteknik"));
        CODE_TO_CATEGORY.put(20506, new ClassificationCategory(20506, 3,"Engineering and Technology --> Materials Engineering --> Metallurgy and Metallic Materials","Metallurgi och metalliska material"));
        CODE_TO_CATEGORY.put(20599, new ClassificationCategory(20599, 3,"Engineering and Technology --> Materials Engineering --> Other Materials Engineering","Annan materialteknik"));
        CODE_TO_CATEGORY.put(206, new ClassificationCategory(206, 2,"Engineering and Technology --> Medical Engineering","Medicinteknik"));
        CODE_TO_CATEGORY.put(20601, new ClassificationCategory(20601, 3,"Engineering and Technology --> Medical Engineering --> Medical Laboratory and Measurements Technologies","Medicinsk laboratorie- och mätteknik"));
        CODE_TO_CATEGORY.put(20602, new ClassificationCategory(20602, 3,"Engineering and Technology --> Medical Engineering --> Medical Materials","Medicinsk material- och protesteknik"));
        CODE_TO_CATEGORY.put(20603, new ClassificationCategory(20603, 3,"Engineering and Technology --> Medical Engineering --> Medical Image Processing","Medicinsk bildbehandling"));
        CODE_TO_CATEGORY.put(20604, new ClassificationCategory(20604, 3,"Engineering and Technology --> Medical Engineering --> Medical Equipment Engineering","Medicinsk apparatteknik"));
        CODE_TO_CATEGORY.put(20605, new ClassificationCategory(20605, 3,"Engineering and Technology --> Medical Engineering --> Medical Ergonomics","Medicinsk ergonomi"));
        CODE_TO_CATEGORY.put(20699, new ClassificationCategory(20699, 3,"Engineering and Technology --> Medical Engineering --> Other Medical Engineering","Annan medicinteknik"));
        CODE_TO_CATEGORY.put(207, new ClassificationCategory(207, 2,"Engineering and Technology --> Environmental Engineering","Naturresursteknik"));
        CODE_TO_CATEGORY.put(20701, new ClassificationCategory(20701, 3,"Engineering and Technology --> Environmental Engineering --> Geophysical Engineering","Geofysisk teknik"));
        CODE_TO_CATEGORY.put(20702, new ClassificationCategory(20702, 3,"Engineering and Technology --> Environmental Engineering --> Energy Systems","Energisystem"));
        CODE_TO_CATEGORY.put(20703, new ClassificationCategory(20703, 3,"Engineering and Technology --> Environmental Engineering --> Remote Sensing","Fjärranalysteknik"));
        CODE_TO_CATEGORY.put(20704, new ClassificationCategory(20704, 3,"Engineering and Technology --> Environmental Engineering --> Mineral and Mine Engineering","Mineral- och gruvteknik"));
        CODE_TO_CATEGORY.put(20705, new ClassificationCategory(20705, 3,"Engineering and Technology --> Environmental Engineering --> Marine Engineering","Marin teknik"));
        CODE_TO_CATEGORY.put(20706, new ClassificationCategory(20706, 3,"Engineering and Technology --> Environmental Engineering --> Ocean and River Engineering","Havs- och vattendragsteknik"));
        CODE_TO_CATEGORY.put(20707, new ClassificationCategory(20707, 3,"Engineering and Technology --> Environmental Engineering --> Environmental Management","Miljöledning"));
        CODE_TO_CATEGORY.put(20799, new ClassificationCategory(20799, 3,"Engineering and Technology --> Environmental Engineering --> Other Environmental Engineering","Annan naturresursteknik"));
        CODE_TO_CATEGORY.put(208, new ClassificationCategory(208, 2,"Engineering and Technology --> Environmental Biotechnology","Miljöbioteknik"));
        CODE_TO_CATEGORY.put(20801, new ClassificationCategory(20801, 3,"Engineering and Technology --> Environmental Biotechnology --> Bioremediation","Biosanering"));
        CODE_TO_CATEGORY.put(20802, new ClassificationCategory(20802, 3,"Engineering and Technology --> Environmental Biotechnology --> Diagnostic Biotechnology","Diagnostisk bioteknologi"));
        CODE_TO_CATEGORY.put(20803, new ClassificationCategory(20803, 3,"Engineering and Technology --> Environmental Biotechnology --> Water Treatment","Vattenbehandling"));
        CODE_TO_CATEGORY.put(20804, new ClassificationCategory(20804, 3,"Engineering and Technology --> Environmental Biotechnology --> Bioethics","Bioteknisk etik"));
        CODE_TO_CATEGORY.put(20899, new ClassificationCategory(20899, 3,"Engineering and Technology --> Environmental Biotechnology --> Other Environmental Biotechnology","Annan miljöbioteknik"));
        CODE_TO_CATEGORY.put(209, new ClassificationCategory(209, 2,"Engineering and Technology --> Industrial Biotechnology","Industriell bioteknik"));
        CODE_TO_CATEGORY.put(20901, new ClassificationCategory(20901, 3,"Engineering and Technology --> Industrial Biotechnology --> Bioprocess Technology","Bioprocessteknik"));
        CODE_TO_CATEGORY.put(20902, new ClassificationCategory(20902, 3,"Engineering and Technology --> Industrial Biotechnology --> Biochemicals","Biokemikalier"));
        CODE_TO_CATEGORY.put(20903, new ClassificationCategory(20903, 3,"Engineering and Technology --> Industrial Biotechnology --> Bio Materials","Biomaterial"));
        CODE_TO_CATEGORY.put(20904, new ClassificationCategory(20904, 3,"Engineering and Technology --> Industrial Biotechnology --> Bioenergy","Bioenergi"));
        CODE_TO_CATEGORY.put(20905, new ClassificationCategory(20905, 3,"Engineering and Technology --> Industrial Biotechnology --> Pharmaceutical Biotechnology","Läkemedelsbioteknik"));
        CODE_TO_CATEGORY.put(20906, new ClassificationCategory(20906, 3,"Engineering and Technology --> Industrial Biotechnology --> Biocatalysis and Enzyme Technology","Biokatalys och enzymteknik"));
        CODE_TO_CATEGORY.put(20907, new ClassificationCategory(20907, 3,"Engineering and Technology --> Industrial Biotechnology --> Bioengineering Equipment","Bioteknisk apparatteknik"));
        CODE_TO_CATEGORY.put(20908, new ClassificationCategory(20908, 3,"Engineering and Technology --> Industrial Biotechnology --> Medical Biotechnology","Medicinsk bioteknik"));
        CODE_TO_CATEGORY.put(20999, new ClassificationCategory(20999, 3,"Engineering and Technology --> Industrial Biotechnology --> Other Industrial Biotechnology","Annan industriell bioteknik"));
        CODE_TO_CATEGORY.put(210, new ClassificationCategory(210, 2,"Engineering and Technology --> Nano-technology","Nanoteknik"));
        CODE_TO_CATEGORY.put(21001, new ClassificationCategory(21001, 3,"Engineering and Technology --> Nano-technology --> Nano-technology","Nanoteknik"));
        CODE_TO_CATEGORY.put(211, new ClassificationCategory(211, 2,"Engineering and Technology --> Other Engineering and Technologies","Annan teknik"));
        CODE_TO_CATEGORY.put(21101, new ClassificationCategory(21101, 3,"Engineering and Technology --> Other Engineering and Technologies --> Food Engineering","Livsmedelsteknik"));
        CODE_TO_CATEGORY.put(21102, new ClassificationCategory(21102, 3,"Engineering and Technology --> Other Engineering and Technologies --> Media Engineering","Mediateknik"));
        CODE_TO_CATEGORY.put(21103, new ClassificationCategory(21103, 3,"Engineering and Technology --> Other Engineering and Technologies --> Interaction Technologies","Interaktionsteknik"));
        CODE_TO_CATEGORY.put(21199, new ClassificationCategory(21199, 3,"Engineering and Technology --> Other Engineering and Technologies --> Other Engineering and Technologies not elsewhere specified","Övrig annan teknik"));
        CODE_TO_CATEGORY.put(3, new ClassificationCategory(3, 1,"Medical and Health Sciences","Medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(301, new ClassificationCategory(301, 2,"Medical and Health Sciences --> Basic Medicine","Medicinska och farmaceutiska grundvetenskaper"));
        CODE_TO_CATEGORY.put(30101, new ClassificationCategory(30101, 3,"Medical and Health Sciences --> Basic Medicine --> Pharmaceutical Sciences","Farmaceutiska vetenskaper"));
        CODE_TO_CATEGORY.put(30102, new ClassificationCategory(30102, 3,"Medical and Health Sciences --> Basic Medicine --> Pharmacology and Toxicology","Farmakologi och toxikologi"));
        CODE_TO_CATEGORY.put(30103, new ClassificationCategory(30103, 3,"Medical and Health Sciences --> Basic Medicine --> Medicinal Chemistry","Läkemedelskemi"));
        CODE_TO_CATEGORY.put(30104, new ClassificationCategory(30104, 3,"Medical and Health Sciences --> Basic Medicine --> Social and Clinical Pharmacy","Samhällsfarmaci och klinisk farmaci"));
        CODE_TO_CATEGORY.put(30105, new ClassificationCategory(30105, 3,"Medical and Health Sciences --> Basic Medicine --> Neurosciences","Neurovetenskaper"));
        CODE_TO_CATEGORY.put(30106, new ClassificationCategory(30106, 3,"Medical and Health Sciences --> Basic Medicine --> Physiology","Fysiologi"));
        CODE_TO_CATEGORY.put(30107, new ClassificationCategory(30107, 3,"Medical and Health Sciences --> Basic Medicine --> Medical Genetics","Medicinsk genetik"));
        CODE_TO_CATEGORY.put(30108, new ClassificationCategory(30108, 3,"Medical and Health Sciences --> Basic Medicine --> Cell and Molecular Biology","Cell- och molekylärbiologi"));
        CODE_TO_CATEGORY.put(30109, new ClassificationCategory(30109, 3,"Medical and Health Sciences --> Basic Medicine --> Microbiology in the medical area","Mikrobiologi inom det medicinska området"));
        CODE_TO_CATEGORY.put(30110, new ClassificationCategory(30110, 3,"Medical and Health Sciences --> Basic Medicine --> Immunology in the medical area","Immunologi inom det medicinska området"));
        CODE_TO_CATEGORY.put(30199, new ClassificationCategory(30199, 3,"Medical and Health Sciences --> Basic Medicine --> Other Basic Medicine","Andra medicinska och farmaceutiska grundvetenskaper"));
        CODE_TO_CATEGORY.put(302, new ClassificationCategory(302, 2,"Medical and Health Sciences --> Clinical Medicine","Klinisk medicin"));
        CODE_TO_CATEGORY.put(30201, new ClassificationCategory(30201, 3,"Medical and Health Sciences --> Clinical Medicine --> Anesthesiology and Intensive Care","Anestesi och intensivvård"));
        CODE_TO_CATEGORY.put(30202, new ClassificationCategory(30202, 3,"Medical and Health Sciences --> Clinical Medicine --> Hematology","Hematologi"));
        CODE_TO_CATEGORY.put(30203, new ClassificationCategory(30203, 3,"Medical and Health Sciences --> Clinical Medicine --> Cancer and Oncology","Cancer och onkologi"));
        CODE_TO_CATEGORY.put(30204, new ClassificationCategory(30204, 3,"Medical and Health Sciences --> Clinical Medicine --> Dermatology and Venereal Diseases","Dermatologi och venereologi"));
        CODE_TO_CATEGORY.put(30205, new ClassificationCategory(30205, 3,"Medical and Health Sciences --> Clinical Medicine --> Endocrinology and Diabetes","Endokrinologi och diabetes"));
        CODE_TO_CATEGORY.put(30206, new ClassificationCategory(30206, 3,"Medical and Health Sciences --> Clinical Medicine --> Cardiac and Cardiovascular Systems","Kardiologi"));
        CODE_TO_CATEGORY.put(30207, new ClassificationCategory(30207, 3,"Medical and Health Sciences --> Clinical Medicine --> Neurology","Neurologi"));
        CODE_TO_CATEGORY.put(30208, new ClassificationCategory(30208, 3,"Medical and Health Sciences --> Clinical Medicine --> Radiology, Nuclear Medicine and Medical Imaging","Radiologi och bildbehandling"));
        CODE_TO_CATEGORY.put(30209, new ClassificationCategory(30209, 3,"Medical and Health Sciences --> Clinical Medicine --> Infectious Medicine","Infektionsmedicin"));
        CODE_TO_CATEGORY.put(30210, new ClassificationCategory(30210, 3,"Medical and Health Sciences --> Clinical Medicine --> Rheumatology and Autoimmunity","Reumatologi och inflammation"));
        CODE_TO_CATEGORY.put(30211, new ClassificationCategory(30211, 3,"Medical and Health Sciences --> Clinical Medicine --> Orthopaedics","Ortopedi"));
        CODE_TO_CATEGORY.put(30212, new ClassificationCategory(30212, 3,"Medical and Health Sciences --> Clinical Medicine --> Surgery","Kirurgi"));
        CODE_TO_CATEGORY.put(30213, new ClassificationCategory(30213, 3,"Medical and Health Sciences --> Clinical Medicine --> Gastroenterology and Hepatology","Gastroenterologi"));
        CODE_TO_CATEGORY.put(30214, new ClassificationCategory(30214, 3,"Medical and Health Sciences --> Clinical Medicine --> Urology and Nephrology","Urologi och njurmedicin"));
        CODE_TO_CATEGORY.put(30215, new ClassificationCategory(30215, 3,"Medical and Health Sciences --> Clinical Medicine --> Psychiatry","Psykiatri"));
        CODE_TO_CATEGORY.put(30216, new ClassificationCategory(30216, 3,"Medical and Health Sciences --> Clinical Medicine --> Dentistry","Odontologi"));
        CODE_TO_CATEGORY.put(30217, new ClassificationCategory(30217, 3,"Medical and Health Sciences --> Clinical Medicine --> Ophthalmology","Oftalmologi"));
        CODE_TO_CATEGORY.put(30218, new ClassificationCategory(30218, 3,"Medical and Health Sciences --> Clinical Medicine --> Otorhinolaryngology","Oto-rhino-laryngologi"));
        CODE_TO_CATEGORY.put(30219, new ClassificationCategory(30219, 3,"Medical and Health Sciences --> Clinical Medicine --> Respiratory Medicine and Allergy","Lungmedicin och allergi"));
        CODE_TO_CATEGORY.put(30220, new ClassificationCategory(30220, 3,"Medical and Health Sciences --> Clinical Medicine --> Obstetrics, Gynaecology and Reproductive Medicine","Reproduktionsmedicin och gynekologi"));
        CODE_TO_CATEGORY.put(30221, new ClassificationCategory(30221, 3,"Medical and Health Sciences --> Clinical Medicine --> Pediatrics","Pediatrik"));
        CODE_TO_CATEGORY.put(30222, new ClassificationCategory(30222, 3,"Medical and Health Sciences --> Clinical Medicine --> Geriatrics","Geriatrik"));
        CODE_TO_CATEGORY.put(30223, new ClassificationCategory(30223, 3,"Medical and Health Sciences --> Clinical Medicine --> Clinical Laboratory Medicine","Klinisk laboratoriemedicin"));
        CODE_TO_CATEGORY.put(30224, new ClassificationCategory(30224, 3,"Medical and Health Sciences --> Clinical Medicine --> General Practice","Allmänmedicin"));
        CODE_TO_CATEGORY.put(30299, new ClassificationCategory(30299, 3,"Medical and Health Sciences --> Clinical Medicine --> Other Clinical Medicine","Annan klinisk medicin"));
        CODE_TO_CATEGORY.put(303, new ClassificationCategory(303, 2,"Medical and Health Sciences --> Health Sciences","Hälsovetenskap"));
        CODE_TO_CATEGORY.put(30301, new ClassificationCategory(30301, 3,"Medical and Health Sciences --> Health Sciences --> Health Care Service and Management, Health Policy and Services and Health Economy","Hälso- och sjukvårdsorganisation, hälsopolitik och hälsoekonomi"));
        CODE_TO_CATEGORY.put(30302, new ClassificationCategory(30302, 3,"Medical and Health Sciences --> Health Sciences --> Public Health, Global Health, Social Medicine and Epidemiology","Folkhälsovetenskap, global hälsa, socialmedicin och epidemiologi"));
        CODE_TO_CATEGORY.put(30303, new ClassificationCategory(30303, 3,"Medical and Health Sciences --> Health Sciences --> Occupational Health and Environmental Health","Arbetsmedicin och miljömedicin"));
        CODE_TO_CATEGORY.put(30304, new ClassificationCategory(30304, 3,"Medical and Health Sciences --> Health Sciences --> Nutrition and Dietetics","Näringslära"));
        CODE_TO_CATEGORY.put(30305, new ClassificationCategory(30305, 3,"Medical and Health Sciences --> Health Sciences --> Nursing","Omvårdnad"));
        CODE_TO_CATEGORY.put(30306, new ClassificationCategory(30306, 3,"Medical and Health Sciences --> Health Sciences --> Occupational Therapy","Arbetsterapi"));
        CODE_TO_CATEGORY.put(30307, new ClassificationCategory(30307, 3,"Medical and Health Sciences --> Health Sciences --> Physiotherapy","Sjukgymnastik"));
        CODE_TO_CATEGORY.put(30308, new ClassificationCategory(30308, 3,"Medical and Health Sciences --> Health Sciences --> Sport and Fitness Sciences","Idrottsvetenskap"));
        CODE_TO_CATEGORY.put(30309, new ClassificationCategory(30309, 3,"Medical and Health Sciences --> Health Sciences --> Substance Abuse","Beroendelära"));
        CODE_TO_CATEGORY.put(30310, new ClassificationCategory(30310, 3,"Medical and Health Sciences --> Health Sciences --> Medical Ethics","Medicinsk etik"));
        CODE_TO_CATEGORY.put(30399, new ClassificationCategory(30399, 3,"Medical and Health Sciences --> Health Sciences --> Other Health Sciences","Annan hälsovetenskap"));
        CODE_TO_CATEGORY.put(304, new ClassificationCategory(304, 2,"Medical and Health Sciences --> Medical Biotechnology","Medicinsk bioteknologi"));
        CODE_TO_CATEGORY.put(30401, new ClassificationCategory(30401, 3,"Medical and Health Sciences --> Medical Biotechnology --> Medical Biotechnology (focus on Cell Biology (incl. Stem Cell Biology), Molecular Biology, Microbiology, Biochemistry or Biopharmacy)","Medicinsk bioteknologi (inriktn. mot cellbiologi (inkl. stamcellsbiologi), molekylärbiologi, mikrobiologi, biokemi eller biofarmaci)"));
        CODE_TO_CATEGORY.put(30402, new ClassificationCategory(30402, 3,"Medical and Health Sciences --> Medical Biotechnology --> Biomedical Laboratory Science/Technology","Biomedicinsk laboratorievetenskap/teknologi"));
        CODE_TO_CATEGORY.put(30403, new ClassificationCategory(30403, 3,"Medical and Health Sciences --> Medical Biotechnology --> Biomaterials Science","Biomaterialvetenskap"));
        CODE_TO_CATEGORY.put(30499, new ClassificationCategory(30499, 3,"Medical and Health Sciences --> Medical Biotechnology --> Other Medical Biotechnology","Annan medicinsk bioteknologi"));
        CODE_TO_CATEGORY.put(305, new ClassificationCategory(305, 2,"Medical and Health Sciences --> Other Medical and Health Sciences","Annan medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(30501, new ClassificationCategory(30501, 3,"Medical and Health Sciences --> Other Medical and Health Sciences --> Forensic Science","Rättsmedicin"));
        CODE_TO_CATEGORY.put(30502, new ClassificationCategory(30502, 3,"Medical and Health Sciences --> Other Medical and Health Sciences --> Gerontology, specialising in Medical and Health Sciences (specialising in Social Sciences to be 50999)","Gerontologi, medicinsk/hälsovetenskaplig inriktning  (Samhällsvetenskaplig inriktn.under 50999)"));
        CODE_TO_CATEGORY.put(30599, new ClassificationCategory(30599, 3,"Medical and Health Sciences --> Other Medical and Health Sciences --> Other Medical and Health Sciences not elsewhere specified","Övrig annan medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(4, new ClassificationCategory(4, 1,"Agricultural Sciences","Lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(401, new ClassificationCategory(401, 2,"Agricultural Sciences --> Agricultural, Forestry and Fisheries","Lantbruksvetenskap, skogsbruk och fiske"));
        CODE_TO_CATEGORY.put(40101, new ClassificationCategory(40101, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Agricultural Science","Jordbruksvetenskap"));
        CODE_TO_CATEGORY.put(40102, new ClassificationCategory(40102, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Horticulture","Trädgårdsvetenskap/hortikultur"));
        CODE_TO_CATEGORY.put(40103, new ClassificationCategory(40103, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Food Science","Livsmedelsvetenskap"));
        CODE_TO_CATEGORY.put(40104, new ClassificationCategory(40104, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Forest Science","Skogsvetenskap"));
        CODE_TO_CATEGORY.put(40105, new ClassificationCategory(40105, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Wood Science","Trävetenskap"));
        CODE_TO_CATEGORY.put(40106, new ClassificationCategory(40106, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Soil Science","Markvetenskap"));
        CODE_TO_CATEGORY.put(40107, new ClassificationCategory(40107, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Fish and Aquacultural Science","Fisk- och akvakulturforskning"));
        CODE_TO_CATEGORY.put(40108, new ClassificationCategory(40108, 3,"Agricultural Sciences --> Agricultural, Forestry and Fisheries --> Landscape Architecture","Landskapsarkitektur"));
        CODE_TO_CATEGORY.put(402, new ClassificationCategory(402, 2,"Agricultural Sciences --> Animal and Dairy Sience","Husdjursvetenskap"));
        CODE_TO_CATEGORY.put(40201, new ClassificationCategory(40201, 3,"Agricultural Sciences --> Animal and Dairy Sience --> Animal and Dairy Science.","Husdjursvetenskap"));
        CODE_TO_CATEGORY.put(403, new ClassificationCategory(403, 2,"Agricultural Sciences --> Veterinary Science","Veterinärmedicin"));
        CODE_TO_CATEGORY.put(40301, new ClassificationCategory(40301, 3,"Agricultural Sciences --> Veterinary Science --> Medical Bioscience","Medicinsk biovetenskap"));
        CODE_TO_CATEGORY.put(40302, new ClassificationCategory(40302, 3,"Agricultural Sciences --> Veterinary Science --> Pathobiology","Patobiologi"));
        CODE_TO_CATEGORY.put(40303, new ClassificationCategory(40303, 3,"Agricultural Sciences --> Veterinary Science --> Clinical Science","Klinisk vetenskap"));
        CODE_TO_CATEGORY.put(40304, new ClassificationCategory(40304, 3,"Agricultural Sciences --> Veterinary Science --> Other Veterinary Science","Annan veterinärmedicin"));
        CODE_TO_CATEGORY.put(404, new ClassificationCategory(404, 2,"Agricultural Sciences --> Agricultural Biotechnology","Bioteknologi med applikationer på växter och djur"));
        CODE_TO_CATEGORY.put(40401, new ClassificationCategory(40401, 3,"Agricultural Sciences --> Agricultural Biotechnology --> Plant Biotechnology","Växtbioteknologi"));
        CODE_TO_CATEGORY.put(40402, new ClassificationCategory(40402, 3,"Agricultural Sciences --> Agricultural Biotechnology --> Genetics and Breeding in Agricultural Sciences","Genetik och förädling inom lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(405, new ClassificationCategory(405, 2,"Agricultural Sciences --> Other Agricultural Sciences","Annan lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(40501, new ClassificationCategory(40501, 3,"Agricultural Sciences --> Other Agricultural Sciences --> Renewable Bioenergy Research","Förnyelsebar bioenergi"));
        CODE_TO_CATEGORY.put(40502, new ClassificationCategory(40502, 3,"Agricultural Sciences --> Other Agricultural Sciences --> Fish and Wildlife Management","Vilt- och fiskeförvaltning"));
        CODE_TO_CATEGORY.put(40503, new ClassificationCategory(40503, 3,"Agricultural Sciences --> Other Agricultural Sciences --> Agricultural Occupational Health and Safety","Lantbrukets arbetsmiljö och säkerhet"));
        CODE_TO_CATEGORY.put(40504, new ClassificationCategory(40504, 3,"Agricultural Sciences --> Other Agricultural Sciences --> Environmental Sciences related to Agriculture and Land-use","Miljö- och  naturvårdsvetenskap"));
        CODE_TO_CATEGORY.put(40599, new ClassificationCategory(40599, 3,"Agricultural Sciences --> Other Agricultural Sciences --> Other Agricultural Sciences not elsewhere specified","Övrig annan lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(5, new ClassificationCategory(5, 1,"Social Sciences","Samhällsvetenskap"));
        CODE_TO_CATEGORY.put(501, new ClassificationCategory(501, 2,"Social Sciences --> Psychology","Psykologi"));
        CODE_TO_CATEGORY.put(50101, new ClassificationCategory(50101, 3,"Social Sciences --> Psychology --> Psychology (excluding Applied Psychology)","Psykologi  (exklusive tillämpad psykologi)"));
        CODE_TO_CATEGORY.put(50102, new ClassificationCategory(50102, 3,"Social Sciences --> Psychology --> Applied Psychology","Tillämpad psykologi"));
        CODE_TO_CATEGORY.put(502, new ClassificationCategory(502, 2,"Social Sciences --> Economics and Business","Ekonomi och näringsliv"));
        CODE_TO_CATEGORY.put(50201, new ClassificationCategory(50201, 3,"Social Sciences --> Economics and Business --> Economics","Nationalekonomi"));
        CODE_TO_CATEGORY.put(50202, new ClassificationCategory(50202, 3,"Social Sciences --> Economics and Business --> Business Administration","Företagsekonomi"));
        CODE_TO_CATEGORY.put(50203, new ClassificationCategory(50203, 3,"Social Sciences --> Economics and Business --> Economic History","Ekonomisk historia"));
        CODE_TO_CATEGORY.put(503, new ClassificationCategory(503, 2,"Social Sciences --> Educational Sciences","Utbildningsvetenskap"));
        CODE_TO_CATEGORY.put(50301, new ClassificationCategory(50301, 3,"Social Sciences --> Educational Sciences --> Pedagogy","Pedagogik"));
        CODE_TO_CATEGORY.put(50302, new ClassificationCategory(50302, 3,"Social Sciences --> Educational Sciences --> Didactics","Didaktik"));
        CODE_TO_CATEGORY.put(50303, new ClassificationCategory(50303, 3,"Social Sciences --> Educational Sciences --> Learning","Lärande"));
        CODE_TO_CATEGORY.put(50304, new ClassificationCategory(50304, 3,"Social Sciences --> Educational Sciences --> Pedagogical Work","Pedagogiskt arbete"));
        CODE_TO_CATEGORY.put(504, new ClassificationCategory(504, 2,"Social Sciences --> Sociology","Sociologi"));
        CODE_TO_CATEGORY.put(50401, new ClassificationCategory(50401, 3,"Social Sciences --> Sociology --> Sociology (excluding Social Work, Social Psychology and Social Anthropology)","Sociologi (exklusive socialt arbete, socialpsykologi och socialantropologi)"));
        CODE_TO_CATEGORY.put(50402, new ClassificationCategory(50402, 3,"Social Sciences --> Sociology --> Social Work","Socialt arbete"));
        CODE_TO_CATEGORY.put(50403, new ClassificationCategory(50403, 3,"Social Sciences --> Sociology --> Social Psychology","Socialpsykologi"));
        CODE_TO_CATEGORY.put(50404, new ClassificationCategory(50404, 3,"Social Sciences --> Sociology --> Social Anthropology","Socialantropologi"));
        CODE_TO_CATEGORY.put(505, new ClassificationCategory(505, 2,"Social Sciences --> Law","Juridik"));
        CODE_TO_CATEGORY.put(50501, new ClassificationCategory(50501, 3,"Social Sciences --> Law --> Law (excluding Law and Society)","Juridik (exklusive juridik och samhälle)"));
        CODE_TO_CATEGORY.put(50502, new ClassificationCategory(50502, 3,"Social Sciences --> Law --> Law and Society","Juridik och samhälle"));
        CODE_TO_CATEGORY.put(506, new ClassificationCategory(506, 2,"Social Sciences --> Political Science","Statsvetenskap"));
        CODE_TO_CATEGORY.put(50601, new ClassificationCategory(50601, 3,"Social Sciences --> Political Science --> Political Science (excluding Public Administration Studies and Globalisation Studies)","Statsvetenskap (exklusive studier av offentlig förvaltning och globaliseringsstudier)"));
        CODE_TO_CATEGORY.put(50602, new ClassificationCategory(50602, 3,"Social Sciences --> Political Science --> Public Administration Studies","Studier av offentlig förvaltning"));
        CODE_TO_CATEGORY.put(50603, new ClassificationCategory(50603, 3,"Social Sciences --> Political Science --> Globalisation Studies","Globaliseringsstudier"));
        CODE_TO_CATEGORY.put(507, new ClassificationCategory(507, 2,"Social Sciences --> Social and Economic Geography","Social och ekonomisk geografi"));
        CODE_TO_CATEGORY.put(50701, new ClassificationCategory(50701, 3,"Social Sciences --> Social and Economic Geography --> Human Geography","Kulturgeografi"));
        CODE_TO_CATEGORY.put(50702, new ClassificationCategory(50702, 3,"Social Sciences --> Social and Economic Geography --> Economic Geography","Ekonomisk geografi"));
        CODE_TO_CATEGORY.put(508, new ClassificationCategory(508, 2,"Social Sciences --> Media and Communications","Medie- och kommunikationsvetenskap"));
        CODE_TO_CATEGORY.put(50801, new ClassificationCategory(50801, 3,"Social Sciences --> Media and Communications --> Media Studies","Medievetenskap"));
        CODE_TO_CATEGORY.put(50802, new ClassificationCategory(50802, 3,"Social Sciences --> Media and Communications --> Communication Studies","Kommunikationsvetenskap"));
        CODE_TO_CATEGORY.put(50803, new ClassificationCategory(50803, 3,"Social Sciences --> Media and Communications --> Human Aspects of ICT","Mänsklig interaktion med IKT"));
        CODE_TO_CATEGORY.put(50804, new ClassificationCategory(50804, 3,"Social Sciences --> Media and Communications --> Information Systems, Social aspects","Systemvetenskap, informationssystem och informatik med samhällsvetenskaplig inriktning"));
        CODE_TO_CATEGORY.put(50805, new ClassificationCategory(50805, 3,"Social Sciences --> Media and Communications --> Information Studies","Biblioteks-och informationsvetenskap"));
        CODE_TO_CATEGORY.put(509, new ClassificationCategory(509, 2,"Social Sciences --> Other Social Sciences","Annan samhällsvetenskap"));
        CODE_TO_CATEGORY.put(50901, new ClassificationCategory(50901, 3,"Social Sciences --> Other Social Sciences --> Social Sciences Interdisciplinary","Tvärvetenskapliga studier inom samhällsvetenskap"));
        CODE_TO_CATEGORY.put(50902, new ClassificationCategory(50902, 3,"Social Sciences --> Other Social Sciences --> Gender Studies","Genusstudier"));
        CODE_TO_CATEGORY.put(50903, new ClassificationCategory(50903, 3,"Social Sciences --> Other Social Sciences --> Work Sciences","Arbetslivsstudier"));
        CODE_TO_CATEGORY.put(50904, new ClassificationCategory(50904, 3,"Social Sciences --> Other Social Sciences --> International Migration and Ethnic Relations","Internationell migration och etniska relationer (IMER)"));
        CODE_TO_CATEGORY.put(50999, new ClassificationCategory(50999, 3,"Social Sciences --> Other Social Sciences --> Other Social Sciences not elsewhere specified","Övrig annan samhällsvetenskap"));
        CODE_TO_CATEGORY.put(6, new ClassificationCategory(6, 1,"Humanities","Humaniora"));
        CODE_TO_CATEGORY.put(601, new ClassificationCategory(601, 2,"Humanities --> History and Archaeology","Historia och arkeologi"));
        CODE_TO_CATEGORY.put(60101, new ClassificationCategory(60101, 3,"Humanities --> History and Archaeology --> History","Historia"));
        CODE_TO_CATEGORY.put(60102, new ClassificationCategory(60102, 3,"Humanities --> History and Archaeology --> History of Technology","Teknikhistoria"));
        CODE_TO_CATEGORY.put(60103, new ClassificationCategory(60103, 3,"Humanities --> History and Archaeology --> Archaeology","Arkeologi"));
        CODE_TO_CATEGORY.put(602, new ClassificationCategory(602, 2,"Humanities --> Languages and Literature","Språk och litteratur"));
        CODE_TO_CATEGORY.put(60201, new ClassificationCategory(60201, 3,"Humanities --> Languages and Literature --> General Language Studies and Linguistics","Jämförande språkvetenskap och allmän lingvistik"));
        CODE_TO_CATEGORY.put(60202, new ClassificationCategory(60202, 3,"Humanities --> Languages and Literature --> Specific Languages","Studier av enskilda språk"));
        CODE_TO_CATEGORY.put(60203, new ClassificationCategory(60203, 3,"Humanities --> Languages and Literature --> General Literary studies","Litteraturvetenskap"));
        CODE_TO_CATEGORY.put(60204, new ClassificationCategory(60204, 3,"Humanities --> Languages and Literature --> Specific Literatures","Litteraturstudier"));
        CODE_TO_CATEGORY.put(603, new ClassificationCategory(603, 2,"Humanities --> Philosophy, Ethics and Religion","Filosofi, etik och religion"));
        CODE_TO_CATEGORY.put(60301, new ClassificationCategory(60301, 3,"Humanities --> Philosophy, Ethics and Religion --> Philosophy","Filosofi"));
        CODE_TO_CATEGORY.put(60302, new ClassificationCategory(60302, 3,"Humanities --> Philosophy, Ethics and Religion --> Ethics","Etik"));
        CODE_TO_CATEGORY.put(60303, new ClassificationCategory(60303, 3,"Humanities --> Philosophy, Ethics and Religion --> Religious Studies","Religionsvetenskap"));
        CODE_TO_CATEGORY.put(60304, new ClassificationCategory(60304, 3,"Humanities --> Philosophy, Ethics and Religion --> History of Religions","Religionshistoria"));
        CODE_TO_CATEGORY.put(60305, new ClassificationCategory(60305, 3,"Humanities --> Philosophy, Ethics and Religion --> History of Ideas","Idé- och lärdomshistoria"));
        CODE_TO_CATEGORY.put(604, new ClassificationCategory(604, 2,"Humanities --> Arts","Konst"));
        CODE_TO_CATEGORY.put(60401, new ClassificationCategory(60401, 3,"Humanities --> Arts --> Visual Arts","Bildkonst"));
        CODE_TO_CATEGORY.put(60402, new ClassificationCategory(60402, 3,"Humanities --> Arts --> Music","Musik"));
        CODE_TO_CATEGORY.put(60403, new ClassificationCategory(60403, 3,"Humanities --> Arts --> Literary Composition","Litterär gestaltning"));
        CODE_TO_CATEGORY.put(60404, new ClassificationCategory(60404, 3,"Humanities --> Arts --> Performing Arts","Scenkonst"));
        CODE_TO_CATEGORY.put(60405, new ClassificationCategory(60405, 3,"Humanities --> Arts --> Architecture","Arkitektur"));
        CODE_TO_CATEGORY.put(60406, new ClassificationCategory(60406, 3,"Humanities --> Arts --> Design","Design"));
        CODE_TO_CATEGORY.put(60407, new ClassificationCategory(60407, 3,"Humanities --> Arts --> Art History","Konstvetenskap"));
        CODE_TO_CATEGORY.put(60408, new ClassificationCategory(60408, 3,"Humanities --> Arts --> Musicology","Musikvetenskap"));
        CODE_TO_CATEGORY.put(60409, new ClassificationCategory(60409, 3,"Humanities --> Arts --> Performing Art Studies","Teatervetenskap"));
        CODE_TO_CATEGORY.put(60410, new ClassificationCategory(60410, 3,"Humanities --> Arts --> Studies on Film","Filmvetenskap"));
        CODE_TO_CATEGORY.put(605, new ClassificationCategory(605, 2,"Humanities --> Other Humanities","Annan humaniora"));
        CODE_TO_CATEGORY.put(60501, new ClassificationCategory(60501, 3,"Humanities --> Other Humanities --> Classical Archaeology and Ancient History","Antikvetenskap"));
        CODE_TO_CATEGORY.put(60502, new ClassificationCategory(60502, 3,"Humanities --> Other Humanities --> Cultural Studies","Kulturstudier"));
        CODE_TO_CATEGORY.put(60503, new ClassificationCategory(60503, 3,"Humanities --> Other Humanities --> Ethnology","Etnologi"));
        CODE_TO_CATEGORY.put(60599, new ClassificationCategory(60599, 3,"Humanities --> Other Humanities --> Other Humanities not elsewhere specified","Övrig annan humaniora"));







/*
        CODE_TO_CATEGORY.put(1, new ClassificationCategory(1, 1,"Natural sciences","Naturvetenskap"));
        CODE_TO_CATEGORY.put(101, new ClassificationCategory(101, 2,"Mathematics","Matematik"));
        CODE_TO_CATEGORY.put(10101, new ClassificationCategory(10101, 3,"Mathematical Analysis","Matematisk analys"));
        CODE_TO_CATEGORY.put(10102, new ClassificationCategory(10102, 3,"Geometry","Geometri"));
        CODE_TO_CATEGORY.put(10103, new ClassificationCategory(10103, 3,"Algebra and Logic","Algebra och logik"));
        CODE_TO_CATEGORY.put(10104, new ClassificationCategory(10104, 3,"Discrete Mathematics","Diskret matematik"));
        CODE_TO_CATEGORY.put(10105, new ClassificationCategory(10105, 3,"Computational Mathematics","Beräkningsmatematik"));
        CODE_TO_CATEGORY.put(10106, new ClassificationCategory(10106, 3,"Probability Theory and Statistics","Sannolikhetsteori och statistik"));
        CODE_TO_CATEGORY.put(10199, new ClassificationCategory(10199, 3,"Other Mathematics","Annan matematik"));
        CODE_TO_CATEGORY.put(102, new ClassificationCategory(102, 2,"Computer and Information Sciences","Data- och informationsvetenskap (Datateknik)"));
        CODE_TO_CATEGORY.put(10201, new ClassificationCategory(10201, 3,"Computer Sciences","Datavetenskap (datalogi)"));
        CODE_TO_CATEGORY.put(10202, new ClassificationCategory(10202, 3,"Information Systems (Social aspects to be 50804)","Systemvetenskap, informationssystem och informatik (samhällsvetenskaplig inriktning under 50804)"));
        CODE_TO_CATEGORY.put(10203, new ClassificationCategory(10203, 3,"Bioinformatics (Computational Biology) (applications to be 10610)","Bioinformatik (beräkningsbiologi) (tillämpningar under 10610)"));
        CODE_TO_CATEGORY.put(10204, new ClassificationCategory(10204, 3,"Human Computer Interaction (Social aspects to be 50803)","Människa-datorinteraktion (interaktionsdesign) (Samhällsvetenskapliga aspekter under 50803)"));
        CODE_TO_CATEGORY.put(10205, new ClassificationCategory(10205, 3,"Software Engineering","Programvaruteknik"));
        CODE_TO_CATEGORY.put(10206, new ClassificationCategory(10206, 3,"Computer Engineering","Datorteknik"));
        CODE_TO_CATEGORY.put(10207, new ClassificationCategory(10207, 3,"Computer Vision and Robotics (Autonomous Systems)","Datorseende och robotik (autonoma system)"));
        CODE_TO_CATEGORY.put(10208, new ClassificationCategory(10208, 3,"Language Technology (Computational Linguistics)","Språkteknologi (språkvetenskaplig databehandling)"));
        CODE_TO_CATEGORY.put(10209, new ClassificationCategory(10209, 3,"Media and Communication Technology","Medieteknik"));
        CODE_TO_CATEGORY.put(10299, new ClassificationCategory(10299, 3,"Other Computer and Information Science","Annan data- och informationsvetenskap"));
        CODE_TO_CATEGORY.put(103, new ClassificationCategory(103, 2,"Physical Sciences","Fysik"));
        CODE_TO_CATEGORY.put(10301, new ClassificationCategory(10301, 3,"Subatomic Physics","Subatomär fysik"));
        CODE_TO_CATEGORY.put(10302, new ClassificationCategory(10302, 3,"Atom and Molecular Physics and Optics","Atom- och molekylfysik och optik"));
        CODE_TO_CATEGORY.put(10303, new ClassificationCategory(10303, 3,"Fusion, Plasma and Space Physics","Fusion, plasma och rymdfysik"));
        CODE_TO_CATEGORY.put(10304, new ClassificationCategory(10304, 3,"Condensed Matter Physics","Den kondenserade materiens fysik"));
        CODE_TO_CATEGORY.put(10305, new ClassificationCategory(10305, 3,"Astronomy, Astrophysics and Cosmology","Astronomi, astrofysik och kosmologi"));
        CODE_TO_CATEGORY.put(10306, new ClassificationCategory(10306, 3,"Accelerator Physics and Instrumentation","Acceleratorfysik och instrumentering"));
        CODE_TO_CATEGORY.put(10399, new ClassificationCategory(10399, 3,"Other Physics Topics","Annan fysik"));
        CODE_TO_CATEGORY.put(104, new ClassificationCategory(104, 2,"Chemical Sciences","Kemi"));
        CODE_TO_CATEGORY.put(10401, new ClassificationCategory(10401, 3,"Analytical Chemistry","Analytisk kemi"));
        CODE_TO_CATEGORY.put(10402, new ClassificationCategory(10402, 3,"Physical Chemistry","Fysikalisk kemi"));
        CODE_TO_CATEGORY.put(10403, new ClassificationCategory(10403, 3,"Materials Chemistry","Materialkemi"));
        CODE_TO_CATEGORY.put(10404, new ClassificationCategory(10404, 3,"Inorganic Chemistry","Oorganisk kemi"));
        CODE_TO_CATEGORY.put(10405, new ClassificationCategory(10405, 3,"Organic Chemistry","Organisk kemi"));
        CODE_TO_CATEGORY.put(10406, new ClassificationCategory(10406, 3,"Polymer Chemistry","Polymerkemi"));
        CODE_TO_CATEGORY.put(10407, new ClassificationCategory(10407, 3,"Theoretical Chemistry","Teoretisk kemi"));
        CODE_TO_CATEGORY.put(10499, new ClassificationCategory(10499, 3,"Other Chemistry Topics","Annan kemi"));
        CODE_TO_CATEGORY.put(105, new ClassificationCategory(105, 2,"Earth and Related Environmental Sciences","Geovetenskap och miljövetenskap"));
        CODE_TO_CATEGORY.put(10501, new ClassificationCategory(10501, 3,"Climate Research","Klimatforskning"));
        CODE_TO_CATEGORY.put(10502, new ClassificationCategory(10502, 3,"Environmental Sciences (social aspects to be 507)","Miljövetenskap (Samhällsvetenskapliga aspekter under 507)"));
        CODE_TO_CATEGORY.put(10503, new ClassificationCategory(10503, 3,"Geosciences, Multidisciplinary","Multidisciplinär geovetenskap"));
        CODE_TO_CATEGORY.put(10504, new ClassificationCategory(10504, 3,"Geology","Geologi"));
        CODE_TO_CATEGORY.put(10505, new ClassificationCategory(10505, 3,"Geophysics","Geofysik"));
        CODE_TO_CATEGORY.put(10506, new ClassificationCategory(10506, 3,"Geochemistry","Geokemi"));
        CODE_TO_CATEGORY.put(10507, new ClassificationCategory(10507, 3,"Physical Geography","Naturgeografi"));
        CODE_TO_CATEGORY.put(10508, new ClassificationCategory(10508, 3,"Meteorology and Atmospheric Sciences","Meteorologi och atmosfärforskning"));
        CODE_TO_CATEGORY.put(10509, new ClassificationCategory(10509, 3,"Oceanography, Hydrology and Water Resources","Oceanografi, hydrologi och vattenresurser"));
        CODE_TO_CATEGORY.put(10599, new ClassificationCategory(10599, 3,"Other Earth and Related Environmental Sciences","Annan geovetenskap och miljövetenskap"));
        CODE_TO_CATEGORY.put(106, new ClassificationCategory(106, 2,"Biological Sciences  (Medical to be 3 and Agricultural to be 4)","Biologi (Medicinska tillämpningar under 3 och lantbruksvetenskapliga under 4)"));
        CODE_TO_CATEGORY.put(10601, new ClassificationCategory(10601, 3,"Structural Biology","Strukturbiologi"));
        CODE_TO_CATEGORY.put(10602, new ClassificationCategory(10602, 3,"Biochemistry and Molecular Biology","Biokemi och molekylärbiologi"));
        CODE_TO_CATEGORY.put(10603, new ClassificationCategory(10603, 3,"Biophysics","Biofysik"));
        CODE_TO_CATEGORY.put(10604, new ClassificationCategory(10604, 3,"Cell Biology","Cellbiologi"));
        CODE_TO_CATEGORY.put(10605, new ClassificationCategory(10605, 3,"Immunology (medical  to be 30110 and agricultural to be 40302)","Immunologi (medicinsk under 30110 och lantbruksvetenskaplig under 40302)"));
        CODE_TO_CATEGORY.put(10606, new ClassificationCategory(10606, 3,"Microbiology (medical  to be 30109 and agricultural to be 40302)","Mikrobiologi (medicinsk under 30109 och lantbruksvetenskaplig under 40302)"));
        CODE_TO_CATEGORY.put(10607, new ClassificationCategory(10607, 3,"Botany","Botanik"));
        CODE_TO_CATEGORY.put(10608, new ClassificationCategory(10608, 3,"Zoology","Zoologi"));
        CODE_TO_CATEGORY.put(10609, new ClassificationCategory(10609, 3,"Genetics (medical to be 30107 and agricultural to be 40402)","Genetik (medicinsk under 30107 och lantbruksvetenskaplig under 40402)"));
        CODE_TO_CATEGORY.put(10610, new ClassificationCategory(10610, 3,"Bioinformatics and Systems Biology (methods development to be 10203)","Bioinformatik och systembiologi (metodutveckling under 10203)"));
        CODE_TO_CATEGORY.put(10611, new ClassificationCategory(10611, 3,"Ecology","Ekologi"));
        CODE_TO_CATEGORY.put(10612, new ClassificationCategory(10612, 3,"Biological Systematics","Biologisk systematik"));
        CODE_TO_CATEGORY.put(10613, new ClassificationCategory(10613, 3,"Behavioural Sciences Biology","Etologi"));
        CODE_TO_CATEGORY.put(10614, new ClassificationCategory(10614, 3,"Developmental Biology","Utvecklingsbiologi"));
        CODE_TO_CATEGORY.put(10615, new ClassificationCategory(10615, 3,"Evolutionary Biology","Evolutionsbiologi"));
        CODE_TO_CATEGORY.put(10699, new ClassificationCategory(10699, 3,"Other Biological Topics","Annan biologi"));
        CODE_TO_CATEGORY.put(107, new ClassificationCategory(107, 2,"Other Natural Sciences","Annan naturvetenskap"));
        CODE_TO_CATEGORY.put(10799, new ClassificationCategory(10799, 3,"Other Natural Sciences not elsewhere specified","Övrig annan naturvetenskap"));
        CODE_TO_CATEGORY.put(2, new ClassificationCategory(2, 1,"Engineering and Technology","Teknik"));
        CODE_TO_CATEGORY.put(201, new ClassificationCategory(201, 2,"Civil Engineering","Samhällsbyggnadsteknik"));
        CODE_TO_CATEGORY.put(20101, new ClassificationCategory(20101, 3,"Architectural Engineering","Arkitekturteknik"));
        CODE_TO_CATEGORY.put(20102, new ClassificationCategory(20102, 3,"Construction Management","Byggproduktion"));
        CODE_TO_CATEGORY.put(20103, new ClassificationCategory(20103, 3,"Building Technologies","Husbyggnad"));
        CODE_TO_CATEGORY.put(20104, new ClassificationCategory(20104, 3,"Infrastructure Engineering","Infrastrukturteknik"));
        CODE_TO_CATEGORY.put(20105, new ClassificationCategory(20105, 3,"Transport Systems and Logistics","Transportteknik och logistik"));
        CODE_TO_CATEGORY.put(20106, new ClassificationCategory(20106, 3,"Geotechnical Engineering","Geoteknik"));
        CODE_TO_CATEGORY.put(20107, new ClassificationCategory(20107, 3,"Water Engineering","Vattenteknik"));
        CODE_TO_CATEGORY.put(20108, new ClassificationCategory(20108, 3,"Environmental Analysis and  Construction Information Technology","Miljöanalys och bygginformationsteknik"));
        CODE_TO_CATEGORY.put(20199, new ClassificationCategory(20199, 3,"Other Civil Engineering","Annan samhällsbyggnadsteknik"));
        CODE_TO_CATEGORY.put(202, new ClassificationCategory(202, 2,"Electrical Engineering, Electronic Engineering, Information Engineering","Elektroteknik och elektronik"));
        CODE_TO_CATEGORY.put(20201, new ClassificationCategory(20201, 3,"Robotics","Robotteknik och automation"));
        CODE_TO_CATEGORY.put(20202, new ClassificationCategory(20202, 3,"Control Engineering","Reglerteknik"));
        CODE_TO_CATEGORY.put(20203, new ClassificationCategory(20203, 3,"Communication Systems","Kommunikationssystem"));
        CODE_TO_CATEGORY.put(20204, new ClassificationCategory(20204, 3,"Telecommunications","Telekommunikation"));
        CODE_TO_CATEGORY.put(20205, new ClassificationCategory(20205, 3,"Signal Processing","Signalbehandling"));
        CODE_TO_CATEGORY.put(20206, new ClassificationCategory(20206, 3,"Computer Systems","Datorsystem"));
        CODE_TO_CATEGORY.put(20207, new ClassificationCategory(20207, 3,"Embedded Systems","Inbäddad systemteknik"));
        CODE_TO_CATEGORY.put(20299, new ClassificationCategory(20299, 3,"Other Electrical Engineering, Electronic Engineering, Information Engineering","Annan elektroteknik och elektronik"));
        CODE_TO_CATEGORY.put(203, new ClassificationCategory(203, 2,"Mechanical Engineering","Maskinteknik"));
        CODE_TO_CATEGORY.put(20301, new ClassificationCategory(20301, 3,"Applied Mechanics","Teknisk mekanik"));
        CODE_TO_CATEGORY.put(20302, new ClassificationCategory(20302, 3,"Aerospace Engineering","Rymd- och flygteknik"));
        CODE_TO_CATEGORY.put(20303, new ClassificationCategory(20303, 3,"Vehicle Engineering","Farkostteknik"));
        CODE_TO_CATEGORY.put(20304, new ClassificationCategory(20304, 3,"Energy Engineering","Energiteknik"));
        CODE_TO_CATEGORY.put(20305, new ClassificationCategory(20305, 3,"Reliability and Maintenance","Tillförlitlighets- och kvalitetsteknik"));
        CODE_TO_CATEGORY.put(20306, new ClassificationCategory(20306, 3,"Fluid Mechanics and Acoustics","Strömningsmekanik och akustik"));
        CODE_TO_CATEGORY.put(20307, new ClassificationCategory(20307, 3,"Production Engineering, Human Work Science and Ergonomics","Produktionsteknik, arbetsvetenskap och ergonomi"));
        CODE_TO_CATEGORY.put(20308, new ClassificationCategory(20308, 3,"Tribology (interacting surfaces including Friction, Lubrication and Wear)","Tribologi (ytteknik omfattande friktion, nötning och smörjning)"));
        CODE_TO_CATEGORY.put(20399, new ClassificationCategory(20399, 3,"Other Mechanical Engineering","Annan maskinteknik"));
        CODE_TO_CATEGORY.put(204, new ClassificationCategory(204, 2,"Chemical Engineering","Kemiteknik"));
        CODE_TO_CATEGORY.put(20401, new ClassificationCategory(20401, 3,"Chemical Process Engineering","Kemiska processer"));
        CODE_TO_CATEGORY.put(20402, new ClassificationCategory(20402, 3,"Corrosion Engineering","Korrosionsteknik"));
        CODE_TO_CATEGORY.put(20403, new ClassificationCategory(20403, 3,"Polymer Technologies","Polymerteknologi"));
        CODE_TO_CATEGORY.put(20404, new ClassificationCategory(20404, 3,"Pharmaceutical Chemistry","Farmaceutisk synteskemi"));
        CODE_TO_CATEGORY.put(20499, new ClassificationCategory(20499, 3,"Other Chemical Engineering","Annan kemiteknik"));
        CODE_TO_CATEGORY.put(205, new ClassificationCategory(205, 2,"Materials Engineering","Materialteknik"));
        CODE_TO_CATEGORY.put(20501, new ClassificationCategory(20501, 3,"Ceramics","Keramteknik"));
        CODE_TO_CATEGORY.put(20502, new ClassificationCategory(20502, 3,"Composite Science and Engineering","Kompositmaterial och -teknik"));
        CODE_TO_CATEGORY.put(20503, new ClassificationCategory(20503, 3,"Paper, Pulp and Fiber Technology","Pappers-, massa-  och fiberteknik"));
        CODE_TO_CATEGORY.put(20504, new ClassificationCategory(20504, 3,"Textile, Rubber and Polymeric Materials","Textil-, gummi- och polymermaterial"));
        CODE_TO_CATEGORY.put(20505, new ClassificationCategory(20505, 3,"Manufacturing, Surface and Joining Technology","Bearbetnings-, yt- och fogningsteknik"));
        CODE_TO_CATEGORY.put(20506, new ClassificationCategory(20506, 3,"Metallurgy and Metallic Materials","Metallurgi och metalliska material"));
        CODE_TO_CATEGORY.put(20599, new ClassificationCategory(20599, 3,"Other Materials Engineering","Annan materialteknik"));
        CODE_TO_CATEGORY.put(206, new ClassificationCategory(206, 2,"Medical Engineering","Medicinteknik"));
        CODE_TO_CATEGORY.put(20601, new ClassificationCategory(20601, 3,"Medical Laboratory and Measurements Technologies","Medicinsk laboratorie- och mätteknik"));
        CODE_TO_CATEGORY.put(20602, new ClassificationCategory(20602, 3,"Medical Materials","Medicinsk material- och protesteknik"));
        CODE_TO_CATEGORY.put(20603, new ClassificationCategory(20603, 3,"Medical Image Processing","Medicinsk bildbehandling"));
        CODE_TO_CATEGORY.put(20604, new ClassificationCategory(20604, 3,"Medical Equipment Engineering","Medicinsk apparatteknik"));
        CODE_TO_CATEGORY.put(20605, new ClassificationCategory(20605, 3,"Medical Ergonomics","Medicinsk ergonomi"));
        CODE_TO_CATEGORY.put(20699, new ClassificationCategory(20699, 3,"Other Medical Engineering","Annan medicinteknik"));
        CODE_TO_CATEGORY.put(207, new ClassificationCategory(207, 2,"Environmental Engineering","Naturresursteknik"));
        CODE_TO_CATEGORY.put(20701, new ClassificationCategory(20701, 3,"Geophysical Engineering","Geofysisk teknik"));
        CODE_TO_CATEGORY.put(20702, new ClassificationCategory(20702, 3,"Energy Systems","Energisystem"));
        CODE_TO_CATEGORY.put(20703, new ClassificationCategory(20703, 3,"Remote Sensing","Fjärranalysteknik"));
        CODE_TO_CATEGORY.put(20704, new ClassificationCategory(20704, 3,"Mineral and Mine Engineering","Mineral- och gruvteknik"));
        CODE_TO_CATEGORY.put(20705, new ClassificationCategory(20705, 3,"Marine Engineering","Marin teknik"));
        CODE_TO_CATEGORY.put(20706, new ClassificationCategory(20706, 3,"Ocean and River Engineering","Havs- och vattendragsteknik"));
        CODE_TO_CATEGORY.put(20707, new ClassificationCategory(20707, 3,"Environmental Management","Miljöledning"));
        CODE_TO_CATEGORY.put(20799, new ClassificationCategory(20799, 3,"Other Environmental Engineering","Annan naturresursteknik"));
        CODE_TO_CATEGORY.put(208, new ClassificationCategory(208, 2,"Environmental Biotechnology","Miljöbioteknik"));
        CODE_TO_CATEGORY.put(20801, new ClassificationCategory(20801, 3,"Bioremediation","Biosanering"));
        CODE_TO_CATEGORY.put(20802, new ClassificationCategory(20802, 3,"Diagnostic Biotechnology","Diagnostisk bioteknologi"));
        CODE_TO_CATEGORY.put(20803, new ClassificationCategory(20803, 3,"Water Treatment","Vattenbehandling"));
        CODE_TO_CATEGORY.put(20804, new ClassificationCategory(20804, 3,"Bioethics","Bioteknisk etik"));
        CODE_TO_CATEGORY.put(20899, new ClassificationCategory(20899, 3,"Other Environmental Biotechnology","Annan miljöbioteknik"));
        CODE_TO_CATEGORY.put(209, new ClassificationCategory(209, 2,"Industrial Biotechnology","Industriell bioteknik"));
        CODE_TO_CATEGORY.put(20901, new ClassificationCategory(20901, 3,"Bioprocess Technology","Bioprocessteknik"));
        CODE_TO_CATEGORY.put(20902, new ClassificationCategory(20902, 3,"Biochemicals","Biokemikalier"));
        CODE_TO_CATEGORY.put(20903, new ClassificationCategory(20903, 3,"Bio Materials","Biomaterial"));
        CODE_TO_CATEGORY.put(20904, new ClassificationCategory(20904, 3,"Bioenergy","Bioenergi"));
        CODE_TO_CATEGORY.put(20905, new ClassificationCategory(20905, 3,"Pharmaceutical Biotechnology","Läkemedelsbioteknik"));
        CODE_TO_CATEGORY.put(20906, new ClassificationCategory(20906, 3,"Biocatalysis and Enzyme Technology","Biokatalys och enzymteknik"));
        CODE_TO_CATEGORY.put(20907, new ClassificationCategory(20907, 3,"Bioengineering Equipment","Bioteknisk apparatteknik"));
        CODE_TO_CATEGORY.put(20908, new ClassificationCategory(20908, 3,"Medical Biotechnology","Medicinsk bioteknik"));
        CODE_TO_CATEGORY.put(20999, new ClassificationCategory(20999, 3,"Other Industrial Biotechnology","Annan industriell bioteknik"));
        CODE_TO_CATEGORY.put(210, new ClassificationCategory(210, 2,"Nano-technology","Nanoteknik"));
        CODE_TO_CATEGORY.put(21001, new ClassificationCategory(21001, 3,"Nano-technology","Nanoteknik"));
        CODE_TO_CATEGORY.put(211, new ClassificationCategory(211, 2,"Other Engineering and Technologies","Annan teknik"));
        CODE_TO_CATEGORY.put(21101, new ClassificationCategory(21101, 3,"Food Engineering","Livsmedelsteknik"));
        CODE_TO_CATEGORY.put(21102, new ClassificationCategory(21102, 3,"Media Engineering","Mediateknik"));
        CODE_TO_CATEGORY.put(21103, new ClassificationCategory(21103, 3,"Interaction Technologies","Interaktionsteknik"));
        CODE_TO_CATEGORY.put(21199, new ClassificationCategory(21199, 3,"Other Engineering and Technologies not elsewhere specified","Övrig annan teknik"));
        CODE_TO_CATEGORY.put(3, new ClassificationCategory(3, 1,"Medical and Health Sciences","Medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(301, new ClassificationCategory(301, 2,"Basic Medicine","Medicinska och farmaceutiska grundvetenskaper"));
        CODE_TO_CATEGORY.put(30101, new ClassificationCategory(30101, 3,"Pharmaceutical Sciences","Farmaceutiska vetenskaper"));
        CODE_TO_CATEGORY.put(30102, new ClassificationCategory(30102, 3,"Pharmacology and Toxicology","Farmakologi och toxikologi"));
        CODE_TO_CATEGORY.put(30103, new ClassificationCategory(30103, 3,"Medicinal Chemistry","Läkemedelskemi"));
        CODE_TO_CATEGORY.put(30104, new ClassificationCategory(30104, 3,"Social and Clinical Pharmacy","Samhällsfarmaci och klinisk farmaci"));
        CODE_TO_CATEGORY.put(30105, new ClassificationCategory(30105, 3,"Neurosciences","Neurovetenskaper"));
        CODE_TO_CATEGORY.put(30106, new ClassificationCategory(30106, 3,"Physiology","Fysiologi"));
        CODE_TO_CATEGORY.put(30107, new ClassificationCategory(30107, 3,"Medical Genetics","Medicinsk genetik"));
        CODE_TO_CATEGORY.put(30108, new ClassificationCategory(30108, 3,"Cell and Molecular Biology","Cell- och molekylärbiologi"));
        CODE_TO_CATEGORY.put(30109, new ClassificationCategory(30109, 3,"Microbiology in the medical area","Mikrobiologi inom det medicinska området"));
        CODE_TO_CATEGORY.put(30110, new ClassificationCategory(30110, 3,"Immunology in the medical area","Immunologi inom det medicinska området"));
        CODE_TO_CATEGORY.put(30199, new ClassificationCategory(30199, 3,"Other Basic Medicine","Andra medicinska och farmaceutiska grundvetenskaper"));
        CODE_TO_CATEGORY.put(302, new ClassificationCategory(302, 2,"Clinical Medicine","Klinisk medicin"));
        CODE_TO_CATEGORY.put(30201, new ClassificationCategory(30201, 3,"Anesthesiology and Intensive Care","Anestesi och intensivvård"));
        CODE_TO_CATEGORY.put(30202, new ClassificationCategory(30202, 3,"Hematology","Hematologi"));
        CODE_TO_CATEGORY.put(30203, new ClassificationCategory(30203, 3,"Cancer and Oncology","Cancer och onkologi"));
        CODE_TO_CATEGORY.put(30204, new ClassificationCategory(30204, 3,"Dermatology and Venereal Diseases","Dermatologi och venereologi"));
        CODE_TO_CATEGORY.put(30205, new ClassificationCategory(30205, 3,"Endocrinology and Diabetes","Endokrinologi och diabetes"));
        CODE_TO_CATEGORY.put(30206, new ClassificationCategory(30206, 3,"Cardiac and Cardiovascular Systems","Kardiologi"));
        CODE_TO_CATEGORY.put(30207, new ClassificationCategory(30207, 3,"Neurology","Neurologi"));
        CODE_TO_CATEGORY.put(30208, new ClassificationCategory(30208, 3,"Radiology, Nuclear Medicine and Medical Imaging","Radiologi och bildbehandling"));
        CODE_TO_CATEGORY.put(30209, new ClassificationCategory(30209, 3,"Infectious Medicine","Infektionsmedicin"));
        CODE_TO_CATEGORY.put(30210, new ClassificationCategory(30210, 3,"Rheumatology and Autoimmunity","Reumatologi och inflammation"));
        CODE_TO_CATEGORY.put(30211, new ClassificationCategory(30211, 3,"Orthopaedics","Ortopedi"));
        CODE_TO_CATEGORY.put(30212, new ClassificationCategory(30212, 3,"Surgery","Kirurgi"));
        CODE_TO_CATEGORY.put(30213, new ClassificationCategory(30213, 3,"Gastroenterology and Hepatology","Gastroenterologi"));
        CODE_TO_CATEGORY.put(30214, new ClassificationCategory(30214, 3,"Urology and Nephrology","Urologi och njurmedicin"));
        CODE_TO_CATEGORY.put(30215, new ClassificationCategory(30215, 3,"Psychiatry","Psykiatri"));
        CODE_TO_CATEGORY.put(30216, new ClassificationCategory(30216, 3,"Dentistry","Odontologi"));
        CODE_TO_CATEGORY.put(30217, new ClassificationCategory(30217, 3,"Ophthalmology","Oftalmologi"));
        CODE_TO_CATEGORY.put(30218, new ClassificationCategory(30218, 3,"Otorhinolaryngology","Oto-rhino-laryngologi"));
        CODE_TO_CATEGORY.put(30219, new ClassificationCategory(30219, 3,"Respiratory Medicine and Allergy","Lungmedicin och allergi"));
        CODE_TO_CATEGORY.put(30220, new ClassificationCategory(30220, 3,"Obstetrics, Gynaecology and Reproductive Medicine","Reproduktionsmedicin och gynekologi"));
        CODE_TO_CATEGORY.put(30221, new ClassificationCategory(30221, 3,"Pediatrics","Pediatrik"));
        CODE_TO_CATEGORY.put(30222, new ClassificationCategory(30222, 3,"Geriatrics","Geriatrik"));
        CODE_TO_CATEGORY.put(30223, new ClassificationCategory(30223, 3,"Clinical Laboratory Medicine","Klinisk laboratoriemedicin"));
        CODE_TO_CATEGORY.put(30224, new ClassificationCategory(30224, 3,"General Practice","Allmänmedicin"));
        CODE_TO_CATEGORY.put(30299, new ClassificationCategory(30299, 3,"Other Clinical Medicine","Annan klinisk medicin"));
        CODE_TO_CATEGORY.put(303, new ClassificationCategory(303, 2,"Health Sciences","Hälsovetenskap"));
        CODE_TO_CATEGORY.put(30301, new ClassificationCategory(30301, 3,"Health Care Service and Management, Health Policy and Services and Health Economy","Hälso- och sjukvårdsorganisation, hälsopolitik och hälsoekonomi"));
        CODE_TO_CATEGORY.put(30302, new ClassificationCategory(30302, 3,"Public Health, Global Health, Social Medicine and Epidemiology","Folkhälsovetenskap, global hälsa, socialmedicin och epidemiologi"));
        CODE_TO_CATEGORY.put(30303, new ClassificationCategory(30303, 3,"Occupational Health and Environmental Health","Arbetsmedicin och miljömedicin"));
        CODE_TO_CATEGORY.put(30304, new ClassificationCategory(30304, 3,"Nutrition and Dietetics","Näringslära"));
        CODE_TO_CATEGORY.put(30305, new ClassificationCategory(30305, 3,"Nursing","Omvårdnad"));
        CODE_TO_CATEGORY.put(30306, new ClassificationCategory(30306, 3,"Occupational Therapy","Arbetsterapi"));
        CODE_TO_CATEGORY.put(30307, new ClassificationCategory(30307, 3,"Physiotherapy","Sjukgymnastik"));
        CODE_TO_CATEGORY.put(30308, new ClassificationCategory(30308, 3,"Sport and Fitness Sciences","Idrottsvetenskap"));
        CODE_TO_CATEGORY.put(30309, new ClassificationCategory(30309, 3,"Substance Abuse","Beroendelära"));
        CODE_TO_CATEGORY.put(30310, new ClassificationCategory(30310, 3,"Medical Ethics","Medicinsk etik"));
        CODE_TO_CATEGORY.put(30399, new ClassificationCategory(30399, 3,"Other Health Sciences","Annan hälsovetenskap"));
        CODE_TO_CATEGORY.put(304, new ClassificationCategory(304, 2,"Medical Biotechnology","Medicinsk bioteknologi"));
        CODE_TO_CATEGORY.put(30401, new ClassificationCategory(30401, 3,"Medical Biotechnology (focus on Cell Biology (incl. Stem Cell Biology), Molecular Biology, Microbiology, Biochemistry or Biopharmacy)","Medicinsk bioteknologi (inriktn. mot cellbiologi (inkl. stamcellsbiologi), molekylärbiologi, mikrobiologi, biokemi eller biofarmaci)"));
        CODE_TO_CATEGORY.put(30402, new ClassificationCategory(30402, 3,"Biomedical Laboratory Science/Technology","Biomedicinsk laboratorievetenskap/teknologi"));
        CODE_TO_CATEGORY.put(30403, new ClassificationCategory(30403, 3,"Biomaterials Science","Biomaterialvetenskap"));
        CODE_TO_CATEGORY.put(30499, new ClassificationCategory(30499, 3,"Other Medical Biotechnology","Annan medicinsk bioteknologi"));
        CODE_TO_CATEGORY.put(305, new ClassificationCategory(305, 2,"Other Medical and Health Sciences","Annan medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(30501, new ClassificationCategory(30501, 3,"Forensic Science","Rättsmedicin"));
        CODE_TO_CATEGORY.put(30502, new ClassificationCategory(30502, 3,"Gerontology, specialising in Medical and Health Sciences (specialising in Social Sciences to be 50999)","Gerontologi, medicinsk/hälsovetenskaplig inriktning  (Samhällsvetenskaplig inriktn.under 50999)"));
        CODE_TO_CATEGORY.put(30599, new ClassificationCategory(30599, 3,"Other Medical and Health Sciences not elsewhere specified","Övrig annan medicin och hälsovetenskap"));
        CODE_TO_CATEGORY.put(4, new ClassificationCategory(4, 1,"Agricultural Sciences","Lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(401, new ClassificationCategory(401, 2,"Agricultural, Forestry and Fisheries","Lantbruksvetenskap, skogsbruk och fiske"));
        CODE_TO_CATEGORY.put(40101, new ClassificationCategory(40101, 3,"Agricultural Science","Jordbruksvetenskap"));
        CODE_TO_CATEGORY.put(40102, new ClassificationCategory(40102, 3,"Horticulture","Trädgårdsvetenskap/hortikultur"));
        CODE_TO_CATEGORY.put(40103, new ClassificationCategory(40103, 3,"Food Science","Livsmedelsvetenskap"));
        CODE_TO_CATEGORY.put(40104, new ClassificationCategory(40104, 3,"Forest Science","Skogsvetenskap"));
        CODE_TO_CATEGORY.put(40105, new ClassificationCategory(40105, 3,"Wood Science","Trävetenskap"));
        CODE_TO_CATEGORY.put(40106, new ClassificationCategory(40106, 3,"Soil Science","Markvetenskap"));
        CODE_TO_CATEGORY.put(40107, new ClassificationCategory(40107, 3,"Fish and Aquacultural Science","Fisk- och akvakulturforskning"));
        CODE_TO_CATEGORY.put(40108, new ClassificationCategory(40108, 3,"Landscape Architecture","Landskapsarkitektur"));
        CODE_TO_CATEGORY.put(402, new ClassificationCategory(402, 2,"Animal and Dairy Sience","Husdjursvetenskap"));
        CODE_TO_CATEGORY.put(40201, new ClassificationCategory(40201, 3,"Animal and Dairy Science.","Husdjursvetenskap"));
        CODE_TO_CATEGORY.put(403, new ClassificationCategory(403, 2,"Veterinary Science","Veterinärmedicin"));
        CODE_TO_CATEGORY.put(40301, new ClassificationCategory(40301, 3,"Medical Bioscience","Medicinsk biovetenskap"));
        CODE_TO_CATEGORY.put(40302, new ClassificationCategory(40302, 3,"Pathobiology","Patobiologi"));
        CODE_TO_CATEGORY.put(40303, new ClassificationCategory(40303, 3,"Clinical Science","Klinisk vetenskap"));
        CODE_TO_CATEGORY.put(40304, new ClassificationCategory(40304, 3,"Other Veterinary Science","Annan veterinärmedicin"));
        CODE_TO_CATEGORY.put(404, new ClassificationCategory(404, 2,"Agricultural Biotechnology","Bioteknologi med applikationer på växter och djur"));
        CODE_TO_CATEGORY.put(40401, new ClassificationCategory(40401, 3,"Plant Biotechnology","Växtbioteknologi"));
        CODE_TO_CATEGORY.put(40402, new ClassificationCategory(40402, 3,"Genetics and Breeding in Agricultural Sciences","Genetik och förädling inom lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(405, new ClassificationCategory(405, 2,"Other Agricultural Sciences","Annan lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(40501, new ClassificationCategory(40501, 3,"Renewable Bioenergy Research","Förnyelsebar bioenergi"));
        CODE_TO_CATEGORY.put(40502, new ClassificationCategory(40502, 3,"Fish and Wildlife Management","Vilt- och fiskeförvaltning"));
        CODE_TO_CATEGORY.put(40503, new ClassificationCategory(40503, 3,"Agricultural Occupational Health and Safety","Lantbrukets arbetsmiljö och säkerhet"));
        CODE_TO_CATEGORY.put(40504, new ClassificationCategory(40504, 3,"Environmental Sciences related to Agriculture and Land-use","Miljö- och  naturvårdsvetenskap"));
        CODE_TO_CATEGORY.put(40599, new ClassificationCategory(40599, 3,"Other Agricultural Sciences not elsewhere specified","Övrig annan lantbruksvetenskap"));
        CODE_TO_CATEGORY.put(5, new ClassificationCategory(5, 1,"Social Sciences","Samhällsvetenskap"));
        CODE_TO_CATEGORY.put(501, new ClassificationCategory(501, 2,"Psychology","Psykologi"));
        CODE_TO_CATEGORY.put(50101, new ClassificationCategory(50101, 3,"Psychology (excluding Applied Psychology)","Psykologi  (exklusive tillämpad psykologi)"));
        CODE_TO_CATEGORY.put(50102, new ClassificationCategory(50102, 3,"Applied Psychology","Tillämpad psykologi"));
        CODE_TO_CATEGORY.put(502, new ClassificationCategory(502, 2,"Economics and Business","Ekonomi och näringsliv"));
        CODE_TO_CATEGORY.put(50201, new ClassificationCategory(50201, 3,"Economics","Nationalekonomi"));
        CODE_TO_CATEGORY.put(50202, new ClassificationCategory(50202, 3,"Business Administration","Företagsekonomi"));
        CODE_TO_CATEGORY.put(50203, new ClassificationCategory(50203, 3,"Economic History","Ekonomisk historia"));
        CODE_TO_CATEGORY.put(503, new ClassificationCategory(503, 2,"Educational Sciences","Utbildningsvetenskap"));
        CODE_TO_CATEGORY.put(50301, new ClassificationCategory(50301, 3,"Pedagogy","Pedagogik"));
        CODE_TO_CATEGORY.put(50302, new ClassificationCategory(50302, 3,"Didactics","Didaktik"));
        CODE_TO_CATEGORY.put(50303, new ClassificationCategory(50303, 3,"Learning","Lärande"));
        CODE_TO_CATEGORY.put(50304, new ClassificationCategory(50304, 3,"Pedagogical Work","Pedagogiskt arbete"));
        CODE_TO_CATEGORY.put(504, new ClassificationCategory(504, 2,"Sociology","Sociologi"));
        CODE_TO_CATEGORY.put(50401, new ClassificationCategory(50401, 3,"Sociology (excluding Social Work, Social Psychology and Social Anthropology)","Sociologi (exklusive socialt arbete, socialpsykologi och socialantropologi)"));
        CODE_TO_CATEGORY.put(50402, new ClassificationCategory(50402, 3,"Social Work","Socialt arbete"));
        CODE_TO_CATEGORY.put(50403, new ClassificationCategory(50403, 3,"Social Psychology","Socialpsykologi"));
        CODE_TO_CATEGORY.put(50404, new ClassificationCategory(50404, 3,"Social Anthropology","Socialantropologi"));
        CODE_TO_CATEGORY.put(505, new ClassificationCategory(505, 2,"Law","Juridik"));
        CODE_TO_CATEGORY.put(50501, new ClassificationCategory(50501, 3,"Law (excluding Law and Society)","Juridik (exklusive juridik och samhälle)"));
        CODE_TO_CATEGORY.put(50502, new ClassificationCategory(50502, 3,"Law and Society","Juridik och samhälle"));
        CODE_TO_CATEGORY.put(506, new ClassificationCategory(506, 2,"Political Science","Statsvetenskap"));
        CODE_TO_CATEGORY.put(50601, new ClassificationCategory(50601, 3,"Political Science (excluding Public Administration Studies and Globalisation Studies)","Statsvetenskap (exklusive studier av offentlig förvaltning och globaliseringsstudier)"));
        CODE_TO_CATEGORY.put(50602, new ClassificationCategory(50602, 3,"Public Administration Studies","Studier av offentlig förvaltning"));
        CODE_TO_CATEGORY.put(50603, new ClassificationCategory(50603, 3,"Globalisation Studies","Globaliseringsstudier"));
        CODE_TO_CATEGORY.put(507, new ClassificationCategory(507, 2,"Social and Economic Geography","Social och ekonomisk geografi"));
        CODE_TO_CATEGORY.put(50701, new ClassificationCategory(50701, 3,"Human Geography","Kulturgeografi"));
        CODE_TO_CATEGORY.put(50702, new ClassificationCategory(50702, 3,"Economic Geography","Ekonomisk geografi"));
        CODE_TO_CATEGORY.put(508, new ClassificationCategory(508, 2,"Media and Communications","Medie- och kommunikationsvetenskap"));
        CODE_TO_CATEGORY.put(50801, new ClassificationCategory(50801, 3,"Media Studies","Medievetenskap"));
        CODE_TO_CATEGORY.put(50802, new ClassificationCategory(50802, 3,"Communication Studies","Kommunikationsvetenskap"));
        CODE_TO_CATEGORY.put(50803, new ClassificationCategory(50803, 3,"Human Aspects of ICT","Mänsklig interaktion med IKT"));
        CODE_TO_CATEGORY.put(50804, new ClassificationCategory(50804, 3,"Information Systems, Social aspects","Systemvetenskap, informationssystem och informatik med samhällsvetenskaplig inriktning"));
        CODE_TO_CATEGORY.put(50805, new ClassificationCategory(50805, 3,"Information Studies","Biblioteks-och informationsvetenskap"));
        CODE_TO_CATEGORY.put(509, new ClassificationCategory(509, 2,"Other Social Sciences","Annan samhällsvetenskap"));
        CODE_TO_CATEGORY.put(50901, new ClassificationCategory(50901, 3,"Social Sciences Interdisciplinary","Tvärvetenskapliga studier inom samhällsvetenskap"));
        CODE_TO_CATEGORY.put(50902, new ClassificationCategory(50902, 3,"Gender Studies","Genusstudier"));
        CODE_TO_CATEGORY.put(50903, new ClassificationCategory(50903, 3,"Work Sciences","Arbetslivsstudier"));
        CODE_TO_CATEGORY.put(50904, new ClassificationCategory(50904, 3,"International Migration and Ethnic Relations","Internationell migration och etniska relationer (IMER)"));
        CODE_TO_CATEGORY.put(50999, new ClassificationCategory(50999, 3,"Other Social Sciences not elsewhere specified","Övrig annan samhällsvetenskap"));
        CODE_TO_CATEGORY.put(6, new ClassificationCategory(6, 1,"Humanities","Humaniora"));
        CODE_TO_CATEGORY.put(601, new ClassificationCategory(601, 2,"History and Archaeology","Historia och arkeologi"));
        CODE_TO_CATEGORY.put(60101, new ClassificationCategory(60101, 3,"History","Historia"));
        CODE_TO_CATEGORY.put(60102, new ClassificationCategory(60102, 3,"History of Technology","Teknikhistoria"));
        CODE_TO_CATEGORY.put(60103, new ClassificationCategory(60103, 3,"Archaeology","Arkeologi"));
        CODE_TO_CATEGORY.put(602, new ClassificationCategory(602, 2,"Languages and Literature","Språk och litteratur"));
        CODE_TO_CATEGORY.put(60201, new ClassificationCategory(60201, 3,"General Language Studies and Linguistics","Jämförande språkvetenskap och allmän lingvistik"));
        CODE_TO_CATEGORY.put(60202, new ClassificationCategory(60202, 3,"Specific Languages","Studier av enskilda språk"));
        CODE_TO_CATEGORY.put(60203, new ClassificationCategory(60203, 3,"General Literary studies","Litteraturvetenskap"));
        CODE_TO_CATEGORY.put(60204, new ClassificationCategory(60204, 3,"Specific Literatures","Litteraturstudier"));
        CODE_TO_CATEGORY.put(603, new ClassificationCategory(603, 2,"Philosophy, Ethics and Religion","Filosofi, etik och religion"));
        CODE_TO_CATEGORY.put(60301, new ClassificationCategory(60301, 3,"Philosophy","Filosofi"));
        CODE_TO_CATEGORY.put(60302, new ClassificationCategory(60302, 3,"Ethics","Etik"));
        CODE_TO_CATEGORY.put(60303, new ClassificationCategory(60303, 3,"Religious Studies","Religionsvetenskap"));
        CODE_TO_CATEGORY.put(60304, new ClassificationCategory(60304, 3,"History of Religions","Religionshistoria"));
        CODE_TO_CATEGORY.put(60305, new ClassificationCategory(60305, 3,"History of Ideas","Idé- och lärdomshistoria"));
        CODE_TO_CATEGORY.put(604, new ClassificationCategory(604, 2,"Arts","Konst"));
        CODE_TO_CATEGORY.put(60401, new ClassificationCategory(60401, 3,"Visual Arts","Bildkonst"));
        CODE_TO_CATEGORY.put(60402, new ClassificationCategory(60402, 3,"Music","Musik"));
        CODE_TO_CATEGORY.put(60403, new ClassificationCategory(60403, 3,"Literary Composition","Litterär gestaltning"));
        CODE_TO_CATEGORY.put(60404, new ClassificationCategory(60404, 3,"Performing Arts","Scenkonst"));
        CODE_TO_CATEGORY.put(60405, new ClassificationCategory(60405, 3,"Architecture","Arkitektur"));
        CODE_TO_CATEGORY.put(60406, new ClassificationCategory(60406, 3,"Design","Design"));
        CODE_TO_CATEGORY.put(60407, new ClassificationCategory(60407, 3,"Art History","Konstvetenskap"));
        CODE_TO_CATEGORY.put(60408, new ClassificationCategory(60408, 3,"Musicology","Musikvetenskap"));
        CODE_TO_CATEGORY.put(60409, new ClassificationCategory(60409, 3,"Performing Art Studies","Teatervetenskap"));
        CODE_TO_CATEGORY.put(60410, new ClassificationCategory(60410, 3,"Studies on Film","Filmvetenskap"));
        CODE_TO_CATEGORY.put(605, new ClassificationCategory(605, 2,"Other Humanities","Annan humaniora"));
        CODE_TO_CATEGORY.put(60501, new ClassificationCategory(60501, 3,"Classical Archaeology and Ancient History","Antikvetenskap"));
        CODE_TO_CATEGORY.put(60502, new ClassificationCategory(60502, 3,"Cultural Studies","Kulturstudier"));
        CODE_TO_CATEGORY.put(60503, new ClassificationCategory(60503, 3,"Ethnology","Etnologi"));
        CODE_TO_CATEGORY.put(60599, new ClassificationCategory(60599, 3,"Other Humanities not elsewhere specified","Övrig annan humaniora"));




*/


    }



    public static ClassificationCategory getCategoryInfo(Integer code) {


        return CODE_TO_CATEGORY.get(code);

    }



}
