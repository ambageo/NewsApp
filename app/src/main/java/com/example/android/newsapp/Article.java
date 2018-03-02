package com.example.android.newsapp;

import static android.R.attr.author;

/**
 * Created by georgeampartzidis on 16/7/17.
 */

public class Article {
    private String aTitle;
    private String aSection;
    private String aDate;
    private String aUrl;

    /**
     *
     * @param title     refers to the article title.
     * @param section   refers to the name of the section the article belongs to.
     * @param date      refers to the date of the article.
     * @param url       refers to the url of the article.
     */
    public Article(String title, String section, String date, String url){
        aTitle= title;
        aSection= section;
        aDate= date;
        aUrl= url;
    }

    public String getnTitle() {
        return aTitle;
    }

    public String getnSection() {
        return aSection;
    }

    public String getnDate() {
        return aDate;
    }

    public String getnUrl() {
        return aUrl;
    }
}
