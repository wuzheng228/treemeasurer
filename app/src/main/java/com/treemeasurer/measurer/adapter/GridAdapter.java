package com.treemeasurer.measurer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.treemeasurer.measurer.R;

import java.io.File;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private int resourse;
    private List<File> paths;

    public GridAdapter(Context context, int resourse, List<File> paths) {
        this.context = context;
        this.resourse = resourse;
        this.paths = paths;
    }
    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_grid_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Glide.with(context).load(paths.get(position))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.2f)
                .into(holder.imageView);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
