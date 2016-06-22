package com.dhavalmotghare.wikires.wikiapi;

import org.json.JSONObject;

/**
 * Response object returned after completion of Wiki API request.
 */
public class WikiResponse {

    public enum State {

        SUCCESS(0),
        FAILURE(1),
        INVALID_PARAMETERS(2),
        AUTH_ERROR(3),
        SERVER_ERROR(4),
        NETWORK_ERROR(5),
        PARSE_ERROR(6),
        TIME_OUT(7);

        private int mState;

        /**
         * Constructor
         *
         * @param state - request state
         */
        State(int state) {
            mState = state;
        }

        /**
         * Get the request state
         *
         * @return
         */
        public int getState() {
            return mState;
        }
    }

    private State mRequestState;
    private JSONObject mResponseObject;

    public WikiResponse(State state) {
        mRequestState = state;
        mResponseObject = new JSONObject();
    }

    public WikiResponse(State state, JSONObject responseObject) {
        mRequestState = state;
        mResponseObject = responseObject;
    }

    public State getRequestState() {
        return mRequestState;
    }

    public JSONObject getResponseObject() {
        return mResponseObject;
    }

}
