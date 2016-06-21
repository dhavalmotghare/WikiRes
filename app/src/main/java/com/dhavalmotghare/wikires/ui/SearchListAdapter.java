package com.dhavalmotghare.wikires.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dhavalmotghare.wikires.R;
import com.dhavalmotghare.wikires.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    private Context mContext;
    private List<SearchItem> mSearchItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemID;
        TextView itemTitle;
        ImageView itemImage;
        LinearLayout container;

        public ViewHolder(View view) {
            super(view);
            container = (LinearLayout) view.findViewById(R.id.search_item);
            itemID = (TextView) view.findViewById(R.id.search_item_page_id);
            itemTitle = (TextView) view.findViewById(R.id.search_item_title);
            itemImage = (ImageView) view.findViewById(R.id.search_image);
        }
    }

    public SearchListAdapter(Context context, List<SearchItem> searchItems) {
        mContext = context;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemID.setText(mSearchItems.get(position).getPageID() + "");
        holder.itemTitle.setText(mSearchItems.get(position).getTitle());
        holder.itemImage.setImageResource(0);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                holder.itemImage.setBackgroundColor(mContext.getResources().getColor(R.color.background_color));
            } else {
//                Picasso.with(mContext)
//                        .load(mSearchItems.get(position).getThumbnailUrl())
//                        .noPlaceholder()
//                        .error(R.mipmap.ic_error_outline_white_36dp)
//                        .fit()
//                        .centerCrop()
//                        .into(holder.itemImage);

                Glide.with(mContext)
                        .load(mSearchItems.get(position).getThumbnailUrl())
                        .animate(android.R.anim.fade_in)
                        .centerCrop()
                        .into(holder.itemImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSearchItems.size();
    }
}