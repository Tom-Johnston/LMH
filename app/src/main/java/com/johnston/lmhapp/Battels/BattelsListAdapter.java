package com.johnston.lmhapp.Battels;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Tom on 28/10/2014.
 */
public class BattelsListAdapter extends ArrayAdapter<String> {
    final int resource;
    final Context context;
    final List<String> objects;

    public BattelsListAdapter(Context passedcontext, int passedresource, List<String> passedobjects) {
        super(passedcontext, passedresource, passedobjects);
        resource = passedresource;
        context = passedcontext;
        objects = passedobjects;
    }

    @Override
    public int getCount() {
        return objects.size() / 4;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        position = position * 4;

        TextView battelsDate = (TextView) convertView.findViewById(R.id.battelsDate);
        TextView battelsDescription = (TextView) convertView.findViewById(R.id.battelsDescription);
        TextView battelsAmount = (TextView) convertView.findViewById(R.id.battelsAmount);

        if (objects.get(position).equals("Total")) {
            battelsDate.setTypeface(null, Typeface.BOLD);
            battelsDescription.setTypeface(null, Typeface.BOLD);
            battelsAmount.setTypeface(null, Typeface.BOLD);
        } else {
            battelsDate.setTypeface(null, Typeface.NORMAL);
            battelsDescription.setTypeface(null, Typeface.NORMAL);
            battelsAmount.setTypeface(null, Typeface.NORMAL);
        }
        battelsDate.setText(objects.get(position));
        battelsDescription.setText(objects.get(position + 2));
        battelsAmount.setText(objects.get(position + 3));
        return convertView;
    }

}