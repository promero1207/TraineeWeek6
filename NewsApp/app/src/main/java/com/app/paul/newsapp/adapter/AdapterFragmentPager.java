package com.app.paul.newsapp.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.paul.newsapp.fragments.FragmentBase;


/**
 * Adapter for fragments in the tabs
 */
public class AdapterFragmentPager extends FragmentStatePagerAdapter {
    public static final String PATH_TO_NEWS = "PATH_TO_NEWS";

    public AdapterFragmentPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        String path;
        Bundle bundle;
        switch(position){
            case 0:
                path = "economy";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase economyFragment = new FragmentBase();
                economyFragment.setArguments(bundle);
                return economyFragment;
            case 1:
                path = "politics";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase politics = new FragmentBase();
                politics.setArguments(bundle);
                return politics;
            case 2:
                path = "sports";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase sports = new FragmentBase();
                sports.setArguments(bundle);
                return sports;
            case 3:
                path = "technology";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase technology = new FragmentBase();
                technology.setArguments(bundle);
                return technology;
            case 4:
                path = "science";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase science = new FragmentBase();
                science.setArguments(bundle);
                return science;
            case 5:
                path = "music";
                bundle = new Bundle();
                bundle.putString(PATH_TO_NEWS, path);
                FragmentBase music = new FragmentBase();
                music.setArguments(bundle);
                return music;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }

    /**
     * return page title
     * @param position position
     * @return string
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ECONOMY";
            case 1:
                return "POLITICS";
            case 2:
                return "SPORTS";
            case 3:
                return "TECHNOLOGY";
            case 4:
                return "SCIENCE";
            case 5:
                return "MUSIC";
        }
        return "";
    }
}
