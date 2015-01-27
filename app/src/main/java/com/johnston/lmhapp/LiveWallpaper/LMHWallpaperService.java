package com.johnston.lmhapp.LiveWallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.johnston.lmhapp.Triangle;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tom on 18/01/2015.
 */
public class LMHWallpaperService extends android.service.wallpaper.WallpaperService {

    //    This isn't even close to finished but I have fixed a few bugs in development and will commit this code.
    @Override
    public Engine onCreateEngine() {
        return new LMHWallpaperEngine();
    }



    public class LMHWallpaperEngine extends Engine{
        ArrayList<Triangle> triangles;
        ArrayList<Integer> onScreenTriangles;
        ArrayList<Integer> targetColours;
        ArrayList<Integer> currentColours;
        int[] color = new int[9];
        Handler drawHandler = new Handler();
        int numberOfTriangleGenerators;
        Paint bottomLeftFillPaint1 = new Paint();
        Paint bottomLeftStrokePaint = new Paint();
        Random rand = new Random();


        int width =1000;
        int height =2000;



        Runnable drawRunner = new Runnable(){
            @Override
            public void run(){
                initialDraw();
            }
        };

        public LMHWallpaperEngine() {
            color[0] = (Color.parseColor("#BA68C8"));
            color[1] = (Color.parseColor("#7986CB"));
            color[2] = (Color.parseColor("#64B5F6"));
            color[3] = (Color.parseColor("#4DB6AC"));
            color[4] = (Color.parseColor("#81C784"));
            color[5] = (Color.parseColor("#DCE775"));
            color[6] = (Color.parseColor("#FFD54F"));
            color[7] = (Color.parseColor("#E0E0E0"));
            color[8] = (Color.parseColor("#90A4AE"));
            bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
            bottomLeftFillPaint1.setAntiAlias(true);
            bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
            bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
            bottomLeftStrokePaint.setAntiAlias(true);
            bottomLeftStrokePaint.setStrokeWidth(1);
            System.out.println("Engine Started");

        }

        public void generateTriangles() {
            drawHandler.removeCallbacks(drawRunner);
            numberOfTriangleGenerators++;
            System.out.println("Generating Triangles");
            int numberOfIterations = 5;
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    if(message.what==0){
                        numberOfTriangleGenerators--;
                        System.out.println(numberOfTriangleGenerators);
                    }
                    if(numberOfTriangleGenerators==0){
                        if(message.what==0){
                            triangles = (ArrayList<Triangle>) message.obj;
                        }else{
                            targetColours = (ArrayList<Integer>) message.obj;
                            System.out.println("Got target colours");
                            generateRandomColours();
                            checkOnScreen();
                            initialDraw();
                        }
                    }

                }
            };
            new TriangleGeneratorAsync().execute(width,height,numberOfIterations,handler,getApplicationContext());
        }

        public void generateRandomColours() {
            Random random = new Random();
            currentColours = new ArrayList<>();
            for (int i = 0; i < triangles.size(); i++) {
                currentColours.add(color[random.nextInt(9)]);
            }
            System.out.println("Generated Random Colours");
        }

        public void checkOnScreen(){
            Triangle triangle;
            onScreenTriangles = new ArrayList<>();
            for (int i = 0; i < triangles.size(); i++) {
                triangle = triangles.get(i);
                if(triangle.x1<width && triangle.y1<height||triangle.x2<width && triangle.y2<height||triangle.x3<width && triangle.y3<height) {
                    //We must render this triangle
                    onScreenTriangles.add(i);
                }
            }
            System.out.println("Checked for on screen triangles");
        }


        public Rect computeDirtyRegion(Triangle triangle){
            int minX = (int) Math.min(Math.min(triangle.x1,triangle.x2),triangle.x3);
            int maxX = (int) Math.max(Math.max(triangle.x1,triangle.x2),triangle.x3)+1;
            int minY = (int) Math.min(Math.min(triangle.y1,triangle.y2),triangle.y3);
            int maxY = (int) Math.max(Math.max(triangle.y1,triangle.y2),triangle.y3)+1;
            return new Rect(minX,minY,maxX,maxY);

        }

        public void initialDraw(){
            long startTime = System.currentTimeMillis();

            bottomLeftStrokePaint.setColor(Color.parseColor("#000000"));

            Triangle triangle;
            int i;

            int changedTriangle = onScreenTriangles.get(rand.nextInt(onScreenTriangles.size()));
            currentColours.set(changedTriangle,targetColours.get(changedTriangle));

            final  SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas =  holder.lockCanvas();
            for (i = 0; i < onScreenTriangles.size(); i++) {
                triangle = triangles.get(onScreenTriangles.get(i));
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(triangle.x1, triangle.y1);
                path.lineTo(triangle.x2, triangle.y2);
                path.lineTo(triangle.x3, triangle.y3);
                path.close();

                    bottomLeftFillPaint1.setColor(currentColours.get(onScreenTriangles.get(i)));
                    canvas.drawPath(path, bottomLeftFillPaint1);
                    canvas.drawPath(path, bottomLeftStrokePaint);
            }
            holder.unlockCanvasAndPost(canvas);
            drawHandler.postDelayed(drawRunner,100);
            System.out.println("Initial Draw");
            System.out.println(System.currentTimeMillis()-startTime);
            return;
        }

//        public void draw(){
//            if(workingCanvas==null){
//                initialDraw();
//            }else{
//                showFrameTime++;
//                startTime = System.currentTimeMillis();
//                int changedTriangle = onScreenTriangles.get(rand.nextInt(onScreenTriangles.size()));
//                currentColours.set(changedTriangle,targetColours.get(changedTriangle));
//
//                triangle = triangles.get(changedTriangle);
//                path.setFillType(Path.FillType.EVEN_ODD);
//                path.moveTo(triangle.x1, triangle.y1);
//                path.lineTo(triangle.x2, triangle.y2);
//                path.lineTo(triangle.x3, triangle.y3);
//                path.close();
//                final  SurfaceHolder holder = getSurfaceHolder();
//                Canvas canvas = holder.lockCanvas();
//                canvas.drawBitmap(workingBitmap,0,0,null);
//                canvas.setBitmap(workingBitmap);
//                System.out.println("Start");
//                System.out.println(System.currentTimeMillis()-startTime);
//
//                if(currentColours.get(changedTriangle)==-1){
//                    bottomLeftFillPaint1.setColor(Color.WHITE);
//                    bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
//                }else if(targetColours.get(changedTriangle)==Color.parseColor("#ffffff")){
//                    bottomLeftFillPaint1.setColor(Color.parseColor("#ffffff"));
//                    bottomLeftStrokePaint.setColor(Color.parseColor("#ffffff"));
//                }else{
//                    bottomLeftFillPaint1.setColor(Color.parseColor("#002147"));
//                    bottomLeftStrokePaint.setColor(Color.parseColor("#003D81"));
//                }
//                System.out.println(System.currentTimeMillis()-startTime);
//
//                canvas.drawPath(path, bottomLeftFillPaint1);
//                canvas.drawPath(path, bottomLeftStrokePaint);
//                System.out.println(System.currentTimeMillis() - startTime);
//                holder.unlockCanvasAndPost(canvas);
//                drawHandler.removeCallbacks(drawRunner);
//                drawHandler.postDelayed(drawRunner,50);
//
//
//                System.out.println(System.currentTimeMillis()-startTime);
//
//            }
//        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                if(triangles==null){
                    System.out.println("Triangles are null");
                    drawHandler.removeCallbacksAndMessages(null);
                    generateTriangles();
                }else{
                    System.out.println("Engine Initialised with triangles");
                }
            } else {
                drawHandler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            this.width = width;
            this.height = height;
            drawHandler.removeCallbacks(drawRunner);
            drawHandler.removeCallbacksAndMessages(null);
            generateTriangles();
        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            drawHandler.removeCallbacks(drawRunner);


        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            drawHandler.removeCallbacks(drawRunner);


        }

    }
}
