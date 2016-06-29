package com.dhavalmotghare.wikires.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Network utilities
 */
public class NetworkUtil {

    /**
     * millisecond
     */
    private static final int UPLOADING_TIMEOUT = 30 * 1000;

    /**
     * Volley get request class to handle a HTTP get request
     */
    public static class GetRequest extends StringRequest {

        /**
         * Constructor
         *
         * @param url           - server URL plus parameters if any appended as part of the URL
         * @param listener      - success listener
         * @param errorListener - error listener
         */
        public GetRequest(String url, Listener<String> listener, ErrorListener errorListener) {
            super(Method.GET, url, listener, errorListener);
        }

    }

    /**
     * Volley post request class to handle a HTTP post request
     */
    public static class PostRequest extends StringRequest {

        /**
         * Data to be submitted as part of the post request
         */
        private String postData;

        /**
         * Constructor
         *
         * @param url           - server URL
         * @param data          - Data to be submitted as part of the post request
         * @param listener      - success listener
         * @param errorListener - error listener
         */
        public PostRequest(String url, String data, Listener<String> listener, ErrorListener errorListener) {
            super(Method.POST, url, listener, errorListener);
            this.postData = data;
        }

        /*
         * (non-Javadoc)
         * @see com.android.volley.Request#getBody()
         */
        public byte[] getBody() throws AuthFailureError {
            return postData.getBytes();
        }

        /*
         * (non-Javadoc)
         * @see com.android.volley.Request#getHeaders()
         */
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Content-Type", "application/x-www-form-urlencoded");
            return params;
        }

    }

    /**
     * Submit the request (either get or post)
     *
     * @param context         - {@link Context}
     * @param url             - Server URL
     * @param data            - Data to be submitted as part of the post or get request
     * @param isPost          - whether post or get request
     * @param successListener - success listener
     * @param errorListener   - error listener
     */
    public static void submitRequest(Context context, String url, String data, boolean isPost, Listener<String> successListener,
                                     ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest sr = null;
        if (isPost) {
            sr = new PostRequest(url, data, successListener, errorListener);
        } else {
            sr = new GetRequest(url + "?" + data, successListener, errorListener);
        }

        sr.setRetryPolicy(new DefaultRetryPolicy(UPLOADING_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    /**
     * Encode a string using 'UTF-8'. {@see URLEncoder#encode}
     *
     * @param string
     * @return - encoded string
     */
    public static String urlEncodeUTF8(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Encode map parameters using 'UTF-8' and append them as url parameters. {@see URLEncoder#encode}
     *
     * @param map - map of key value pairs to be encoded
     * @return - UTF-8 encoded url parameter string
     */
    public static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    /**
     * Check current available network.
     *
     * @param context - Context of the Application
     * @return true if any active network is connected, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }
}
