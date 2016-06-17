
package com.dhavalmotghare.wikires.Util;

import android.content.Context;

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

    ;

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

    ;

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

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

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

}
