package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Johnston on 10/09/2014.
 */
public class MenuListAdapter extends ArrayAdapter<String> {
    final int resource;
    final Context context;
    final List<String> objects;

    public MenuListAdapter(Context passedcontext, int passedresource, List<String> passedobjects) {
        super(passedcontext, passedresource, passedobjects);
        resource = passedresource;
        context = passedcontext;
        objects = passedobjects;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String data = objects.get(position);
        String code = data.substring(0, 2);
        String message = data.substring(2);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        TextView body = (TextView) convertView.findViewById(R.id.ListViewBody);
        body.setText(message);
        if (code.equals("00")) {
            body.setBackgroundColor(Color.parseColor("#bdbdbd"));
//            body.setEnabled(false);
//            body.setTypeface(Typeface.create(body.getTypeface(), Typeface.NORMAL));
            body.setTextAppearance(context, R.style.oldMenuBody);
        } else if (code.equals("01")) {
            body.setBackgroundColor(0xFFFFFFFF);
//            body.setEnabled(true);
//            body.setTypeface(Typeface.create(body.getTypeface(), Typeface.NORMAL));
            body.setTextAppearance(context, R.style.currentMenuBody);
        } else if (code.equals("10")) {
            body.setBackgroundColor(Color.parseColor("#bdbdbd"));
//            body.setEnabled(false);
//            body.setTypeface(Typeface.create(body.getTypeface(), Typeface.BOLD));
            body.setTextAppearance(context, R.style.oldMenuTitle);
        } else if (code.equals("11")) {
            body.setBackgroundColor(0xFFFFFFFF);
//            body.setEnabled(true);
//            body.setTypeface(Typeface.create(body.getTypeface(), Typeface.BOLD));
            body.setTextAppearance(context, R.style.currentMenuTitle);
        } else if (code.equals("12")) {
//
            body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        } else if (code.equals("02")) {
            body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        return convertView;
    }

}
