package com.dhavalmotghare.wikires;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dhavalmotghare.wikires.Util.NetworkUtil;
import com.dhavalmotghare.wikires.model.SearchItem;
import com.dhavalmotghare.wikires.ui.SearchListAdapter;
import com.dhavalmotghare.wikires.wikiapi.WikiApiRequest;
import com.dhavalmotghare.wikires.wikiapi.WikiResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView mSearchResults;
    private SearchListAdapter mSearchListAdapter;
    private GridLayoutManager mLayoutManager;

    protected EditText mSearchField;
    protected SearchTask mSearchTask;
    protected List<SearchItem> mSearchItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mSearchItems = new ArrayList<>();

        mSearchListAdapter = new SearchListAdapter(this, mSearchItems);
        mSearchResults = (RecyclerView) findViewById(R.id.rv_search_results);
        mSearchField = (EditText) findViewById(R.id.search_query);

        mSearchResults.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, getSpanCount());
        mSearchResults.setLayoutManager(mLayoutManager);

        mSearchResults.setAdapter(mSearchListAdapter);
        mSearchTask = new SearchTask(mSearchField, this);

        setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SearchTask implements Runnable, WikiApiRequest.WikiAPIListener, TextWatcher {

        private static final String TAG = "Search Task";

        private static final int MINIMUM_WORD_LENGTH = 3;
        private static final long SLEEP_DURATION_MS = 300;
        private static final long TEXT_UPDATE_INTERVAL_MS = 1000 * 3;

        private static final long ERROR_MESSAGE_UPDATE_INTERVAL_MS = 1000 * 6;

        private Context mContext;
        private WikiApiRequest mWikiApiRequest;

        private String mSearchTerm;
        private EditText mSearchTextSource;

        private boolean mTaskDone;
        private boolean mTaskRunning;
        private long mLastTextUpdateTime;
        private long mLastErrorUpdateTime;

        SearchTask(EditText editText, Context context) {
            mContext = context;
            mSearchTextSource = editText;
            mSearchTextSource.addTextChangedListener(this);
            mSearchTerm = editText.getText().toString();
        }

        @Override
        public void run() {
            mTaskDone = false;
            mTaskRunning = true;
            mLastTextUpdateTime = System.currentTimeMillis();
            while (!mTaskDone) {
                if (newInput()) {
                    mLastTextUpdateTime = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(mSearchTerm)) {
                        if (mSearchTerm.length() >= MINIMUM_WORD_LENGTH) {
                            if (mWikiApiRequest != null) {
                                mWikiApiRequest.cancelRequest();
                            }
                            mWikiApiRequest = new WikiApiRequest(mContext, this);
                            mWikiApiRequest.query(mSearchTerm, 300, 50);
                        }
                    } else {
                        mSearchItems.clear();
                        updateList();
                    }
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
                String currentSearchTerm = mWikiApiRequest.getSearchTerm();
                if (!(currentSearchTerm == null ? mSearchTerm == null : currentSearchTerm.equals(mSearchTerm))) {
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
        CharSequence message = getResources().getText(id);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private int getSpanCount() {
        int columnWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_width);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        return Math.round(dpWidth / 100);
    }
}