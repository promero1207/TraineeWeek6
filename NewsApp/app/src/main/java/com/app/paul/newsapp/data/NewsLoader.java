package com.app.paul.newsapp.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.app.paul.newsapp.News;

import java.util.List;

/**
 * News asynctask loader
 */
public class NewsLoader extends AsyncTaskLoader<List<News>>{

    private String mUrl;

    /**
     * constructor
     * @param context context
     * @param url string containing url path
     */
    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    /**
     * mothod to load in background
     * @return query list of news
     */
    @Nullable
    @Override
    public List<News> loadInBackground() {
        if(mUrl != null){
            return Query.getNewsData(mUrl);
        }
        return null;
    }

    /**
     * starting the load of data
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
