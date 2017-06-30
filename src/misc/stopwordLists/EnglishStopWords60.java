package misc.stopwordLists;

import java.util.HashSet;

/**
 * Created by Cristian on 2016-10-28.
 */
public class EnglishStopWords60 implements Stopwordlist {

    private static HashSet<String> englishStopwords60 = new HashSet<>(100);

    static {

        //top 60 general
        englishStopwords60.add("the");
        englishStopwords60.add("be");
        englishStopwords60.add("to");
        englishStopwords60.add("of");
        englishStopwords60.add("and");
        englishStopwords60.add("a");
        englishStopwords60.add("in");
        englishStopwords60.add("that");
        englishStopwords60.add("have");
        englishStopwords60.add("I");
        englishStopwords60.add("it");
        englishStopwords60.add("for");
        englishStopwords60.add("not");
        englishStopwords60.add("on");
        englishStopwords60.add("with");
        englishStopwords60.add("he");
        englishStopwords60.add("as");
        englishStopwords60.add("you");
        englishStopwords60.add("do");
        englishStopwords60.add("at");
        englishStopwords60.add("this");
        englishStopwords60.add("but");
        englishStopwords60.add("his");
        englishStopwords60.add("by");
        englishStopwords60.add("from");
        englishStopwords60.add("they");
        englishStopwords60.add("we");
        englishStopwords60.add("say");
        englishStopwords60.add("her");
        englishStopwords60.add("she");
        englishStopwords60.add("or");
        englishStopwords60.add("an");
        englishStopwords60.add("will");
        englishStopwords60.add("my");
        englishStopwords60.add("one");
        englishStopwords60.add("all");
        englishStopwords60.add("would");
        englishStopwords60.add("there");
        englishStopwords60.add("their");
        englishStopwords60.add("what");
        englishStopwords60.add("so");
        englishStopwords60.add("up");
        englishStopwords60.add("out");
        englishStopwords60.add("if");
        englishStopwords60.add("about");
        englishStopwords60.add("who");
        englishStopwords60.add("get");
        englishStopwords60.add("which");
        englishStopwords60.add("go");
        englishStopwords60.add("me");
        englishStopwords60.add("when");
        englishStopwords60.add("make");
        englishStopwords60.add("can");
        englishStopwords60.add("like");
        englishStopwords60.add("time");
        englishStopwords60.add("no");
        englishStopwords60.add("just");
        englishStopwords60.add("him");
        englishStopwords60.add("know");
        englishStopwords60.add("take");

        //some very common terms based on SwePub (pre-stemmed)

        englishStopwords60.add("was");
        englishStopwords60.add("are");
        englishStopwords60.add("use");
        englishStopwords60.add("were");
        englishStopwords60.add("study");
        englishStopwords60.add("result");
        englishStopwords60.add("model");
        englishStopwords60.add("between");
        englishStopwords60.add("base");
        englishStopwords60.add("method");
        englishStopwords60.add("increase");
        englishStopwords60.add("has");
        englishStopwords60.add("these");
        englishStopwords60.add("effect");
        englishStopwords60.add("also");
        englishStopwords60.add("been");
        englishStopwords60.add("different");
        englishStopwords60.add("two");
        englishStopwords60.add("show");
        englishStopwords60.add("data");
        englishStopwords60.add("more");
        englishStopwords60.add("high");
        englishStopwords60.add("present");
        englishStopwords60.add("analysis");
        englishStopwords60.add("both");
        englishStopwords60.add("during");
        englishStopwords60.add("than");
        englishStopwords60.add("paper");
        englishStopwords60.add("well");
        englishStopwords60.add("change");
        englishStopwords60.add("after");
        englishStopwords60.add("year");
        englishStopwords60.add("other");
        englishStopwords60.add("new");
        englishStopwords60.add("such");
        englishStopwords60.add("test");
        englishStopwords60.add("how");
        englishStopwords60.add("design");
        englishStopwords60.add("compare");
        englishStopwords60.add("found");
        englishStopwords60.add("may");
        englishStopwords60.add("research");
        englishStopwords60.add("type");
        englishStopwords60.add("approach");
        englishStopwords60.add("low");
        englishStopwords60.add("develop");
        englishStopwords60.add("three");
        englishStopwords60.add("into");
        englishStopwords60.add("however");
        englishStopwords60.add("include");
        englishStopwords60.add("provide");
        englishStopwords60.add("first");
        englishStopwords60.add("most");
        englishStopwords60.add("aim");
        englishStopwords60.add("important");
        englishStopwords60.add("suggest");
        englishStopwords60.add("problem");
        englishStopwords60.add("had");
        englishStopwords60.add("our");
        englishStopwords60.add("find");
        englishStopwords60.add("number");
        englishStopwords60.add("need");
        englishStopwords60.add("non");
        englishStopwords60.add("only");
        englishStopwords60.add("within");
        englishStopwords60.add("through");
        englishStopwords60.add("some");
        englishStopwords60.add("mean");
        englishStopwords60.add("report");
        englishStopwords60.add("over");
        englishStopwords60.add("could");
        englishStopwords60.add("observe");
        englishStopwords60.add("while");
        englishStopwords60.add("affect");
        englishStopwords60.add("thesis");
        englishStopwords60.add("due");
        englishStopwords60.add("obtain");
        englishStopwords60.add("discuss");
        englishStopwords60.add("way");
        englishStopwords60.add("each");
        englishStopwords60.add("shown");
        englishStopwords60.add("many");
        englishStopwords60.add("under");
        englishStopwords60.add("general");
        englishStopwords60.add("same");
        englishStopwords60.add("four");
        englishStopwords60.add("those");
        englishStopwords60.add("less");
        englishStopwords60.add("thus");
        englishStopwords60.add("should");
        englishStopwords60.add("without");
        englishStopwords60.add("very");
        englishStopwords60.add("analyze");
        englishStopwords60.add("target");
        englishStopwords60.add("objective");
        englishStopwords60.add("regard");
        englishStopwords60.add("second");
        englishStopwords60.add("reveal");
        englishStopwords60.add("background");
        englishStopwords60.add("various");
        englishStopwords60.add("aspect");
        englishStopwords60.add("purpose");
        englishStopwords60.add("issue");
        englishStopwords60.add("toward");
        englishStopwords60.add("article");
        englishStopwords60.add("focus");
        englishStopwords60.add("respectively");
        englishStopwords60.add("often");
        englishStopwords60.add("here");
        englishStopwords60.add("question");
        englishStopwords60.add("made");
        englishStopwords60.add("therefore");
        englishStopwords60.add("allow");
        englishStopwords60.add("known");
        englishStopwords60.add("since");
        englishStopwords60.add("did");
        englishStopwords60.add("whether");
        englishStopwords60.add("given");
        englishStopwords60.add("give");
        englishStopwords60.add("before");
        englishStopwords60.add("relative");
        englishStopwords60.add("then");
        englishStopwords60.add("them");
        englishStopwords60.add("against");
        englishStopwords60.add("available");
        englishStopwords60.add("highly");
        englishStopwords60.add("five");





    }


    public boolean isStopword(String s) {

        return englishStopwords60.contains(s);
    }
}
