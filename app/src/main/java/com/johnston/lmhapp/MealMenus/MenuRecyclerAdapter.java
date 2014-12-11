package com.johnston.lmhapp.MealMenus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 02/12/2014.
 */
public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder> {
    public ArrayList<String> entries;

    public MenuRecyclerAdapter(ArrayList<String> initialEntries) {
        entries = initialEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.menu_card, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        position = position * 2;
        viewHolder.mealTitle.setText(entries.get(position));
        viewHolder.mealMenu.setText(entries.get(position + 1));
    }

    @Override
    public int getItemCount() {
        return entries.size() / 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mealTitle;
        public TextView mealMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            mealTitle = (TextView) itemView.findViewById(R.id.mealTitle);
            mealMenu = (TextView) itemView.findViewById(R.id.mealMenu);
        }
    }
}
