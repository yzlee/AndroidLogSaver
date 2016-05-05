package log;

import android.util.Log;

import java.util.List;

/**
 * Created by lee on 4/28/16.
 */
public class LogShower {

    private static List<String> keywords;
    private static Boolean showOtherBranch;

    public synchronized static void custom() {

    }

    public synchronized static void i(String tag, String clazz, String function, String branch, String message) {

        LogShower.custom(tag, clazz, function, message, branch);
    }

    public synchronized static void custom(String tag, String clazz, String function, String message, String... branch) {


        Boolean keywordsContainBranch = false;
        String showedBranch = "";
        String oriBranch = "";

        if (keywords == null) {

            for (String b : branch) {
                oriBranch = oriBranch + "(" + b + ")";
            }
            Log.i(tag, "{" + clazz + "}" + "[" + function + "]" + oriBranch + " " + message);
            return;
        }


        for (String b : branch) {
            oriBranch = oriBranch + "(" + b + ")";
            if (keywords.contains(b)) {
                keywordsContainBranch = true;
                showedBranch = showedBranch + "(" + b + ")";
            }
        }

        //如果分支中没有关键字，或者需要显示其它分支
        if (!keywordsContainBranch || showOtherBranch) {
            showedBranch = oriBranch;
        }


        if (keywords.contains(tag) || keywords.contains(clazz) || keywords.contains(function) || keywordsContainBranch) {
            Log.i(tag, "{" + clazz + "}" + "[" + function + "]" + showedBranch + " "  + message);
        }

    }

    public synchronized void setKeywords(String... keywords) {
        setKeywords(false, keywords);
    }

    public synchronized void setKeywords(Boolean showOtherKeywords, String... keywords) {
        LogShower.keywords = java.util.Arrays.asList(keywords);
        LogShower.showOtherBranch = showOtherKeywords;
    }

}
