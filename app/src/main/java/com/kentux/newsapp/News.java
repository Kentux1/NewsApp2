package com.kentux.newsapp;

final class News {
    private String mNewsTitle;
    private String mNewsPublicationDate;
    private String mNewsCategory;
    private String mUrl;

    News(String newsTitle, String newsPublicationDate, String newsCategory, String url) {
        mNewsTitle = newsTitle;
        mNewsPublicationDate = newsPublicationDate;
        mNewsCategory = newsCategory;
        mUrl = url;
    }

    String getNewsTitle() {
        return mNewsTitle;
    }
    String getNewsPublicationDate() {
        return mNewsPublicationDate;
    }
    String getNewsCategory() {
        return mNewsCategory;
    }
    String getUrl() {
        return mUrl;
    }
}
