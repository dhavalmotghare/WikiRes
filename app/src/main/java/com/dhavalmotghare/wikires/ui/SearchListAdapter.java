package com.dhavalmotghare.wikires.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhavalmotghare.wikires.R;
import com.dhavalmotghare.wikires.model.SearchItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchListAdapter extends BaseAdapter {

    private Context mContext;
    private static LayoutInflater inflater = null;

    private List<SearchItem> mSearchItems;

    public SearchListAdapter(Context context, List<SearchItem> searchItems) {
        mSearchItems = searchItems;

        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSearchItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.mSearchItems = searchItems;
    }

    public class Holder {
        TextView itemID;
        TextView itemTitle;
        ImageView itemImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;

        if (convertView == null) {
            holder = new Holder();
            view = inflater.inflate(R.layout.search_list_item, null);
            holder.itemID = (TextView) view.findViewById(R.id.search_item_page_id);
            holder.itemTitle = (TextView) view.findViewById(R.id.search_item_title);
            holder.itemImage = (ImageView) view.findViewById(R.id.search_image);
            holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

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
                Picasso.with(mContext)
                        .load(mSearchItems.get(position).getThumbnailUrl())
                        .noPlaceholder()
                        .error(R.mipmap.ic_error_outline_white_36dp)
                        .fit()
                        .centerCrop()
                        .into(holder.itemImage);
            }
        }
        return view;
    }
}