package com.dhavalmotghare.wikires;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dhavalmotghare.wikires.model.SearchItem;
import com.dhavalmotghare.wikires.ui.SearchListAdapter;
import com.dhavalmotghare.wikires.wikiapi.WikiApiRequest;
import com.dhavalmotghare.wikires.wikiapi.WikiResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mSearchField;
    private ListView mSearchResults;
    private ImageButton mButtonSearch;

    private SearchTask mSearchTask;
    private List<SearchItem> mSearchItems;
    private SearchListAdapter mSearchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mSearchItems = new ArrayList<>();

        mSearchListAdapter = new SearchListAdapter(this, mSearchItems);
        mSearchResults = (ListView) findViewById(R.id.search_results);
        mButtonSearch = (ImageButton) findViewById(R.id.btn_search);
        mSearchField = (EditText) findViewById(R.id.search_query);

        mButtonSearch.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (!mSearchTask.isTaskRunning()) {
            mSearchTask.start();
        }
    }

    public class SearchTask implements Runnable, WikiApiRequest.WikiAPIListener, TextWatcher {

        private static final String TAG = "Search Task";

        private static final int MINIMUM_WORD_LENGTH = 3;
        private static final long SLEEP_DURATION_MS = 300;
        private static final long TEXT_UPDATE_INTERVAL_MS = 1000 * 3;

        private Context mContext;
        private WikiApiRequest mWikiApiRequest;

        private String mSearchTerm;
        private EditText mSearchTextSource;

        private boolean mTaskDone;
        private boolean mTaskRunning;
        private long mLastTextUpdateTime;

        public SearchTask(EditText editText, Context context) {
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
                            mWikiApiRequest.query(mSearchTerm, 80, 50);
                        }
                    } else {
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

        public void setTaskDone() {
            this.mTaskDone = true;
        }

        public boolean isTaskRunning() {
            return mTaskRunning;
        }

        private boolean newInput() {
            String input = mSearchTextSource.getText().toString();

            if (!(input == null ? mSearchTerm == null : input.equals(mSearchTerm))) {
                mSearchTerm = input;
                return true;
            }
            return false;
        }

        @Override
        public void onComplete(String action, WikiResponse wikiResponse) {
            if (wikiResponse.getRequestState() == WikiResponse.State.SUCCESS &&
                    wikiResponse.getResponseObject() != null) {
                mSearchItems = SearchItem.parseSearchItems(wikiResponse.getResponseObject());
            } else {
                mSearchItems.clear();
            }
            updateList();
        }

        private void updateList() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSearchListAdapter.setSearchItems(mSearchItems);
                    mSearchListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void start() {
            if (!mTaskRunning) {
                Thread thread = new Thread(this);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            start();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
