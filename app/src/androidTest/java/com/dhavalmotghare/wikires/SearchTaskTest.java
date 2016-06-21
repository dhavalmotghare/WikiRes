package com.dhavalmotghare.wikires;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 *
 */
@SmallTest
public class SearchTaskTest extends ActivityUnitTestCase<FakeSearchResultActivity> {

    private Context mContext;
    private FakeSearchResultActivity mSearchResultActivity;

    public SearchTaskTest() {
        super(FakeSearchResultActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
        startActivity();
    }

    public void testSearchTaskValidInput() throws Exception {
        mSearchResultActivity = getActivity();
        mSearchResultActivity.mSearchField.setText("Blackbird");

        sleep(4);

        assertNotNull(mSearchResultActivity.mSearchItems);
        assertTrue(mSearchResultActivity.mSearchItems.size() > 0);

    }

    public void testSearchTaskInvalidInput() throws Exception {
        mSearchResultActivity = getActivity();
        mSearchResultActivity.mSearchField.setText("::::");

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

    void startActivity() {
        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Intent.ACTION_MAIN), null, null);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
}