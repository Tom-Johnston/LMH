package com.johnston.lmhapp.Formal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Tom on 13/11/2014.
 */
public class FormalListAdapter extends ArrayAdapter<String> {

        final int resource;
        final Context context;
        final List<String> objects;

        public FormalListAdapter(Context passedcontext, int passedresource, List<String> passedobjects) {
            super(passedcontext, passedresource, passedobjects);
            resource = passedresource;
            context = passedcontext;
            objects = passedobjects;
        }

        @Override
        public int getCount() {
            return objects.size() / 6;
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
            position = position * 6;
            ((TextView)convertView.findViewById(R.id.formalDate)).setText(objects.get(position));
            ((TextView)convertView.findViewById(R.id.formalName)).setText(objects.get(position + 1));
            ((TextView)convertView.findViewById(R.id.formalNumberGone)).setText(objects.get(position + 3));
            ((TextView)convertView.findViewById(R.id.formalNumberLeft)).setText(objects.get(position + 4));
            int numberGone = Integer.parseInt(objects.get(position+3));
            int numberLeft = Integer.parseInt(objects.get(position+4));
            int sizex =convertView.getWidth();
            System.out.println(sizex);
            int sizey = convertView.getHeight();
            System.out.println(sizey);
            ((ImageView)convertView.findViewById(R.id.graphicNumberLeft)).setImageBitmap(generateGraphicNumberLeft(10,numberLeft,400,100));
            return convertView;
        }

    public Bitmap generateGraphicNumberLeft(int numberGone,int numberLeft,int sizex,int sizey){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(sizex,sizey,conf);
        Canvas c = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#001123"));
        paint.setStyle(Paint.Style.FILL);
        double proportionGone = numberGone/(double)(numberGone+numberLeft);
        System.out.println(proportionGone);
        int numberOFSquares = 5;
        int sizeOfEachSquare = sizex/(numberOFSquares+1);
        int sizeOfGap = sizeOfEachSquare/(numberOFSquares-1);
        if(sizey<sizeOfEachSquare){
            sizeOfEachSquare=sizey;
        }
        paint.setColor(Color.parseColor("#E0E0E0"));
        double proportionOfSquare;
        for(int i =0;i<numberOFSquares;i++) {
            c.drawRect(i * sizeOfEachSquare + i * sizeOfGap, 0, (i + 1) * sizeOfEachSquare + i * sizeOfGap, sizey, paint);
        }
        paint.setColor(Color.parseColor("#757575"));
        for(int i =0;i<numberOFSquares;i++){
            proportionOfSquare = numberOFSquares*proportionGone-(i);
            System.out.println(proportionOfSquare);
            if(proportionOfSquare>=1){
                System.out.println(i*sizeOfEachSquare+i*sizeOfGap+","+0+","+(float)((i+1)*sizeOfEachSquare+i*sizeOfGap)+","+sizey);
                c.drawRect(i*sizeOfEachSquare+i*sizeOfGap,0,(i+1)*sizeOfEachSquare+i*sizeOfGap,sizey,paint);

            }else if(0<proportionOfSquare){
                System.out.println(i*sizeOfEachSquare+i*sizeOfGap+","+0+","+(float)((i)*sizeOfEachSquare+i*sizeOfGap+proportionOfSquare*sizeOfEachSquare)+","+sizey);
                c.drawRect(i*sizeOfEachSquare+i*sizeOfGap,0,(float)((i)*sizeOfEachSquare+i*sizeOfGap+proportionOfSquare*sizeOfEachSquare),sizey,paint);
            }else{

            }
        }


        return bmp;
    }


    }



