package com.app.paul.newsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class NewsProvider extends ContentProvider {
    public static final String LOG_TAG = NewsProvider.class.getSimpleName();

    private static final int NEWS = 1;


    private NewsDbHelper mNewsHelper;

    @Override
    public boolean onCreate() {
        mNewsHelper = new NewsDbHelper(getContext());
        return false;
    }

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Uri matcher
    static {
        /*
         * The calls to addURI() go here, for all of the content URI patterns that the provider
         * should recognize. For this snippet, only the calls for table 3 are shown.
         */

        /*
         * Sets the integer value for multiple rows in table 3 to 1. Notice that no wildcard is used
         * in the path
         */
        uriMatcher.addURI("com.app.paul.newsapp", "news", NEWS);


        /*
         * Sets the code for a single row to 2. In this case, the "#" wildcard is
         * used. "content://com.example.app.provider/table3/3" matches, but
         * "content://com.example.app.provider/table3 doesn't.
         */
    }

    //Method for select on database query
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = mNewsHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match){
            case NEWS:
                cursor = database.query(NewsContract.NewsEntry.TABLE_NAME, strings, s, strings1, null, null,s1);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //Method for inserting in database
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case NEWS:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertNew(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Method called by insert
    private Uri insertNew(Uri uri, ContentValues contentValues) {

        Integer category = contentValues.getAsInteger(NewsContract.NewsEntry.COLUMN_CATEGORY);
        if (category == null) {
            throw new IllegalArgumentException("New requires a category");
        }

        String name = contentValues.getAsString(NewsContract.NewsEntry.COLUMN_NEWS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("New requires a name");
        }

        String body = contentValues.getAsString(NewsContract.NewsEntry.COLUMN_NEWS_BODY);
        if (body == null) {
            throw new IllegalArgumentException("New requires a body");
        }

        String section = contentValues.getAsString(NewsContract.NewsEntry.COLUMN_NEWS_SECTION);
        if (section == null) {
            throw new IllegalArgumentException("New requires a section");
        }

        String img = contentValues.getAsString(NewsContract.NewsEntry.COLUMN_NEWS_IMG);
        if (img == null) {
            throw new IllegalArgumentException("New requires a thumbnail");
        }

        SQLiteDatabase database = mNewsHelper.getWritableDatabase();

        long id = database.insert(NewsContract.NewsEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
      return 0;
    }

    //Method for updating data base
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case NEWS:
                s = "_ID=?";
                return updateIsReadLater(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Method called by update
    private int updateIsReadLater(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if(contentValues.containsKey(NewsContract.NewsEntry.COLUMN_IS_READ_LATER)){
            Integer isReadLater = contentValues.getAsInteger(NewsContract.NewsEntry.COLUMN_IS_READ_LATER);
            if(isReadLater == null){
                throw new IllegalArgumentException("New requires a boolean");
            }
        }

        SQLiteDatabase database = mNewsHelper.getWritableDatabase();

        int rowsUpdated = database.update(NewsContract.NewsEntry.TABLE_NAME,contentValues,s,strings);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        else {
            Toast.makeText(getContext(), "0 ROWS UPDATED", Toast.LENGTH_SHORT).show();
        }

        return  rowsUpdated;
    }

    //Method tha return uri type string
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return NewsContract.NewsEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
