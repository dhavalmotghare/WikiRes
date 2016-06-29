package com.dhavalmotghare.wikires;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.EditText;
import android.widget.Toast;

import com.dhavalmotghare.wikires.utility.NetworkUtil;
import com.dhavalmotghare.wikires.model.SearchItem;
import com.dhavalmotghare.wikires.ui.SearchListAdapter;
import com.dhavalmotghare.wikires.wikiapi.WikiApiRequest;
import com.dhavalmotghare.wikires.wikiapi.WikiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity class
 */
public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG_DATA = "data";

    private RecyclerView mSearchResults;
    private GridLayoutManager mLayoutManager;
    private SearchListAdapter mSearchListAdapter;

    protected EditText mSearchField;
    protected SearchTask mSearchTask;
    protected List<SearchItem> mSearchItems;

    private DataFragment mDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mDataFragment = (DataFragment) fragmentManager.findFragmentByTag(TAG_DATA);

        mSearchField = (EditText) findViewById(R.id.search_query);
        mSearchResults = (RecyclerView) findViewById(R.id.rv_search_results);

        mSearchResults.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, getSpanCount());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchResults.setLayoutManager(mLayoutManager);

        if (mDataFragment == null) {
            mDataFragment = new DataFragment();
            fragmentManager.beginTransaction().add(mDataFragment, TAG_DATA).commit();
            mSearchItems = new ArrayList<>();
            mSearchTask = new SearchTask(mSearchField, this, "");
        } else {
            mSearchItems = mDataFragment.getSearchItems();
            mSearchTask = new SearchTask(mSearchField, this, mDataFragment.getSearchTerm());
        }

        mSearchListAdapter = new SearchListAdapter(this, mSearchItems);
        mSearchResults.setAdapter(mSearchListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataFragment.setSearchItems(mSearchItems);
        mDataFragment.setSearchTerm(mSearchField != null ? mSearchField.getText().toString() : "");
    }

    /**
     * Auto search task. This task would start searching for the images as the user keeps entering
     */
    class SearchTask implements Runnable, WikiApiRequest.WikiAPIListener, TextWatcher {

        private static final String TAG = "Search Task";

        private static final int MINIMUM_WORD_LENGTH = 3;

        private static final long SLEEP_DURATION_MS = 300;
        private static final long TEXT_UPDATE_INTERVAL_MS = 1000 * 3;

        private static final long ERROR_MESSAGE_UPDATE_INTERVAL_MS = 1000 * 6;

        private Context mContext;
        private WikiApiRequest mWikiApiRequest;

        private String mSearchTerm;
        private String mLastSearchedTerm;
        private EditText mSearchTextSource;

        private boolean mTaskDone;
        private boolean mTaskRunning;
        private long mLastTextUpdateTime;
        private long mLastErrorUpdateTime;

        SearchTask(EditText editText, Context context, String lastSearchedTerm) {
            mContext = context;
            mSearchTextSource = editText;
            mLastSearchedTerm = lastSearchedTerm;
            mSearchTextSource.addTextChangedListener(this);
            mSearchTerm = editText.getText().toString();
        }

        @Override
        public void run() {
            mTaskDone = false;
            mTaskRunning = true;
            mLastTextUpdateTime = System.currentTimeMillis();
            while (!mTaskDone) {
                if (newInput()) { // new input received lets clear out old and make new search request
                    mLastTextUpdateTime = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(mSearchTerm)) {
                        if (mSearchTerm.length() >= MINIMUM_WORD_LENGTH) {
                            if (mWikiApiRequest != null) {
                                mWikiApiRequest.cancelRequest();
                            }
                            mWikiApiRequest = new WikiApiRequest(mContext, this);
                            mWikiApiRequest.query(mSearchTerm, 800, 50);
                        }
                    } else {
                        mSearchItems.clear();
                        updateList();
                    }
                    mLastSearchedTerm = mSearchTerm;
                } else {
                    if (TextUtils.isEmpty(mSearchTerm)) {
                        mSearchItems.clear();
                        updateList();
                    }
                }

                try {
                    Thread.sleep(SLEEP_DURATION_MS);
                } catch (InterruptedException ie) {
                    Log.i(TAG, "Thread interrupted");
                }
                // the user hasn't entered anything for a few seconds, lets stop the thread
                if (System.currentTimeMillis() - mLastTextUpdateTime > TEXT_UPDATE_INTERVAL_MS) {
                    setTaskDone();
                }
            }
            mTaskRunning = false;
        }

        void setTaskDone() {
            this.mTaskDone = true;
        }

        private boolean newInput() {
            mSearchTerm = mSearchTextSource.getText().toString();

            if (mWikiApiRequest != null) {
                if (!(mLastSearchedTerm == null ? mSearchTerm == null : mLastSearchedTerm.equals(mSearchTerm))) {
                    return true;
                }
                return false;
            }
            return true;
        }

        @Override
        public void onComplete(String action, WikiResponse wikiResponse) {
            if (wikiResponse.getRequestState() == WikiResponse.State.SUCCESS) {
                if (wikiResponse.getResponseObject() != null)
                    mSearchItems = SearchItem.parseSearchItems(wikiResponse.getResponseObject());
                else
                    mSearchItems.clear();
            } else if (wikiResponse.getRequestState() == WikiResponse.State.NETWORK_ERROR) {
                showErrorToast(R.string.error_network);
            } else {
                showErrorToast(R.string.error_unable_to_fetch);
            }
            updateList();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        private void start() {
            if (!mTaskRunning) {
                Thread thread = new Thread(this);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (NetworkUtil.isNetworkAvailable(mContext)) {
                start();
            } else {
                if (System.currentTimeMillis() - mLastErrorUpdateTime > ERROR_MESSAGE_UPDATE_INTERVAL_MS) {
                    mLastErrorUpdateTime = System.currentTimeMillis();
                    showErrorToast(R.string.error_network);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    protected void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchListAdapter.setSearchItems(mSearchItems);
                mSearchListAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void showErrorToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();
    }

    private int getSpanCount() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        return Math.round(dpWidth / 100);
    }
}