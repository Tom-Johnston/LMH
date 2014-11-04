package com.johnston.lmhapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tom on 03/11/2014.
 */
public class drawerAdapter extends ArrayAdapter<String> {
    final int resource;
    final Context context;
    final List<String> objects;
    public int selected = 0;
    Bitmap selectedCircle;
    Bitmap unselectedCircle;


    public drawerAdapter(Context passedcontext, int passedresource, List<String> passedobjects, Bitmap passedSelectedCircle, Bitmap passedUnselectedCircle) {
        super(passedcontext, passedresource, passedobjects);
        resource = passedresource;
        context = passedcontext;
        objects = passedobjects;
        selectedCircle = passedSelectedCircle;
        unselectedCircle = passedUnselectedCircle;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = objects.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        TextView body = (TextView) convertView.findViewById(R.id.text1);
        ImageView circle = (ImageView) convertView.findViewById(R.id.imageView);
        body.setText(data);
        if (position == selected) {
            circle.setImageBitmap(selectedCircle);
            body.setTextColor(Color.parseColor("#002147"));
            //TODO change the above line.
        } else {
            circle.setImageBitmap(unselectedCircle);
            body.setTextColor(Color.parseColor("#57000000"));
        }


        return convertView;
    }

}

