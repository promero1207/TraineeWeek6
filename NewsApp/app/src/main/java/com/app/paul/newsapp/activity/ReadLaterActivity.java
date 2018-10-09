package com.app.paul.newsapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;
import com.app.paul.newsapp.adapter.AdapterReadLater;
import com.app.paul.newsapp.data.NewsContract;

import java.util.ArrayList;
import java.util.List;

import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_BODY;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_IMG;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_TITLE;
import static com.app.paul.newsapp.fragments.FragmentBase.SHOW_WEB;

/**
 * Activity for display items selected for read later
 */
public class ReadLaterActivity extends AppCompatActivity implements AdapterReadLater.OnItemClickListener, AdapterReadLater.OnItemDeleteListener {
    //Fields
    private List<News> listReadLater;
    private AdapterReadLater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later);

        listReadLater = new ArrayList<>();
        RecyclerView recyclerViewReadLater = findViewById(R.id.recycler_read_later);
        adapter = new AdapterReadLater(listReadLater, this ,this);
        recyclerViewReadLater.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReadLater.setLayoutManager(layoutManager);
        getNewsDataBase();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onItemClick(Integer position) {
        Intent intent = new Intent(this, ShowNewActivity.class);
        intent.putExtra(SHOW_TITLE, listReadLater.get(position).getHeadline());
        intent.putExtra(SHOW_BODY, listReadLater.get(position).getBody());
        intent.putExtra(SHOW_IMG, listReadLater.get(position).getThumbnail());
        intent.putExtra(SHOW_WEB, listReadLater.get(position).getWeb());
        startActivity(intent);
    }

    /**
     * Method for getting database info, and fill list
     */
    private void getNewsDataBase() {
        String s = NewsContract.NewsEntry.COLUMN_IS_READ_LATER + "=?";
        String[] args = {String.valueOf(1)};
        String[] projection = {
                NewsContract.NewsEntry._ID,
                NewsContract.NewsEntry.COLUMN_CATEGORY,
                NewsContract.NewsEntry.COLUMN_NEWS_NAME,
                NewsContract.NewsEntry.COLUMN_NEWS_BODY,
                NewsContract.NewsEntry.COLUMN_NEWS_SECTION,
                NewsContract.NewsEntry.COLUMN_NEWS_IMG,
                NewsContract.NewsEntry.COLUMN_IS_READ_LATER};


        Cursor cursor = getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                s,                   // Selection criteria
                args,                   // Selection criteria
                null);

        int idColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry._ID);
        int categoryColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_CATEGORY);
        int nameColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_NAME);
        int bodyColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_BODY);
        int sectionColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_SECTION);
        int imgColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_IMG);
        int isReadLaterColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_IS_READ_LATER);

        try {
            listReadLater.clear();
            while (cursor.moveToNext()) {
                String id = cursor.getString(idColumnIndex);
                int category = cursor.getInt(categoryColumnIndex);
                String title = cursor.getString(nameColumnIndex);
                String body = cursor.getString(bodyColumnIndex);
                String section = cursor.getString(sectionColumnIndex);
                String img = cursor.getString(imgColumnIndex);
                int isReadLater = cursor.getInt(isReadLaterColumnIndex);
                listReadLater.add(new News(category, title, section, img, body, "", isReadLater, id));
            }
            adapter.notifyDataSetChanged();
        }
        finally {
            cursor.close();
        }

    }

    /**
     * Method called when an item is deleted, it updates databasee to
     * @param id id to be udated in DB
     */
    @Override
    public void onItemDeleteClick(String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_IS_READ_LATER, 0);
        String [] args = {id};
        getContentResolver().update(NewsContract.NewsEntry.CONTENT_URI, contentValues, null, args);

    }

    //method for back arrow pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
