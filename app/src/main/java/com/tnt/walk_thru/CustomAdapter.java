package com.tnt.walk_thru;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by EricTremblay on 16-01-23.
 */
public class CustomAdapter extends ArrayAdapter<Restaurant> {

    public CustomAdapter(Context context, List restoList) {
        super(context, R.layout.custom_row, restoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.custom_row, parent, false);
        String name = getItem(position).name;
        String distance = getItem(position).distance;
        Bitmap logo = getItem(position).logo;
        TextView textViewName = (TextView) customView.findViewById(R.id.TextViewName);
        TextView textViewDistance = (TextView) customView.findViewById(R.id.TextViewDistance);
        ImageView imageViewLogo = (ImageView) customView.findViewById(R.id.ImageLogo);
        textViewName.setText(name);
        textViewDistance.setText(distance);
        imageViewLogo.setImageBitmap(logo);   // Uses same image for all items
        return customView;
    }
}
