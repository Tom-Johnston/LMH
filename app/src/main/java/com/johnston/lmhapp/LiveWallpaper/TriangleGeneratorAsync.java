package com.johnston.lmhapp.LiveWallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Handler;

import com.johnston.lmhapp.Triangle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tom on 05/11/2014.
 */
public class TriangleGeneratorAsync extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        System.out.println("Generate Triangles");
        int sizex = (Integer) params[0];
        int sizey = (Integer) params[1];
        int numberOfIterationsToDo = (int) params[2];
        Handler handler = (Handler) params[3];

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
            triangles = NextIteration(triangles);
        }
        handler.obtainMessage(0,triangles).sendToTarget();
        System.out.println("Finished Generating Triangles");
        return null;

    }

    public ArrayList<Triangle> NextIteration(ArrayList<Triangle> passedTriangles) {
        ArrayList<Triangle> triangles = new ArrayList<>();
        float oneOverSquareRoot5 = 0.4472135955f;
        Triangle newTriangle;
        int numberOfTriangles = passedTriangles.size();
        Triangle triangle;
        for (int i = 0; i < numberOfTriangles; i++) {

            triangle = passedTriangles.get(i);


//            Triangle 1
            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x1;
            newTriangle.y2 = triangle.y1;

            newTriangle.x3 = triangle.x3;
            newTriangle.y3 = triangle.y3;

            triangles.add(newTriangle);

//            Triangle 2
            newTriangle = new Triangle();
            newTriangle.x1 = (float) (0.5 * triangle.x1 + 0.5 * (triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.x3 - triangle.x2)));
            newTriangle.y1 = (float) (0.5 * triangle.y1 + 0.5 * (triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.y3 - triangle.y2)));

            newTriangle.x2 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y2 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.x3 = triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.x3 - triangle.x2);
            newTriangle.y3 = triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.y3 - triangle.y2);

            triangles.add(newTriangle);

//            Triangle 3

            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * 3 * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * 3 * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.x3 - triangle.x2);
            newTriangle.y2 = triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.y3 - triangle.y2);

            newTriangle.x3 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y3 = (float) (0.5 * (triangle.y1 + triangle.y2));

            triangles.add(newTriangle);

            //            Triangle 4

            newTriangle = new Triangle();
            newTriangle.x1 = (float) (0.5 * triangle.x1 + 0.5 * (triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.x3 - triangle.x2)));
            newTriangle.y1 = (float) (0.5 * triangle.y1 + 0.5 * (triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * (triangle.y3 - triangle.y2)));

            newTriangle.x2 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y2 = (float) (0.5 * (triangle.y1 + triangle.y2));

            newTriangle.x3 = triangle.x1;
            newTriangle.y3 = triangle.y1;

            triangles.add(newTriangle);

            //            Triangle 5

            newTriangle = new Triangle();
            newTriangle.x1 = triangle.x3 - oneOverSquareRoot5 * oneOverSquareRoot5 * 3 * (triangle.x3 - triangle.x2);
            newTriangle.y1 = triangle.y3 - oneOverSquareRoot5 * oneOverSquareRoot5 * 3 * (triangle.y3 - triangle.y2);

            newTriangle.x2 = triangle.x2;
            newTriangle.y2 = triangle.y2;

            newTriangle.x3 = (float) (0.5 * (triangle.x1 + triangle.x2));
            newTriangle.y3 = (float) (0.5 * (triangle.y1 + triangle.y2));

            triangles.add(newTriangle);


        }

        return triangles;
    }
}
