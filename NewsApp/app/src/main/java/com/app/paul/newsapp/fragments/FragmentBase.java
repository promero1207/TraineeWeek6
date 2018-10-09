package com.app.paul.newsapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;
import com.app.paul.newsapp.activity.ShowNewActivity;
import com.app.paul.newsapp.adapter.AdapterRvMainNews;
import com.app.paul.newsapp.data.NewsContract;
import com.app.paul.newsapp.data.NewsLoader;

import java.util.ArrayList;
import java.util.List;

import static com.app.paul.newsapp.adapter.AdapterFragmentPager.PATH_TO_NEWS;

public class FragmentBase extends Fragment implements AdapterRvMainNews.OnItemClickListener, AdapterRvMainNews.OnItemReadLaterClickListener,LoaderManager.LoaderCallbacks  {

    //constants
    public static final String SHOW_TITLE = "SHOW_TITLE";
    public static final String SHOW_BODY = "SHOW_BODY";
    public static final String SHOW_IMG = "SHOW_IMG";
    public static final String SHOW_WEB = "SHOW_WEB";
    public static final String SCROLL = "SCROLL";
    public static final String ID = "ID";
    public static final int DATABASE_LOADER_ID = -1;

    //fields
    private List<News> newsList = new ArrayList<>();
    protected String path;
    protected RecyclerView recyclerNews;
    protected AdapterRvMainNews adapter;
    private ProgressBar progress;
    private Boolean isScrolling = false;
    private int currentItems;
    private int totalItems;
    private int scrollItems;
    private int cont = 1;
    private int cont2 = 1;
    private boolean isSearching = false;
    private boolean isNewData = false;
    private boolean internetConnection = false;
    private ImageView wifi;
    private String initialQuery = "";


    //constructor
    public FragmentBase() {
    }

    /**
     * Method for creating view
     * @param inflater inflater
     * @param container container of the view
     * @param savedInstanceState Bundle when conf has changed
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle fragmentBundle = getArguments();

        if(fragmentBundle != null) {
            initialQuery = fragmentBundle.getString(PATH_TO_NEWS);
        }


        View v = inflater.inflate(R.layout.fragment_base, container, false);

        wifi = v.findViewById(R.id.wifi);

        progress = v.findViewById(R.id.progress_loading);
        recyclerNews = v.findViewById(R.id.recycler_main_mews);
        adapter = new AdapterRvMainNews(newsList, this, this);
        recyclerNews.setAdapter(adapter);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerNews.setLayoutManager(manager);

        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            //recycler view method to check when reached last item
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollItems == totalItems) && !isSearching){
                    isScrolling = false;

                    path = path.replace(String.valueOf("page=" + cont2),String.valueOf("page="+(cont2+1)));
                    cont2++;
                    increaseCont();
                    path = path.replace("size=30","size=5");
                    recyclerNews.post(new Runnable() {
                        @Override
                        public void run() {
                            isNewData = true;
                            load(cont);
                        }
                    });
                }
            }
        });

        buildString(initialQuery);

        if(savedInstanceState == null){
            load(0);
        }
        else {
            isSearching = true;
            load(savedInstanceState.getInt(ID));
            cont++;

        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!internetConnection) {
            getLoaderManager().restartLoader(DATABASE_LOADER_ID, null, this);
        }
    }

    /**
     * Loader method when creating loader
     * @param id id to diference between calls
     * @param args arguments extras
     * @return Loader
     */
    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if(id != DATABASE_LOADER_ID) {
            if(getContext() != null) {
                return new NewsLoader(getContext(), path);
            }
        }
        return getNewsDataBase();
    }

    /**
     * Method called whe load of data finishes
     * @param loader load
     * @param data data returned of loading
     */
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.INVISIBLE);

        if(data != null) {
            int loaderId = loader.getId();
            switch (loaderId) {
                case DATABASE_LOADER_ID:
                    Cursor cursor = (Cursor) data;
                    loadDataBase(cursor);
                    break;
                default:
                    List<News> news = (List<News>) data;
                    loadApiData(news);
                    break;
            }
        }
    }


    /**
     * method called when reset on load is made
     * @param loader loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        newsList.clear();
        progress.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();

    }

    /**
     * method for saving state
     * @param outState bundle outstate
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(recyclerNews.getLayoutManager() != null) {
            //saving scroll of recycler
            outState.putParcelable(SCROLL, recyclerNews.getLayoutManager().onSaveInstanceState());
        }
        outState.putInt(ID,getCont() -1);
        super.onSaveInstanceState(outState);
    }


    /**
     * restoring instance
     * @param savedInstanceState bundle of restored state
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * method for calling loader
     * @param id id of the loader call, it must be diferent on each call for new data
     */
    public void load(int id){
        ConnectivityManager connMgr;
        if (getContext() != null) {
            connMgr = (ConnectivityManager)
                    getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                LoaderManager loaderManager;
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    loaderManager = getLoaderManager();

                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.initLoader(id, null, this);
                    internetConnection = true;
                }
                else {
                    loaderManager = getLoaderManager();
                    progress.setVisibility(View.GONE);
                    loaderManager.initLoader(DATABASE_LOADER_ID, null, this);
                    internetConnection =false;
                }
            }

        }

    }

    /**
     * method for building query
     * @param query query string to be searched
     */
    public void buildString(String query){
        path = "https://content.guardianapis.com/search?&show-fields=bodyText%2Cheadline%2Csection%2Cthumbnail&page=1&page-size=30&q="+
                query +"&api-key=819465fe-ccca-48b5-a3ca-af834bf6741e";
    }

    //setter and getters
    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    public void increaseCont() {
        cont++;
    }

    public int getCont(){
        return cont;
    }

    private int getCategory(String query){
        switch (query){
            case "economy":
                return 1;
            case "politics":
                return 2;
            case "sports":
                return 3;
            case "science":
                return 4;
            case "music":
                return 5;
        }
        return 0;
    }

    /**
     * Metho when clicked the read later item
     * @param position adapter position
     * @param isReadLater boolean if the item is set for read later
     */
    @Override
    public void onItemReadLaterClick(Integer position, int isReadLater) {
        update(position, isReadLater);
    }

    private void update(int position, int isReadLater) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_IS_READ_LATER, isReadLater);
        String [] args = {newsList.get(position).getNewsId()};
        getContext().getContentResolver().update(NewsContract.NewsEntry.CONTENT_URI, contentValues, null, args);
    }

    //Method for on click event of recyclerView
    @Override
    public void onItemClick(Integer position) {
        Intent intent = new Intent(getContext(), ShowNewActivity.class);
        intent.putExtra(SHOW_TITLE, newsList.get(position).getHeadline());
        intent.putExtra(SHOW_BODY, newsList.get(position).getBody());
        intent.putExtra(SHOW_IMG, newsList.get(position).getThumbnail());
        intent.putExtra(SHOW_WEB, newsList.get(position).getWeb());
        startActivity(intent);
    }

    /**
     * Method for getting database query
     * @return Cursor Loader
     */
    private CursorLoader getNewsDataBase() {

        String s = NewsContract.NewsEntry.COLUMN_CATEGORY + "=?";
        String[] projection = {
                NewsContract.NewsEntry._ID,
                NewsContract.NewsEntry.COLUMN_CATEGORY,
                NewsContract.NewsEntry.COLUMN_NEWS_NAME,
                NewsContract.NewsEntry.COLUMN_NEWS_BODY,
                NewsContract.NewsEntry.COLUMN_NEWS_SECTION,
                NewsContract.NewsEntry.COLUMN_NEWS_IMG,
                NewsContract.NewsEntry.COLUMN_IS_READ_LATER};

        String[] selectionArgs = {String.valueOf(getCategory(initialQuery))};
        return new CursorLoader(getContext(),
                NewsContract.NewsEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                s,                   // Selection criteria
                selectionArgs,                   // Selection criteria
                null);



    }

    /**
     * Method for inserting in data base
     * @param data
     */
    private void insertDataBase(List<News> data) {
        for (News news : data) {
            String newsId = news.getNewsId();
            int category = getCategory(initialQuery);
            String title = news.getHeadline();
            String body = news.getBody();
            String section = news.getSection();
            String img = news.getThumbnail();
            int isReadLater = news.isReadLater();

            ContentValues values = new ContentValues();
            values.put(NewsContract.NewsEntry._ID, newsId);
            values.put(NewsContract.NewsEntry.COLUMN_CATEGORY, category);
            values.put(NewsContract.NewsEntry.COLUMN_NEWS_NAME, title);
            values.put(NewsContract.NewsEntry.COLUMN_NEWS_BODY, body);
            values.put(NewsContract.NewsEntry.COLUMN_NEWS_SECTION, section);
            values.put(NewsContract.NewsEntry.COLUMN_NEWS_IMG, img);
            values.put(NewsContract.NewsEntry.COLUMN_IS_READ_LATER, isReadLater);

            Uri newUri = getContext().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), "FAILED INSERTING DATA ON DB", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method for filling the list with database info
     * @param cursor cursor from database
     */
    private void loadDataBase(Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_NAME);
        int bodyColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_BODY);
        int sectionColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_SECTION);
        int imgColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_IMG);
        int isReadLaterColumnIndex = cursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_IS_READ_LATER);

        try {
            while (cursor.moveToNext()) {
                String id = cursor.getString(idColumnIndex);
                String title = cursor.getString(nameColumnIndex);
                String body = cursor.getString(bodyColumnIndex);
                String section = cursor.getString(sectionColumnIndex);
                String img = cursor.getString(imgColumnIndex);
                int isReadLater = cursor.getInt(isReadLaterColumnIndex);
                newsList.add(new News(title, section, img, body, "", isReadLater, id));
            }
            adapter.notifyDataSetChanged();
        }
        finally {
            cursor.close();
        }

    }

    /**
     * Method for loading in list data returned from API
     * @param news list used by the adapter
     */
    private void loadApiData(List<News> news) {
        wifi.setVisibility(View.GONE);
        if (newsList.isEmpty()) {
            newsList.addAll(news);
            insertDataBase(news);
        }
        if (isNewData) {
            newsList.addAll(news);
            isNewData = false;
            //insertDataBase(data);
        }
        if (isSearching) {
            newsList.clear();
            newsList.addAll(news);
            isSearching = false;
        }

        adapter.notifyDataSetChanged();
    }
}
