package misc.stopwordLists;

import java.util.HashSet;

/**
 * Created by Cristian on 2016-10-28.
 */
public class SwedishStopWords60 implements Stopwordlist {

    private static HashSet<String> swedishStopwords60 = new HashSet<>(100);
    static {

        //top 60 general swedish terms

        swedishStopwords60.add("i");
        swedishStopwords60.add("och");
        swedishStopwords60.add("att");
        swedishStopwords60.add("det");
        swedishStopwords60.add("som");
        swedishStopwords60.add("en");
        swedishStopwords60.add("på");
        swedishStopwords60.add("är");
        swedishStopwords60.add("av");
        swedishStopwords60.add("för");
        swedishStopwords60.add("med");
        swedishStopwords60.add("till");
        swedishStopwords60.add("den");
        swedishStopwords60.add("har");
        swedishStopwords60.add("de");
        swedishStopwords60.add("inte");
        swedishStopwords60.add("om");
        swedishStopwords60.add("ett");
        swedishStopwords60.add("han");
        swedishStopwords60.add("men");
        swedishStopwords60.add("var");
        swedishStopwords60.add("jag");
        swedishStopwords60.add("sig");
        swedishStopwords60.add("från");
        swedishStopwords60.add("vi");
        swedishStopwords60.add("så");
        swedishStopwords60.add("kan");
        swedishStopwords60.add("man");
        swedishStopwords60.add("när");
        swedishStopwords60.add("år");
        swedishStopwords60.add("säger");
        swedishStopwords60.add("hon");
        swedishStopwords60.add("under");
        swedishStopwords60.add("också");
        swedishStopwords60.add("efter");
        swedishStopwords60.add("eller");
        swedishStopwords60.add("nu");
        swedishStopwords60.add("sin");
        swedishStopwords60.add("där");
        swedishStopwords60.add("vid");
        swedishStopwords60.add("mot");
        swedishStopwords60.add("ska");
        swedishStopwords60.add("skulle");
        swedishStopwords60.add("kommer");
        swedishStopwords60.add("ut");
        swedishStopwords60.add("får");
        swedishStopwords60.add("finns");
        swedishStopwords60.add("vara");
        swedishStopwords60.add("hade");
        swedishStopwords60.add("alla");
        swedishStopwords60.add("andra");
        swedishStopwords60.add("mycket");
        swedishStopwords60.add("än");
        swedishStopwords60.add("här");
        swedishStopwords60.add("då");
        swedishStopwords60.add("sedan");
        swedishStopwords60.add("över");
        swedishStopwords60.add("bara");
        swedishStopwords60.add("in");
        swedishStopwords60.add("blir");

        //some very common terms based on SwePub (pre-stemmed)

        swedishStopwords60.add("olika");
        swedishStopwords60.add("hur");
        swedishStopwords60.add("ell");
        swedishStopwords60.add("und");
        swedishStopwords60.add("mellan");
        swedishStopwords60.add("denna");
        swedishStopwords60.add("detta");
        swedishStopwords60.add("inom");
        swedishStopwords60.add("dessa");
        swedishStopwords60.add("genom");
        swedishStopwords60.add("samt");
        swedishStopwords60.add("studi");
        swedishStopwords60.add("finn");
        swedishStopwords60.add("även");
        swedishStopwords60.add("vis");
        swedishStopwords60.add("del");
        swedishStopwords60.add("vilk");
        swedishStopwords60.add("hos");
        swedishStopwords60.add("vad");
        swedishStopwords60.add("mer");
        swedishStopwords60.add("två");
        swedishStopwords60.add("metod");
        swedishStopwords60.add("eft");
        swedishStopwords60.add("sätt");
        swedishStopwords60.add("nya");
        swedishStopwords60.add("både");
        swedishStopwords60.add("resultat");
        swedishStopwords60.add("avhandling");
        swedishStopwords60.add("myck");
        swedishStopwords60.add("rapport");
        swedishStopwords60.add("många");
        swedishStopwords60.add("kunna");
        swedishStopwords60.add("vilka");
        swedishStopwords60.add("syft");
        swedishStopwords60.add("betyd");
        swedishStopwords60.add("tre");
        swedishStopwords60.add("använda");
        swedishStopwords60.add("utan");
        swedishStopwords60.add("bland");
        swedishStopwords60.add("ger");
        swedishStopwords60.add("result");
        swedishStopwords60.add("några");
        swedishStopwords60.add("kring");
        swedishStopwords60.add("flera");
        swedishStopwords60.add("upp");
        swedishStopwords60.add("dera");
        swedishStopwords60.add("stora");
        swedishStopwords60.add("problem");
        swedishStopwords60.add("dock");
        swedishStopwords60.add("områd");
        swedishStopwords60.add("fram");
        swedishStopwords60.add("sen");
        swedishStopwords60.add("varit");
        swedishStopwords60.add("därför");
        swedishStopwords60.add("förstå");
        swedishStopwords60.add("gäll");
        swedishStopwords60.add("visade");
        swedishStopwords60.add("fråg");
        swedishStopwords60.add("större");
        swedishStopwords60.add("dess");
        swedishStopwords60.add("ofta");
        swedishStopwords60.add("viktig");
        swedishStopwords60.add("allt");
        swedishStopwords60.add("ann");
        swedishStopwords60.add("behöv");
        swedishStopwords60.add("något");
        swedishStopwords60.add("samma");
        swedishStopwords60.add("göra");
        swedishStopwords60.add("mås");
        swedishStopwords60.add("kunde");
        swedishStopwords60.add("vissa");
        swedishStopwords60.add("eftersom");
        swedishStopwords60.add("dem");
        swedishStopwords60.add("dels");
        swedishStopwords60.add("bättre");
        swedishStopwords60.add("idag");
        swedishStopwords60.add("vill");
        swedishStopwords60.add("dessutom");
        swedishStopwords60.add("medan");
        swedishStopwords60.add("sitt");
        swedishStopwords60.add("utgör");
        swedishStopwords60.add("fyra");
        swedishStopwords60.add("någon");
        swedishStopwords60.add("bli");
        swedishStopwords60.add("ing");
        swedishStopwords60.add("skall");
        swedishStopwords60.add("därmed");
        swedishStopwords60.add("syf");
        swedishStopwords60.add("varje");
        swedishStopwords60.add("annan");
        swedishStopwords60.add("artikeln");
        swedishStopwords60.add("via");
        swedishStopwords60.add("följ");
        swedishStopwords60.add("ser");
        swedishStopwords60.add("bakgrund");
        swedishStopwords60.add("båda");
        swedishStopwords60.add("fem");
        swedishStopwords60.add("frågan");
        swedishStopwords60.add("helt");
        swedishStopwords60.add("blev");
        swedishStopwords60.add("sådana");
        swedishStopwords60.add("gjort");
        swedishStopwords60.add("slutsats");
        swedishStopwords60.add("dvs");
        swedishStopwords60.add("ses");
        swedishStopwords60.add("fått");
        swedishStopwords60.add("rör");
        swedishStopwords60.add("däremot");
        swedishStopwords60.add("ingår");
        swedishStopwords60.add("ges");
        swedishStopwords60.add("sett");
        swedishStopwords60.add("säga");
        swedishStopwords60.add("iställ");
        swedishStopwords60.add("års");
        swedishStopwords60.add("liksom");
        swedishStopwords60.add("står");
        swedishStopwords60.add("blivit");
        swedishStopwords60.add("sådan");
        swedishStopwords60.add("iii");

    }

    public boolean isStopword(String s) {

        return swedishStopwords60.contains(s);


    }





}
