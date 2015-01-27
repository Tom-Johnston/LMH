package com.johnston.lmhapp.LiveWallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Handler;

import com.johnston.lmhapp.ImageGeneratorAsync;
import com.johnston.lmhapp.R;
import com.johnston.lmhapp.Triangle;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Tom on 21/01/2015.
 */
public class TriangleGeneratorAsync extends AsyncTask<Object,Void,Void> {
    @Override
    protected Void doInBackground(Object... params) {
        System.out.println("Generate Triangles");
        int desiredWidth = (int) params[0];
        int desiredHeight = (int)params[1];
        int sizex = (Integer) params[0];
        int sizey = (Integer) params[1];
        int numberOfIterationsToDo = (int) params[2];
        Handler handler = (Handler) params[3];
        Context context = (Context) params[4];

        if (sizex > 2 * sizey) {
//            Scale up y to match sizex;
            sizey = sizex / 2;
        } else {
//            Scale up x to match y
            sizex = 2 * sizey;
        }




//        Starting bottom triangle
        ArrayList<Triangle> triangles = new ArrayList<>();
        Triangle triangle = new Triangle();
        triangle.x1 = 0;
        triangle.y1 = 0;
        triangle.x2 = sizex;
        triangle.y2 = 0;
        triangle.x3 = 0;
        triangle.y3 = sizey;
        triangles.add(triangle);

// Starting top triangle
        triangle = new Triangle();
        triangle.x1 = sizex;
        triangle.y1 = sizey;
        triangle.x2 = 0;
        triangle.y2 = sizey;
        triangle.x3 = sizex;
        triangle.y3 = 0;
        triangles.add(triangle);


        for (int j = 0; j < numberOfIterationsToDo; j++) {
            triangles = NextIteration(triangles,desiredWidth,desiredHeight);
        }
        handler.obtainMessage(0,triangles).sendToTarget();
        handler.obtainMessage(1,generateColours(triangles,context,desiredHeight,desiredWidth)).sendToTarget();
        System.out.println("Finished Generating Triangles");
        return null;
    }



    public ArrayList<Integer> generateColours(ArrayList<Triangle> triangles,Context context, int desiredWidth, int desiredHeight){
        ArrayList<Integer> targetColours = new ArrayList<>();
        int oxfordBlue = Color.parseColor("#002147");
        int white = Color.parseColor("#ffffff");
        int alpha = -1;



        int margin = desiredWidth/8;
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_for_background);
        float scale = ((float)logo.getHeight())/((float)desiredWidth-2*margin); //We will scale the height so the height of the logo fits the height of the display with margins.

        Triangle triangle;
        int i;
        int x;
        int y;

        for (i = 0; i < triangles.size(); i++) {
            triangle = triangles.get(i);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(triangle.x1, triangle.y1);
            path.lineTo(triangle.x2, triangle.y2);
            path.lineTo(triangle.x3, triangle.y3);
            path.close();
            int periodInX = (int) (((float)logo.getWidth())/scale + margin); // Repeat the logo horizontally.
            int xMargin = (desiredWidth-periodInX*((int)desiredWidth/periodInX)+margin)/2;       // Center the logos.
            x = (int) (scale*(triangle.middlex%periodInX-xMargin));
            y = (int) (scale*(triangle.middley-margin));
            if(0<=x&&x<logo.getWidth()&&0<=y&&y<logo.getHeight()) {
                if(logo.getPixel(x,y)==oxfordBlue){
                    targetColours.add(oxfordBlue);
                }else if(Color.alpha(logo.getPixel(x,y))==0){
                    targetColours.add(alpha);
                }else{
//                    This contains the white triangles and the triangles that are in between the specified colours due to anti-aliasing.
                    targetColours.add(white);
                }
            }else{
                targetColours.add(alpha);
            }
        }
        return targetColours;
    }



    public ArrayList<Triangle> NextIteration(ArrayList<Triangle> passedTriangles,int desiredWidth, int desiredHeight) {
        ArrayList<Triangle> triangles = new ArrayList<>();
        Triangle newTriangle;
        int numberOfTriangles = passedTriangles.size();
        Triangle triangle;
        for (int i = 0; i < numberOfTriangles; i++) {

            triangle = passedTriangles.get(i);

            if(triangle.x1>desiredWidth && triangle.x2>desiredWidth && triangle.x3>desiredWidth  ||  triangle.y1>desiredHeight && triangle.y2>desiredHeight && triangle.y3>desiredHeight){
                continue;
            }


//            Triangle 1
            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - 0.2f * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - 0.2f * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x1;
            newTriangle.y2 = triangle.y1;

            newTriangle.x3 = triangle.x3;
            newTriangle.y3 = triangle.y3;

            newTriangle.middlex = (2f/3f) * newTriangle.x1 +  (1f/3f) * newTriangle.x2;
            newTriangle.middley = (2f/3f) * newTriangle.y1 +  (1f/3f) * newTriangle.y3;

            triangles.add(newTriangle);

//            Triangle 2
            newTriangle = new Triangle();
            newTriangle.x1 = (float) (0.5 * triangle.x1 + 0.5 * (triangle.x3 - 0.2f * (triangle.x3 - triangle.x2)));
            newTriangle.y1 = (float) (0.5 * triangle.y1 + 0.5 * (triangle.y3 - 0.2f * (triangle.y3 - triangle.y2)));

            newTriangle.x2 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y2 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.x3 = triangle.x3 - 0.2f * (triangle.x3 - triangle.x2);
            newTriangle.y3 = triangle.y3 - 0.2f * (triangle.y3 - triangle.y2);

            newTriangle.middlex = (2f/3f) * newTriangle.x1 +  (1f/3f) * newTriangle.x2;
            newTriangle.middley = (2f/3f) * newTriangle.y1 +  (1f/3f) * newTriangle.y3;

            triangles.add(newTriangle);

//            Triangle 3

            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - 0.2f * 3 * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - 0.2f * 3 * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x3 - 0.2f * (triangle.x3 - triangle.x2);
            newTriangle.y2 = triangle.y3 - 0.2f * (triangle.y3 - triangle.y2);

            newTriangle.x3 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y3 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.middlex = (2f/3f) * newTriangle.x1 +  (1f/3f) * newTriangle.x2;
            newTriangle.middley = (2f/3f) * newTriangle.y1 +  (1f/3f) * newTriangle.y3;

            triangles.add(newTriangle);

            //            Triangle 4

            newTriangle = new Triangle();
            newTriangle.x1 = (float) (0.5 * triangle.x1 + 0.5 * (triangle.x3 - 0.2f * (triangle.x3 - triangle.x2)));
            newTriangle.y1 = (float) (0.5 * triangle.y1 + 0.5 * (triangle.y3 - 0.2f * (triangle.y3 - triangle.y2)));

            newTriangle.x2 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y2 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.x3 = triangle.x1;
            newTriangle.y3 = triangle.y1;

            newTriangle.middlex = (2f/3f) * newTriangle.x1 +  (1f/3f) * newTriangle.x2;
            newTriangle.middley = (2f/3f) * newTriangle.y1 +  (1f/3f) * newTriangle.y3;

            triangles.add(newTriangle);

            //            Triangle 5

            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - 0.2f * 3 * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - 0.2f * 3 * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x2;
            newTriangle.y2 = triangle.y2;

            newTriangle.x3 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y3 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.middlex = (2f/3f) * newTriangle.x1 +  (1f/3f) * newTriangle.x2;
            newTriangle.middley = (2f/3f) * newTriangle.y1 +  (1f/3f) * newTriangle.y3;

            triangles.add(newTriangle);


        }

        return triangles;
    }

}
