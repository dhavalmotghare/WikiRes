package com.dhavalmotghare.wikires.wikiapi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.dhavalmotghare.wikires.Util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Wiki API request. Create request object to call WIKI search API. The request can be cancelled any
 * time by calling the {@link WikiApiRequest#cancelRequest()}.
 */
public class WikiApiRequest implements Response.Listener<String>, Response.ErrorListener {

    private static final String TAG = "WikiApiRequest";
    private static final String URL = "https://en.wikipedia.org/w/api.php";

    public static final String KEY_PROP = "prop";
    public static final String KEY_FORMAT = "format";
    public static final String KEY_ACTION = "action";
    public static final String KEY_PI_PROP = "piprop";
    public static final String KEY_PI_LIMIT = "pilimit";
    public static final String KEY_GENERATOR = "generator";
    public static final String KEY_GPS_SEARCH = "gpssearch";
    public static final String KEY_THUMBNAIL_SIZE = "pithumbsize";

    public static final String ACTION_QUERY = "query";

    private Context mContext;
    private String mCurrentAction;
    private WikiAPIListener mWikiAPIListener;

    private boolean mCancelled;

    /**
     * Callback for any API request (status - successful or failed)
     */
    public interface WikiAPIListener {

        /**
         * On request complete
         *
         * @param action
         * @param wikiResponse {@link WikiResponse}
         */
        void onComplete(String action, WikiResponse wikiResponse);
    }

    /**
     * Create a request object by passing context and {@link com.dhavalmotghare.wikires.wikiapi.WikiApiRequest.WikiAPIListener}
     *
     * @param context
     * @param wikiAPIListener
     */
    public WikiApiRequest(Context context, WikiAPIListener wikiAPIListener) {
        mContext = context;
        mWikiAPIListener = wikiAPIListener;
    }

    /**
     * Query wikipedia for images related to the passed search term
     *
     * @param searchTerm    - the term to search for
     * @param thumbnailSize - thumbnail size
     * @param limit         - max number of results
     */
    public void query(String searchTerm, int thumbnailSize, int limit) {
        Map<String, String> params = new HashMap<>();
        params.put(KEY_ACTION, ACTION_QUERY);
        params.put(KEY_PROP, "pageimages");
        params.put(KEY_FORMAT, "json");
        params.put(KEY_PI_PROP, "thumbnail");
        params.put(KEY_THUMBNAIL_SIZE, String.valueOf(thumbnailSize));
        params.put(KEY_PI_LIMIT, String.valueOf(limit));
        params.put(KEY_GENERATOR, "prefixsearch");
        params.put(KEY_GPS_SEARCH, searchTerm);

        String urlParams = NetworkUtil.urlEncodeUTF8(params);

        mCurrentAction = ACTION_QUERY;
        sendRequest(URL, urlParams, false);
    }

    private void sendRequest(String url, String data, boolean isPost) {
        if (TextUtils.isEmpty(data)) {
            Log.d(TAG, "Data empty. Skip sending request.");
            reportFailure(WikiResponse.State.INVALID_PARAMETERS);
        } else {
            NetworkUtil.submitRequest(mContext, url, data, isPost, this, this);
        }
    }

    private void reportFailure(WikiResponse.State state) {
        if (mWikiAPIListener != null && !mCancelled) {
            mWikiAPIListener.onComplete(mCurrentAction, new WikiResponse(state));
        }
    }

    private void reportSuccess(WikiResponse wikiResponse) {
        if (mWikiAPIListener != null && !mCancelled) {
            mWikiAPIListener.onComplete(mCurrentAction, wikiResponse);
        }
    }

    public void cancelRequest() {
        mCancelled = true;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
        } else if (error instanceof AuthFailureError) {
            reportFailure(WikiResponse.State.AUTH_ERROR);
        } else if (error instanceof ServerError) {
            reportFailure(WikiResponse.State.SERVER_ERROR);
        } else if (error instanceof NetworkError) {
            reportFailure(WikiResponse.State.NETWORK_ERROR);
        } else if (error instanceof ParseError) {
            reportFailure(WikiResponse.State.PARSE_ERROR);
        } else if (error instanceof TimeoutError) {
            reportFailure(WikiResponse.State.TIME_OUT);
        }
    }

    @Override
    public void onResponse(String response) {
        if (mCurrentAction != null) {
            if (mCurrentAction.equals(ACTION_QUERY)) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject != null) {
                    reportSuccess(new WikiResponse(WikiResponse.State.SUCCESS, jsonObject));
                    Log.d(TAG, "Json Response - " + jsonObject.toString());
                } else {
                    reportFailure(WikiResponse.State.FAILURE);
                }
            }
        }
        mCurrentAction = null;
    }
}