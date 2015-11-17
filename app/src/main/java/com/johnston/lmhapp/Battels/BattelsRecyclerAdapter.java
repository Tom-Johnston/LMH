package com.johnston.lmhapp.Battels;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 04/12/2014.
 */
class BattelsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<String> entries;

    public BattelsRecyclerAdapter(ArrayList<String> initialEntries) {
        entries = initialEntries;
    }

    public void updateStatus(String statusUpdate){
        entries.set(0,statusUpdate);
        notifyItemChanged(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.status, viewGroup, false);
            return new StatusHolder(v);
        }
        if(i==2){
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.battels_card_total, viewGroup, false);
            return new BattelsTotalHolder(v);
        }
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.battels_card, viewGroup, false);
        return new BattelsHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position == 0) {
            ((TextView) viewHolder.itemView).setText(entries.get(0));
        }else if(position==getItemCount()-1){
            BattelsTotalHolder battelsTotalHolder = (BattelsTotalHolder) viewHolder;
            position = (position - 1) * 4 + 1;
            battelsTotalHolder.battelsAmount.setText(entries.get(position + 3));
            battelsTotalHolder.battelsReference.setText(entries.get(position + 1));
        } else {
            BattelsHolder battelsHolder = (BattelsHolder) viewHolder;
            position = (position - 1) * 4 + 1;
            battelsHolder.battelsDate.setText(entries.get(position));
            if(entries.get(position + 1).charAt(0)=='¬'){
                battelsHolder.battelsReference.setText(entries.get(position + 1).substring(1));
            }else{
                battelsHolder.battelsReference.setText(entries.get(position + 1));
            }
            battelsHolder.battelsDescription.setText(entries.get(position + 2));
            battelsHolder.battelsAmount.setText(entries.get(position + 3));
            battelsHolder.battelsDescription.setText(entries.get(position + 2));
        }
    }

    @Override
    public int getItemCount() {
        return (entries.size() - 1)/4 + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }else if(position==getItemCount()-1){
            return 2;
        } else {
            return 1;
        }
    }

    public static class StatusHolder extends RecyclerView.ViewHolder {

        public StatusHolder(View itemView) {
            super(itemView);
        }
    }

    public static class BattelsHolder extends RecyclerView.ViewHolder {
        public final TextView battelsReference;
        public final TextView battelsAmount;
        public final TextView battelsDescription;
        public final TextView battelsDate;

        public BattelsHolder(View itemView) {
            super(itemView);
            battelsReference = (TextView) itemView.findViewById(R.id.battelsReference);
            battelsAmount = (TextView) itemView.findViewById(R.id.battelsAmount);
            battelsDescription = (TextView) itemView.findViewById(R.id.battelsDescription);
            battelsDate = (TextView) itemView.findViewById(R.id.battelsDate);
        }
    }

    public static class BattelsTotalHolder extends RecyclerView.ViewHolder {
        public final TextView battelsReference;
        public final TextView battelsAmount;

        public BattelsTotalHolder(View itemView) {
            super(itemView);
            battelsReference = (TextView) itemView.findViewById(R.id.battelsReference);
            battelsAmount = (TextView) itemView.findViewById(R.id.battelsAmount);
        }
    }
}


