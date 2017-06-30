package SwePub;

import java.io.Serializable;

/**
 * Created by Cristian on 2016-11-23.
 */
public class TextAndLang implements Serializable {

    private String text;
    private String lang = "unsupported";


    public TextAndLang() {}

    public TextAndLang(String title) {

        this.text = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String title) {
        this.text = title;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {

        return text + " [" + lang + "]";
    }

}
