package com.dhavalmotghare.wikires.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhavalmotghare.wikires.ImageDetailActivity;
import com.dhavalmotghare.wikires.R;
import com.dhavalmotghare.wikires.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Result adapter for the recycle view.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private Activity mActivity;
    private List<SearchItem> mSearchItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemID;
        TextView itemTitle;
        ImageView itemImage;
        RelativeLayout container;

        public ViewHolder(View view) {
            super(view);
            container = (RelativeLayout) view.findViewById(R.id.search_item);
            itemID = (TextView) view.findViewById(R.id.search_item_page_id);
            itemTitle = (TextView) view.findViewById(R.id.search_item_title);
            itemImage = (ImageView) view.findViewById(R.id.search_image);
        }
    }

    public SearchListAdapter(Activity activity, List<SearchItem> searchItems) {
        mActivity = activity;
        mSearchItems = searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.mSearchItems = searchItems;
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.search_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (mSearchItems == null || mSearchItems.size() <= 0) return;

        holder.itemID.setText(mSearchItems.get(position).getPageID() + "");
        holder.itemTitle.setText(mSearchItems.get(position).getTitle());
        holder.itemImage.setImageResource(0);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(mSearchItems.get(position).getThumbnailUrl())) {
                    Intent intent = new Intent(mActivity, ImageDetailActivity.class);
                    intent.putExtra(ImageDetailActivity.KEY_IMAGE_URL, mSearchItems.get(position).getThumbnailUrl());
                    intent.putExtra(ImageDetailActivity.KEY_IMAGE_TITLE, mSearchItems.get(position).getTitle());
                    intent.putExtra(ImageDetailActivity.KEY_IMAGE_PAGE_ID, mSearchItems.get(position).getPageID() + "");

                    // Show the activity transition with hero image on api level 21 and above, for
                    // lower versions fall back to default transition
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivityWithTransition(holder, intent);
                    } else {
                        mActivity.startActivity(intent);
                    }
                } else {
                    Toast.makeText(mActivity, R.string.error_no_image, Toast.LENGTH_LONG).show();
                }
            }
        });

        if (mSearchItems.size() <= 0) {
            holder.itemTitle.setText("No Data");
        } else {
            holder.itemID.setText(mSearchItems.get(position).getPageID() + "");
            holder.itemTitle.setText(mSearchItems.get(position).getTitle());
            holder.itemImage.setImageResource(0);

            if (TextUtils.isEmpty(mSearchItems.get(position).getThumbnailUrl())) {
                holder.itemImage.setImageResource(R.mipmap.ic_texture_white_36dp);
                holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                Picasso.with(mActivity)
                        .load(mSearchItems.get(position).getThumbnailUrl())
                        .noPlaceholder()
                        .error(R.mipmap.ic_error_outline_white_36dp)
                        .fit()
                        .centerCrop()
                        .into(holder.itemImage);
            }
        }
    }

    /**
     * Activity transition animation available on the api level 21 and above
     *
     * @param holder
     * @param intent
     */
    @TargetApi(21)
    private void startActivityWithTransition(ViewHolder holder, Intent intent) {
        Pair firstPair = Pair.create(holder.itemImage, "transition_image");
        Pair secondPair = Pair.create(holder.itemTitle, "transition_image_title");
        Pair thirdPair = Pair.create(holder.itemID, "transition_image_id");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mActivity,
                firstPair, secondPair, thirdPair);
        mActivity.startActivity(intent, options.toBundle());
    }

    @Override
    public int getItemCount() {
        return mSearchItems.size();
    }
}