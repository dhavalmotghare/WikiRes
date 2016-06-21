package com.dhavalmotghare.wikires;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

/**
 *
 */
@SmallTest
public class SearchTaskTest extends ActivityInstrumentationTestCase2<SearchResultActivity> {

    private Context mContext;
    private SearchResultActivity mSearchResultActivity;

    public SearchTaskTest() {
        super(SearchResultActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
    }

    public void testSearchTaskValidInput() throws Throwable {
        mSearchResultActivity = getActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchResultActivity.mSearchField.setText("Blackbird");
            }
        });
        sleep(4);

        assertNotNull(mSearchResultActivity.mSearchItems);
        assertTrue(mSearchResultActivity.mSearchItems.size() > 0);

    }

    public void testSearchTaskInvalidInput() throws Throwable {
        mSearchResultActivity = getActivity();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSearchResultActivity.mSearchField.setText("::::");
            }
        });
        sleep(4);

        assertNotNull(mSearchResultActivity.mSearchItems);
        assertTrue(mSearchResultActivity.mSearchItems.size() == 0);

    }

    void sleep(long seconds) {
        long milliseconds = seconds * 1000;
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {

        }
    }
}