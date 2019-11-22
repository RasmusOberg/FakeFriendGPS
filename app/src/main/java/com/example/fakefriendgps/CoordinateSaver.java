package com.example.fakefriendgps;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CoordinateSaver extends Thread {
    private static final String TAG = "CoordinateSaver";
    private Context context;
    private double latitude, longitude;

    public CoordinateSaver(Context context, double latitude, double longitude){
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void run() {
        while(true) {
            File direc = context.getFilesDir();
            File file = new File(direc, "location.txt");
            String loc = latitude + ", " + latitude;
            try {
                OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("location.txt", Context.MODE_PRIVATE));
                if(loc == null){
                    loc = "Lat: NaN, Lng: NaN";
                }
                writer.write(loc);
                writer.close();
                Log.d(TAG, "run: Done!");
                Thread.sleep(30000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
