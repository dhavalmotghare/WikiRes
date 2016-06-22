package com.dhavalmotghare.wikires;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dhavalmotghare.wikires.model.SearchItem;

import java.util.List;

/**
 * Fragment used to retain data during configuration changes
 */
public class DataFragment extends Fragment {

    private String mSearchTerm;
    protected List<SearchItem> mSearchItems;

    public DataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String getSearchTerm() {
        return mSearchTerm;
    }

    public void setSearchTerm(String mSearchTerm) {
        this.mSearchTerm = mSearchTerm;
    }

    public List<SearchItem> getSearchItems() {
        return mSearchItems;
    }

    public void setSearchItems(List<SearchItem> mSearchItems) {
        this.mSearchItems = mSearchItems;
    }
}