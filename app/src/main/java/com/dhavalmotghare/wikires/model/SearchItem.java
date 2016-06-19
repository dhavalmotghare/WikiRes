package com.dhavalmotghare.wikires.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * "575230":{"pageid":575230,
 * "ns":0,
 * "title":"Blackbirding",
 * "index":4,
 * "thumbnail":
 * {"source":"https:\/\/upload.wikimedia.org\/wikipedia\/commons\/thumb\/2\/20\/Seizure_of_blackbirder_Daphne.jpg\/100px-Seizure_of_blackbirder_Daphne.jpg",
 * "width":100,
 * "height":90}
 */
public class SearchItem {

    private String mTitle;
    private int mPageID;
    private int mThumbnailWidth;
    private int mThumbnailHeight;
    private String mThumbnailUrl;

    public SearchItem() {
        this.mTitle = "";
        this.mThumbnailUrl = "";
    }

    public SearchItem(String title, int pageID, int thumbnailWidth, int thumbnailHeight, String thumbnailUrl) {
        this.mTitle = title;
        this.mPageID = pageID;
        this.mThumbnailWidth = thumbnailWidth;
        this.mThumbnailHeight = thumbnailHeight;
        this.mThumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getPageID() {
        return mPageID;
    }

    public void setPageID(int pageID) {
        this.mPageID = pageID;
    }

    public int getThumbnailWidth() {
        return mThumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.mThumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return mThumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.mThumbnailHeight = thumbnailHeight;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.mThumbnailUrl = thumbnailUrl;
    }

    public static List<SearchItem> parseSearchItems(JSONObject jsonObject) {

        List<SearchItem> searchItemList = new ArrayList<>();

        try {
            JSONObject queryObject = jsonObject.getJSONObject("query");
            JSONObject pages = queryObject.getJSONObject("pages");

            Iterator<String> pageIDs = pages.keys();

            while (pageIDs.hasNext()) {
                String pageID = pageIDs.next();
                String val = null;
                try {
                    JSONObject page = pages.getJSONObject(pageID);
                    SearchItem searchItem = new SearchItem();
                    searchItem.setPageID(page.getInt("pageid"));
                    searchItem.setTitle(page.getString("title"));
                    if (page.has("thumbnail")) {
                        JSONObject thumbnail = page.getJSONObject("thumbnail");
                        searchItem.setThumbnailUrl(thumbnail.getString("source"));
                        searchItem.setThumbnailHeight(thumbnail.getInt("width"));
                        searchItem.setThumbnailWidth(thumbnail.getInt("height"));
                    }
                    searchItemList.add(searchItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchItemList;
    }
}
