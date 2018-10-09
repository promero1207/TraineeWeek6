package com.app.paul.newsapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * class base for new
 */
public class News implements Parcelable {
    //fields
    private String headline;
    private String section;
    private String thumbnail;
    private String body;
    private String web;
    private String newsId;
    private int isReadLater;

    //constructor
    public News(String newsId, String headline, String section, String thumbnail, String body, String web, int isReadLater) {
        this.headline = headline;
        this.section = section;
        this.thumbnail = thumbnail;
        this.body = body;
        this.web = web;
        this.newsId = newsId;
        this.isReadLater = isReadLater;
    }

    public News(String headline, String section, String thumbnail, String body, String web, int isReadLater, String newsId) {
        this.headline = headline;
        this.section = section;
        this.thumbnail = thumbnail;
        this.body = body;
        this.web = web;
        this.isReadLater = isReadLater;
        this.newsId = newsId;
    }

    //setters and getters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getBody() {
        return body;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getNewsId() {
        return newsId;
    }

    public int isReadLater() {
        return isReadLater;
    }

    public void setReadLater(int readLater) {
        isReadLater = readLater;
    }

    //parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.headline);
        dest.writeString(this.section);
        dest.writeString(this.thumbnail);
        dest.writeString(this.body);
        dest.writeString(this.web);
    }

    private News(Parcel in) {
        this.headline = in.readString();
        this.section = in.readString();
        this.thumbnail = in.readString();
        this.body = in.readString();
        this.web = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
