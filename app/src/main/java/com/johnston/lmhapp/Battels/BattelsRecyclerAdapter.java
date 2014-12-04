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
public class BattelsRecyclerAdapter extends RecyclerView.Adapter<BattelsRecyclerAdapter.ViewHolder>{
        public ArrayList<String> entries;

        public BattelsRecyclerAdapter(ArrayList<String> initialEntries) {
            entries = initialEntries;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView battelsReference;
            public TextView battelsAmount;
            public TextView battelsDescription;
            public TextView battelsDate;

            public ViewHolder(View itemView) {
                super(itemView);
                battelsReference = (TextView) itemView.findViewById(R.id.battelsReference);
                battelsAmount = (TextView) itemView.findViewById(R.id.battelsAmount);
                battelsDescription = (TextView)itemView.findViewById(R.id.battelsDescription);
                battelsDate = (TextView)itemView.findViewById(R.id.battelsDate);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.battels_card, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            position = position * 4;
            viewHolder.battelsDate.setText(entries.get(position));
            viewHolder.battelsDescription.setText(entries.get(position + 2));
            viewHolder.battelsAmount.setText(entries.get(position + 3));
            viewHolder.battelsReference.setText(entries.get(position+1));
        }

        @Override
        public int getItemCount() {
            return entries.size() / 4;
        }
    }

