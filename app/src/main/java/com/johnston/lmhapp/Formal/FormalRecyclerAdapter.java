package com.johnston.lmhapp.Formal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 28/11/2014.
 */
public class FormalRecyclerAdapter extends RecyclerView.Adapter<FormalRecyclerAdapter.ViewHolder> {
    public ArrayList<String> entries;
    public ArrayList<String> listOfMeals;

    public FormalRecyclerAdapter(ArrayList<String> initialEntries, ArrayList<String> listOfMeals) {
        entries = initialEntries;
        this.listOfMeals = listOfMeals;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView formalName;
        public TextView formalDate;
        public TextView formalMenu;
        public TextView formalNumberLeft;
        public TextView formalNumberGone;
        public ImageView graphicNumberLeft;
        public Button formalButton;

        public ViewHolder(View itemView) {
            super(itemView);
            formalName = (TextView) itemView.findViewById(R.id.formalName);
            formalDate = (TextView) itemView.findViewById(R.id.formalDate);
            formalMenu = (TextView) itemView.findViewById(R.id.formalMenu);
            formalNumberLeft = (TextView) itemView.findViewById(R.id.formalNumberLeft);
            formalNumberGone = (TextView) itemView.findViewById(R.id.formalNumberGone);
            graphicNumberLeft = (ImageView) itemView.findViewById(R.id.graphicNumberLeft);
            formalButton = (Button)itemView.findViewById(R.id.formalButton);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.formal_card, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.formalMenu.setText(listOfMeals.get(position));
        viewHolder.formalButton.setTag(position);
        position = position * 6;
        final int numberGone = Integer.parseInt(entries.get(position + 3));
        final int numberLeft = Integer.parseInt(entries.get(position + 4));
        viewHolder.formalDate.setText(entries.get(position));
        viewHolder.formalName.setText(entries.get(position + 1));
        viewHolder.formalNumberGone.setText(entries.get(position + 3));
        viewHolder.formalNumberLeft.setText(entries.get(position + 4));
        viewHolder.graphicNumberLeft.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                v.removeOnLayoutChangeListener(this);
                int sizex = v.getWidth();
                int sizey = v.getHeight() / 2;
                ((ImageView) v).setImageBitmap(generateGraphicNumberLeft(numberGone, numberLeft, sizex, sizey));
            }
        });
        System.out.println(viewHolder.formalDate.getHeight());
        System.out.println(viewHolder.formalDate.getMeasuredHeight());


    }

    public Bitmap generateGraphicNumberLeft(int numberGone, int numberLeft, int sizex, int sizey) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(sizex, sizey, conf);
        Canvas c = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#001123"));
        paint.setStyle(Paint.Style.FILL);
        double proportionGone = numberGone / (double) (numberGone + numberLeft);


        float sizeOfEachSquare = sizey;
        float sizeOfGap = sizeOfEachSquare / 3;
        int numberOFSquares = (int) (sizex / (sizeOfEachSquare + sizeOfGap));

        paint.setColor(Color.parseColor("#E0E0E0"));
        double proportionOfSquare;
        for (int i = 0; i < numberOFSquares; i++) {
            c.drawRect(i * sizeOfEachSquare + i * sizeOfGap, 0, (i + 1) * sizeOfEachSquare + i * sizeOfGap, sizey, paint);
        }
        paint.setColor(Color.parseColor("#757575"));
        for (int i = 0; i < numberOFSquares; i++) {
            proportionOfSquare = numberOFSquares * proportionGone - (i);
            if (proportionOfSquare >= 1) {
                c.drawRect(i * sizeOfEachSquare + i * sizeOfGap, 0, (i + 1) * sizeOfEachSquare + i * sizeOfGap, sizey, paint);

            } else if (0 < proportionOfSquare) {
                c.drawRect(i * sizeOfEachSquare + i * sizeOfGap, 0, (float) ((i) * sizeOfEachSquare + i * sizeOfGap + proportionOfSquare * sizeOfEachSquare), sizey, paint);
            }
        }


        return bmp;
    }


    @Override
    public int getItemCount() {
        return entries.size() / 6;
    }
}
