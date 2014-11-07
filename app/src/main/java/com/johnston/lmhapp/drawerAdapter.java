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
    String[] iconNames;


    public drawerAdapter(Context passedcontext, int passedresource, List<String> passedobjects, Bitmap passedSelectedCircle, Bitmap passedUnselectedCircle) {
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
        ImageView circle = (ImageView) convertView.findViewById(R.id.imageView);
        body.setText(data);

        int id;

        if (position == selected) {
            if (iconNames[position].equals("Circle")){
                circle.setImageBitmap(selectedCircle);
            }else{
                id = context.getResources().getIdentifier(iconNames[position]+"_blue","drawable","com.johnston.lmhapp");
                circle.setImageDrawable(context.getResources().getDrawable(id));
            }

            body.setTextColor(Color.parseColor("#002147"));
            //TODO change the above line.
        } else {
            if (iconNames[position].equals("Circle")){
                circle.setImageBitmap(unselectedCircle);
            }else{
                id = context.getResources().getIdentifier(iconNames[position],"drawable","com.johnston.lmhapp");
                circle.setImageDrawable(context.getResources().getDrawable(id));
            }
            body.setTextColor(Color.parseColor("#57000000"));
        }


        return convertView;
    }

}

