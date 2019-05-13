package com.industrialmaster.notationchordslyrics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DataModel> dataModelArrayList;

    public ListAdapter(Context context, ArrayList<DataModel> dataModelArrayList) {

        this.context = context;
        this.dataModelArrayList = dataModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return dataModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.post_item, null, true);

            holder.ivCategory = (ImageView) convertView.findViewById(R.id.ivCatogary);
            holder.tvSongName = (TextView) convertView.findViewById(R.id.tvSongName);
            holder.tvArtistName = (TextView) convertView.findViewById(R.id.tvArtistName);
            holder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            holder.tvDislikes = (TextView) convertView.findViewById(R.id.tvDislikes);
            holder.tvContributer = (TextView) convertView.findViewById(R.id.tvContributor);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        Picasso.get().load(dataModelArrayList.get(position).getImgURL()).into(holder.ivCategory);
        holder.tvSongName.setText(dataModelArrayList.get(position).getSongName());
        holder.tvArtistName.setText(dataModelArrayList.get(position).getArtistName());
        holder.tvContributer.setText(dataModelArrayList.get(position).getContributor());
        holder.tvLikes.setText(String.valueOf(dataModelArrayList.get(position).getLikes()));
        holder.tvDislikes.setText(String.valueOf(dataModelArrayList.get(position).getDislikes()));

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvSongName, tvArtistName, tvContributer, tvLikes, tvDislikes;
        protected ImageView ivCategory;
    }

}
