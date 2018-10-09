package com.app.paul.newsapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NewsContract  {
    private NewsContract(){}

    private static final String CONTENT_AUTHORITY = "com.app.paul.newsapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_NEWS = "news";


    public static class NewsEntry implements BaseColumns{
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);
        static final String TABLE_NAME = "news";
        public static final String _ID = "_ID";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_NEWS_NAME = "title";
        public static final String COLUMN_NEWS_BODY = "body";
        public static final String COLUMN_NEWS_SECTION = "section";
        public static final String COLUMN_NEWS_IMG = "img";
        public static final String COLUMN_IS_READ_LATER = "read_later";
    }


}
