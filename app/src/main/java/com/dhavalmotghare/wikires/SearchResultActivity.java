package com.dhavalmotghare.wikires;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dhavalmotghare.wikires.model.SearchItem;
import com.dhavalmotghare.wikires.ui.SearchListAdapter;
import com.dhavalmotghare.wikires.wikiapi.WikiApi;
import com.dhavalmotghare.wikires.wikiapi.WikiResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mSearchResults;
    private ImageButton mButtonSearch;
    private EditText mSearchField;

    private List<SearchItem> mSearchItems;
    private SearchListAdapter mSearchListAdapter;

    private SearchTask mSearchTask;

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (!mSearchTask.isTaskRunning()) {
            new Thread(mSearchTask).start();
        }
    }

    public class SearchTask implements Runnable, WikiApi.WikiAPIListener {

        private static final String TAG = "Search Task";

        private static final int MINIMUM_WORD_LENGTH = 3;
        private static final long SLEEP_DURATION_MS = 300;

        private WikiApi mWikiApi;
        private Context mContext;

        private String mSearchTerm;
        private EditText mSearchTextSource;

        private boolean mTaskDone;
        private boolean mTaskRunning;

        public SearchTask(EditText editText, Context context) {
            mContext = context;
            mSearchTextSource = editText;
            mSearchTerm = editText.getText().toString();
            mWikiApi = new WikiApi(mContext, this);
        }

        @Override
        public void run() {
            mTaskRunning = true;
            while (!mTaskDone) {
                if (newInput()) {
                    if (!TextUtils.isEmpty(mSearchTerm)) {
                        if (mSearchTerm.length() >= MINIMUM_WORD_LENGTH) {
                            mWikiApi.query(mSearchTerm, 80, 50);
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
                mTaskDone = true;
            }
            mTaskRunning = false;
        }

        public void setTaskDone(boolean mTaskDone) {
            this.mTaskDone = mTaskDone;
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
            mSearchListAdapter.setSearchItems(mSearchItems);
            mSearchListAdapter.notifyDataSetChanged();
        }
    }
}
