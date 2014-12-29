package com.johnston.lmhapp.Formal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Tom on 23/11/2014.
 */
public class FormalDetailsListAdapter extends ArrayAdapter<String> {

    final int resource;
    final Context context;
    final List<String> objects;

    public FormalDetailsListAdapter(Context passedcontext, int passedresource, List<String> passedobjects) {
        super(passedcontext, passedresource, passedobjects);
        resource = passedresource;
        context = passedcontext;
        objects = passedobjects;
    }

    @Override
    public int getCount() {
        return objects.size() / 2;
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
        ((TextView) convertView.findViewById(R.id.name)).setText(objects.get(2 * position));
        ((TextView) convertView.findViewById(R.id.numberOfGuests)).setText(objects.get(2 * position + 1));
        return convertView;
    }


}
