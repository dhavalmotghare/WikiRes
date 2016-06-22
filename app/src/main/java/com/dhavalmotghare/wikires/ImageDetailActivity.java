package com.dhavalmotghare.wikires;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Activity used to display image detail view.
 */
public class ImageDetailActivity extends AppCompatActivity {

    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_IMAGE_TITLE = "image_title";
    public static final String KEY_IMAGE_PAGE_ID = "image_page_ID";

    private String mImageUrl;
    private String mImageTitle;
    private String mImagePageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView itemID = (TextView) findViewById(R.id.popup_search_item_page_id);
        TextView itemTitle = (TextView) findViewById(R.id.popup_search_item_title);
        ImageView itemImage = (ImageView) findViewById(R.id.popup_search_image);

        mImageUrl = getIntent().getExtras().getString(KEY_IMAGE_URL);
        mImageTitle = getIntent().getExtras().getString(KEY_IMAGE_TITLE);
        mImagePageID = getIntent().getExtras().getString(KEY_IMAGE_PAGE_ID);

        itemID.setText(mImagePageID);
        itemTitle.setText(mImageTitle);

        Picasso.with(this)
                .load(mImageUrl)
                .noPlaceholder()
                .error(R.mipmap.ic_error_outline_white_36dp)
                .fit()
                .centerCrop()
                .into(itemImage);
    }

}
