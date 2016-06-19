package com.dhavalmotghare.wikires;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.dhavalmotghare.wikires.wikiapi.WikiApiRequest;
import com.dhavalmotghare.wikires.wikiapi.WikiResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@SmallTest
public class WikiApiRequestTest extends InstrumentationTestCase {

    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
    }

    public void testQueryAPI() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        WikiApiRequest wikiApiRequest = new WikiApiRequest(mContext, new WikiApiRequest.WikiAPIListener() {
            @Override
            public void onComplete(String action, WikiResponse wikiResponse) {
                assertNotNull(wikiResponse);
                assertNotNull(action);

                assertTrue(action.equals(WikiApiRequest.ACTION_QUERY));
                assertTrue(wikiResponse.getRequestState() == WikiResponse.State.SUCCESS);
                assertTrue(wikiResponse.getResponseObject() != null);
                latch.countDown();
            }
        });
        wikiApiRequest.query("Blackbird", 100, 50);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}