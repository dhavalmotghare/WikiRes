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

import static android.R.color.holo_orange_light;
import static android.R.color.transparent;

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
        TextView itemText;
        ImageView itemImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        View view = convertView;

        if (convertView == null) {
            holder = new Holder();

            view = inflater.inflate(R.layout.search_list_item, null);
            holder.itemText = (TextView) view.findViewById(R.id.search_text);
            holder.itemImage = (ImageView) view.findViewById(R.id.search_image);
            holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (mSearchItems.size() <= 0) {
            holder.itemText.setText("No Data");
        } else {
            holder.itemText.setText(mSearchItems.get(position).getTitle());

            if (TextUtils.isEmpty(mSearchItems.get(position).getThumbnailUrl())) {
                holder.itemImage.setImageResource(R.mipmap.placeholder);
                holder.itemImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                holder.itemImage.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                // Trigger the download of the URL asynchronously into the image view.
                Picasso.with(mContext) //
                        .load(mSearchItems.get(position).getThumbnailUrl())
                        .placeholder(R.mipmap.placeholder)
                        .error(R.mipmap.placeholder)
                        .fit()
                        .centerCrop()//
                        .into(holder.itemImage);
                holder.itemImage.setBackgroundColor(mContext.getResources().getColor(transparent));
            }
        }
        return view;
    }
}