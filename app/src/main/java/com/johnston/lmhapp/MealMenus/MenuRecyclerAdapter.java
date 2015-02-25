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
class MenuRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<String> entries;

    public MenuRecyclerAdapter(ArrayList<String> initialEntries) {
        entries = initialEntries;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.status, viewGroup, false);
            return new StatusHolder(v);
        }
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.menu_card, viewGroup, false);
        return new MealHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position == 0) {
            ((TextView) viewHolder.itemView).setText("Finished");
        } else {
            MealHolder mealHolder = (MealHolder) viewHolder;
            position = (position - 1) * 3;
            mealHolder.mealTime.setText(entries.get(position));
            mealHolder.mealTitle.setText(entries.get(position+1));
            mealHolder.mealMenu.setText(entries.get(position + 2));
        }
    }

    @Override
    public int getItemCount() {
        return entries.size() / 3 + 1;
    }

    public static class MealHolder extends RecyclerView.ViewHolder {
        public final TextView mealTitle;
        public final TextView mealMenu;
        public final TextView mealTime;

        public MealHolder(View itemView) {
            super(itemView);
            mealTitle = (TextView) itemView.findViewById(R.id.mealTitle);
            mealMenu = (TextView) itemView.findViewById(R.id.mealMenu);
            mealTime = (TextView) itemView.findViewById(R.id.mealTime);
        }
    }

    public static class StatusHolder extends RecyclerView.ViewHolder {

        public StatusHolder(View itemView) {
            super(itemView);
        }
    }
}
