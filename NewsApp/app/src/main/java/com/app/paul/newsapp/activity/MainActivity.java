package com.app.paul.newsapp.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.app.paul.newsapp.R;
import com.app.paul.newsapp.adapter.AdapterFragmentPager;
import com.app.paul.newsapp.fragments.FragmentBase;

import static com.app.paul.newsapp.adapter.AdapterFragmentPager.PATH_TO_NEWS;

/**
 * Main activity; Main screen
 */
public class MainActivity extends AppCompatActivity {
    FragmentBase fragment;
    SearchView searchView;
    private String mSearchString;

    public final static String SEARCH_KEY = "SEARCH_KEY";
    public final static String FRAG_ID = "FRAG_ID";


    /**
     * On create method, inflating view
     * @param savedInstanceState Bundle of data saved after a conf change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager mViewPagerTour = findViewById(R.id.view_pager);
        AdapterFragmentPager adapterFragmentPager = new AdapterFragmentPager(getSupportFragmentManager());
        mViewPagerTour.setAdapter(adapterFragmentPager);

        TabLayout mTabLayoutHost = findViewById(R.id.tablayout_host);
        mTabLayoutHost.setTabTextColors(ColorStateList.valueOf(Color.parseColor("white")));
        mTabLayoutHost.setupWithViewPager(mViewPagerTour);
        mTabLayoutHost.setTabGravity(TabLayout.GRAVITY_CENTER);

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    /**
     * Menu in the top of screen, for searchview
     * @param menu menu to be inflated
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);


        searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(false);

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                return true;
            }
        });

        if(fragment != null && fragment.isVisible()) {
            menuItem.expandActionView();
            if (mSearchString != null && !mSearchString.isEmpty()) {
                searchView.setQuery(mSearchString, true);
                searchView.clearFocus();
            }
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                fragment.buildString(s);
                fragment.load(fragment.getCont());
                fragment.increaseCont();
                fragment.setSearching(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

        return true;
    }


    /**
     * item selected from menu
     * @param item item in this case searchviewmenu
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                putSearchFragment();
                break;
            case R.id.read_later:
                Intent intent = new Intent(this, ReadLaterActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * method to be called to put the search fragment
     */
    public void putSearchFragment(){

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentBase();
        Bundle bundle;
        bundle = new Bundle();
        bundle.putString(PATH_TO_NEWS, "");
        fragment.setArguments(bundle);
        transaction.replace(R.id.frmae_container, fragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

    //Saving activity state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_KEY, searchView.getQuery().toString());
        if(fragment != null){
            outState.putInt(FRAG_ID, fragment.getId());
        }

        super.onSaveInstanceState(outState);

    }

    //Restoring activity state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        fragment = (FragmentBase) fm.findFragmentById(R.id.frmae_container);
        mSearchString = savedInstanceState.getString(SEARCH_KEY);
    }
}
