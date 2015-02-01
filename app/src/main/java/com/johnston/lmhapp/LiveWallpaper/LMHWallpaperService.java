package com.johnston.lmhapp.LiveWallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
        ArrayList<Integer> onScreenTraingles;
        ArrayList<Integer> colours;
        int[] color = new int[9];
        Handler drawHandler = new Handler();
        int numberOfTriangleGenerators;
        Canvas workingCanvas;
        Bitmap workingBitmap;

        int width =1000;
        int height =2000;



        Runnable drawRunner = new Runnable(){
            @Override
            public void run(){
                draw();
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
            System.out.println("Engine Started");
        }
        public void generateColours(){
            colours = new ArrayList<>();
            Random rand = new Random();
            for(int i=0;i<triangles.size();i++){
                colours.add(i,rand.nextInt(9));
            }
            checkOnScreen();
            initialDraw();
        }

        public void generateTriangles() {
            drawHandler.removeCallbacks(drawRunner);
            numberOfTriangleGenerators++;
            System.out.println("Generating Triangles");
            int numberOfIterations = 3;
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    numberOfTriangleGenerators--;
                    if(numberOfTriangleGenerators==0){
                        triangles = (ArrayList<Triangle>) message.obj;
                        generateColours();
                    }

                }
            };
            new TriangleGeneratorAsync().execute(width,height,numberOfIterations,handler);
        }

        public void checkOnScreen(){
            Triangle triangle;
            onScreenTraingles = new ArrayList<>();
            for (int i = 0; i < triangles.size(); i++) {
                triangle = triangles.get(i);
                if(triangle.x1<width && triangle.y1<height||triangle.x2<width && triangle.y2<height||triangle.x3<width && triangle.y3<height) {
                    //We must render this triangle
                    onScreenTraingles.add(i);
                }
            }
        }


        public void initialDraw(){
            long startTime = System.currentTimeMillis();
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


            Paint bottomLeftFillPaint1 = new Paint();
            bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
            bottomLeftFillPaint1.setAntiAlias(true);
            Paint bottomLeftStrokePaint = new Paint();
            bottomLeftStrokePaint.setColor(Color.parseColor("#000000"));
            bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
            bottomLeftStrokePaint.setAntiAlias(true);
            bottomLeftStrokePaint.setStrokeWidth(2);

            Triangle triangle;
            int i;
            Random rand = new Random();
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            workingBitmap = Bitmap.createBitmap(width, height, conf);
            workingCanvas = new Canvas(workingBitmap);

            colours.set(onScreenTraingles.get(rand.nextInt(onScreenTraingles.size())),rand.nextInt(9));
            final  SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas =  holder.lockCanvas();
            for (i = 0; i < onScreenTraingles.size(); i++) {
                triangle = triangles.get(onScreenTraingles.get(i));
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(triangle.x1, triangle.y1);
                path.lineTo(triangle.x2, triangle.y2);
                path.lineTo(triangle.x3, triangle.y3);
                path.close();

                bottomLeftFillPaint1.setColor(color[colours.get(onScreenTraingles.get(i))]);
                workingCanvas.drawPath(path, bottomLeftFillPaint1);
                workingCanvas.drawPath(path, bottomLeftStrokePaint);
            }
            holder.unlockCanvasAndPost(workingCanvas);
            drawHandler.postDelayed(drawRunner,100);
            System.out.println(System.currentTimeMillis()-startTime);
            return;
        }

        public void draw(){
            if(workingCanvas==null){
                initialDraw();
            }else{
                final  SurfaceHolder holder = getSurfaceHolder();
                Canvas canvas = holder.lockCanvas();
                long startTime = System.currentTimeMillis();
                Random rand = new Random();
                int changedTriangle = onScreenTraingles.get(rand.nextInt(onScreenTraingles.size()));
                colours.set(changedTriangle,rand.nextInt(9));
                Triangle triangle = triangles.get(changedTriangle);
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(triangle.x1, triangle.y1);
                path.lineTo(triangle.x2, triangle.y2);
                path.lineTo(triangle.x3, triangle.y3);
                path.close();


                Paint bottomLeftFillPaint1 = new Paint();
                bottomLeftFillPaint1.setStyle(Paint.Style.FILL);
                bottomLeftFillPaint1.setAntiAlias(true);
                Paint bottomLeftStrokePaint = new Paint();
                bottomLeftStrokePaint.setColor(Color.parseColor("#000000"));
                bottomLeftStrokePaint.setStyle(Paint.Style.STROKE);
                bottomLeftStrokePaint.setAntiAlias(true);
                bottomLeftStrokePaint.setStrokeWidth(2);

                bottomLeftFillPaint1.setColor(color[colours.get(changedTriangle)]);
                workingCanvas.drawPath(path, bottomLeftFillPaint1);
                workingCanvas.drawPath(path, bottomLeftStrokePaint);
                drawHandler.removeCallbacks(drawRunner);
                drawHandler.postDelayed(drawRunner,100);
                System.out.println(System.currentTimeMillis()-startTime);
                holder.unlockCanvasAndPost(workingCanvas);
            }
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                if(triangles==null){
                    System.out.println("Triangles are null");
                    drawHandler.removeCallbacksAndMessages(null);
                    generateTriangles();
                }else{
                    System.out.println("Engine Initialised with triangles");
                    initialDraw();
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
