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
class DrawerAdapter extends ArrayAdapter<String> {
    private final int resource;
    private final Context context;
    private final List<String> objects;
    public int selected = 0;
    private final Bitmap selectedCircle;
    private final Bitmap unselectedCircle;
    private final String[] iconNames;


    public DrawerAdapter(Context passedcontext, int passedresource, List<String> passedobjects, Bitmap passedSelectedCircle, Bitmap passedUnselectedCircle) {
        super(passedcontext, passedresource, passedobjects);
        resource = passedresource;
        context = passedcontext;
        objects = passedobjects;
        selectedCircle = passedSelectedCircle;
        unselectedCircle = passedUnselectedCircle;
        iconNames = context.getResources().getStringArray(R.array.iconNames);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = objects.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        TextView body = (TextView) convertView.findViewById(R.id.text1);
        ImageView circle = (ImageView) convertView.findViewById(R.id.profilePicture);
        body.setText(data);

        int id;

        if (position == selected) {
            if (iconNames[position].equals("Circle")) {
                circle.setImageBitmap(selectedCircle);
            } else {
                id = context.getResources().getIdentifier(iconNames[position] + "_blue", "drawable", "com.johnston.lmhapp");
                circle.setImageDrawable(context.getResources().getDrawable(id));
            }

            body.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            if (iconNames[position].equals("Circle")) {
                circle.setImageBitmap(unselectedCircle);
            } else {
                id = context.getResources().getIdentifier(iconNames[position], "drawable", "com.johnston.lmhapp");
                circle.setImageDrawable(context.getResources().getDrawable(id));
            }
            body.setTextColor(Color.parseColor("#de000000"));
        }

        return convertView;
    }

}

