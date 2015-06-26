package ftc.nova.camera_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int[] getEdges(String file){
        Bitmap img = BitmapFactory.decodeFile(file);

        //Difference Calc
        int[][] RGB = new int[img.getHeight()*img.getWidth()][3];
        int argb;
        int width = img.getWidth();
        int height = img.getHeight();

        double[] dist = new double[img.getHeight()*img.getWidth()];

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                argb = img.getPixel(x,y);

                RGB[y*width + x][0] = (argb & 0xff0000) >> 16;
                RGB[y*width + x][1] = (argb & 0xff00) >> 8;
                RGB[y*width + x][2] = (argb & 0xff);
            }
        }

        for(int y = 1; y < img.getHeight()-1; y++){
            for(int x = 1; x < img.getWidth()-1; x++){
                dist[y*width + x] = Math.sqrt(Math.pow(RGB[y*width + x][0] - RGB[(y-1)*width + x][0], 2) + Math.pow(RGB[y*width + x][1] - RGB[(y-1)*width + x][1], 2) + Math.pow(RGB[y*width + x][2] - RGB[(y-1)*width + x][2], 2));
                dist[y*width + x] += Math.sqrt(Math.pow(RGB[y*width + x][0] - RGB[y*width + x + 1][0], 2) + Math.pow(RGB[y*width + x][1] - RGB[y*width + x + 1][1], 2) + Math.pow(RGB[y*width + x][2] - RGB[y*width + x + 1][2], 2));
                dist[y*width + x] += Math.sqrt(Math.pow(RGB[y*width + x][0] - RGB[(y+1)*width + x][0], 2) + Math.pow(RGB[y*width + x][1] - RGB[(y+1)*width + x][1], 2) + Math.pow(RGB[y*width + x][2] - RGB[(y+1)*width + x][2], 2));
                dist[y*width + x] += Math.sqrt(Math.pow(RGB[y*width + x][0] - RGB[y*width + x - 1][0], 2) + Math.pow(RGB[y*width + x][1] - RGB[y*width + x - 1][1], 2) + Math.pow(RGB[y*width + x][2] - RGB[y*width + x - 1][2], 2));
                dist[y*width + x] /= 4;
            }
        }

        //Edge detection
        int[] edges = new int[width*height];

        double sum = 0;
        double avg;

        for(int y = 1; y < height - 1; y++){
            for(int x = 1; x < width - 1; x++){
                sum += dist[y*width + x];
            }
        }

        avg = sum/((width-2)*(height-2));


        /*
        Color codes:
        1: blue
        2: red
        3: yellow
        4: white
        5: black
         */
        for(int y = 1; y < height - 1; y++){
            for(int x = 1; x < width - 1; x++){
                if(dist[y*width + x] > avg*1.5){
                    if(RGB[y*width + x][2] > (RGB[y*width + x][0] + RGB[y*width + x][1])*2){
                        edges[y*width + x] = 1;
                    }
                    else if(RGB[y*width + x][0] > (RGB[y*width + x][2] + RGB[y*width + x][1])*2){
                        edges[y*width + x] = 2;
                    }
                    else if(RGB[y*width + x][0] + RGB[y*width + x][1] > RGB[y*width + x][2]*10){
                        edges[y*width + x] = 3;
                    }
                    else if(RGB[y*width + x][0] + RGB[y*width + x][1] + RGB[y*width + x][2] > 240*3){
                        edges[y*width + x] = 4;
                    }
                    else if(RGB[y*width + x][0] < 50 && RGB[y*width + x][1] < 50 && RGB[y*width + x][2] < 50){
                        edges[y*width + x] = 5;
                    }
                    else{
                        edges[y*width + x] = 0;
                    }
                }
                else{
                    edges[y*width + x] = -1;
                }
            }
        }

        return edges;
    }

    public void Snap(View view)
    {
        int[] edges = getEdges("/storage/emulated/0/Download/lines.png");
        Bitmap img = BitmapFactory.decodeFile("/storage/emulated/0/Download/lines.png");
        int width = img.getWidth();
        int height = img.getHeight();

        Bitmap out_img = img.copy(Bitmap.Config.ARGB_8888, true);

        for(int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if(edges[y*width + x] == 1){
                    out_img.setPixel(x,y, 0xff00ff00);
                    System.out.println("set pixel: (" + x + "," + y + "), ID: " + edges[y*width + x]);
                }
            }
        }

        try{
            String filePath = Environment.getExternalStorageDirectory() + File.separator + "lines_mod.png";
            FileOutputStream fos = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fos);

            out_img.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /** picture call back */
    /*Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            FileOutputStream outStream = null;
            try {
                String dir_path = Environment.getExternalStorageState().toString();// set your directory path here
                outStream = new FileOutputStream(dir_path + "/pictures123/pic.jpg");
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                camera.stopPreview();
                camera.release();
                camera = null;
                Toast.makeText(getApplicationContext(), "Image snapshot Done",Toast.LENGTH_LONG).show();


            }
        }
    };*/
}
