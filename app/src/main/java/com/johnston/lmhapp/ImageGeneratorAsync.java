package com.johnston.lmhapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Handler;

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
public class ImageGeneratorAsync extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        Context context = (Context) params[4];
        String username = (String) params[0];
        Handler handler = (Handler) params[1];
        Boolean background = (Boolean) params[5];
        if (username.length() > 4) {
//            Get rid of the lady number to make the differences more obvious.
            username = username.substring(4);
        }

        int sizex = (Integer) params[2];
        int sizey = (Integer) params[3];
        if (sizex > 2 * sizey) {
//            Scale up y to match sizex;
            sizey = sizex / 2;
        } else {
//            Scale up x to match y
            sizex = 2 * sizey;
        }

        int numberOfIterationsToDo = 3;
        if (background) {
            numberOfIterationsToDo = 5;
        }

        int[] color = new int[9];
        color[0] = (Color.parseColor("#BA68C8"));
        color[1] = (Color.parseColor("#7986CB"));
        color[2] = (Color.parseColor("#64B5F6"));
        color[3] = (Color.parseColor("#4DB6AC"));
        color[4] = (Color.parseColor("#81C784"));
        color[5] = (Color.parseColor("#DCE775"));
        color[6] = (Color.parseColor("#FFD54F"));
        color[7] = (Color.parseColor("#E0E0E0"));
        color[8] = (Color.parseColor("#90A4AE"));


//        Generate a string to switch colors
        String binary = "0";

        for (int i = 0; i < username.length(); i++) {
            binary = binary + (Integer.toBinaryString(username.charAt(i)));
        }
        int necessaryBinaryLength = (int) (2 * Math.pow(5, numberOfIterationsToDo));
        int binaryLength = binary.length();
        if (necessaryBinaryLength > binary.length()) {
            int binaryInt;
            if (binary.length() > 32) {
                binaryInt = Integer.parseInt(binary.substring(0, 31), 2);
            } else {
                binaryInt = Integer.parseInt(binary, 2);
            }
            Random random = new Random(binaryInt);
            for (int i = 0; i < necessaryBinaryLength - binaryLength; i++) {
                if (random.nextBoolean()) {
                    binary = binary + "1";
                } else {
                    binary = binary + "0";
                }
            }
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

        if (background) {
            handler.obtainMessage(0, paintBackgroundLMH(triangles, binary, sizex, sizey)).sendToTarget();
            return null;
        }
        Bitmap bmp = paint(triangles, binary, sizex, sizey, color);

//      Flip the matrix to

        Matrix m = new Matrix();
        m.preScale(1, -1);
        Bitmap dst = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);

//        Save the image so I don't have to do this all the time.
        try {
            File file = new File(context.getFilesDir(), "CustomGraphic.png");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream stream = new BufferedOutputStream(fos);
            dst.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        handler.obtainMessage(0, dst).sendToTarget();
        return null;
    }


    public Bitmap paintBackgroundLMH(ArrayList<Triangle> triangles, String binary, int sizex, int sizey){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(sizex, sizey, conf);
        Canvas c = new Canvas(bmp);

        Paint bottomLeftFillPaint1 = new Paint();
        bottomLeftFillPaint1.setColor(Color.parseColor("#002147"));
        bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
        bottomLeftFillPaint1.setAntiAlias(true);
        Paint bottomLeftFillPaint2 = new Paint();
        bottomLeftFillPaint2.setColor(Color.parseColor("#001123"));
        bottomLeftFillPaint2.setStyle(Paint.Style.FILL);
        Paint bottomLeftStrokePaint = new Paint();
        bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
        bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
        bottomLeftStrokePaint.setAntiAlias(true);
        bottomLeftStrokePaint.setStrokeWidth(2);

        Triangle triangle;
        int i;
        for (i = 0; i < triangles.size(); i++) {
            triangle = triangles.get(i);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(triangle.x1, triangle.y1);
            path.lineTo(triangle.x2, triangle.y2);
            path.lineTo(triangle.x3, triangle.y3);
            path.close();

            System.out.println(triangle.middley);
            if (triangle.middley<300) {
                c.drawPath(path, bottomLeftFillPaint1);
                c.drawPath(path, bottomLeftStrokePaint);
            }

        }
        return bmp;
    }

    public Bitmap paintBackground(ArrayList<Triangle> triangles, String binary, int sizex, int sizey) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(sizex, sizey, conf);
        Canvas c = new Canvas(bmp);

        Paint bottomLeftFillPaint1 = new Paint();
        bottomLeftFillPaint1.setColor(Color.parseColor("#002147"));
        bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
        bottomLeftFillPaint1.setAntiAlias(true);
        Paint bottomLeftFillPaint2 = new Paint();
        bottomLeftFillPaint2.setColor(Color.parseColor("#001123"));
        bottomLeftFillPaint2.setStyle(Paint.Style.FILL);
        Paint bottomLeftStrokePaint = new Paint();
        bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
        bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
        bottomLeftStrokePaint.setAntiAlias(true);
        bottomLeftStrokePaint.setStrokeWidth(2);

        Triangle triangle;
        int i;
        for (i = 0; i < triangles.size(); i++) {
            triangle = triangles.get(i);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(triangle.x1, triangle.y1);
            path.lineTo(triangle.x2, triangle.y2);
            path.lineTo(triangle.x3, triangle.y3);
            path.close();

            if (binary.charAt(i) == '1') {
                c.drawPath(path, bottomLeftFillPaint2);
            } else {
                c.drawPath(path, bottomLeftFillPaint1);
            }
            c.drawPath(path, bottomLeftStrokePaint);
        }
        return bmp;
    }

    public Bitmap paint(ArrayList<Triangle> triangles, String binary, int sizex, int sizey, int[] color) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(sizex, sizey, conf);
        Canvas c = new Canvas(bmp);

        Paint bottomLeftFillPaint1 = new Paint();
        bottomLeftFillPaint1.setColor(Color.parseColor("#002147"));
        bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
        bottomLeftFillPaint1.setAntiAlias(true);
        Paint bottomLeftFillPaint2 = new Paint();
        bottomLeftFillPaint2.setColor(Color.parseColor("#001123"));
        bottomLeftFillPaint2.setStyle(Paint.Style.FILL);
        Paint bottomLeftStrokePaint = new Paint();
        bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
        bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
        bottomLeftStrokePaint.setAntiAlias(true);
        bottomLeftStrokePaint.setStrokeWidth(2);

        Triangle triangle;
        int i;
        for (i = 0; i < triangles.size() / 2; i++) {
            triangle = triangles.get(i);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(triangle.x1, triangle.y1);
            path.lineTo(triangle.x2, triangle.y2);
            path.lineTo(triangle.x3, triangle.y3);
            path.close();

            if (binary.charAt(i) == '1') {
                c.drawPath(path, bottomLeftFillPaint2);
            } else {
                c.drawPath(path, bottomLeftFillPaint1);
            }
            c.drawPath(path, bottomLeftStrokePaint);

        }

        Paint strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(4);
        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color[0]);

        for (i = triangles.size() / 2; i < triangles.size(); i++) {
            if (binary.charAt(i) == '1') {
                fillPaint.setColor(color[i % 9]);
            }

            triangle = triangles.get(i);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(triangle.x1, triangle.y1);
            path.lineTo(triangle.x2, triangle.y2);
            path.lineTo(triangle.x3, triangle.y3);
//            System.out.println(triangle.x1 + "//" + triangle.y1 + "??" + triangle.x2 + "//" + triangle.y2 + "??" + triangle.x3 + "//" + triangle.y3);
            path.close();

            c.drawPath(path, fillPaint);
            c.drawPath(path, strokePaint);
        }
        return bmp;
    }

    public ArrayList<Triangle> NextIteration(ArrayList<Triangle> passedTriangles) {
        ArrayList<Triangle> triangles = new ArrayList<>();
        Triangle newTriangle;
        int numberOfTriangles = passedTriangles.size();
        Triangle triangle;
        for (int i = 0; i < numberOfTriangles; i++) {

            triangle = passedTriangles.get(i);


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
