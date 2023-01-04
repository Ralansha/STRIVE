package com.example.appblocker;

import static android.content.Intent.getIntent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class WidgetService extends Service {

    int LAYOUT_FLAG;
    View floatingView;
    WindowManager windowManager;
    ImageView iv;
    TextView timer;
    float height,width;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String tme = intent.getStringExtra("time");
        int totalTime = Integer.parseInt(tme);
         String startTimeSTR = new SimpleDateFormat("HH:mm:ss").format(new Date());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
    LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        floatingView = LayoutInflater.from(this).inflate(R.layout.widget,null);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 0;

        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140,140,LAYOUT_FLAG,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        imageParams.gravity = Gravity.BOTTOM|Gravity.CENTER;
        imageParams.y = 0;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        iv = new ImageView(this);
        iv.setImageResource(R.drawable.baseline_highlight_off_24);
        iv.setVisibility(View.INVISIBLE);
        windowManager.addView(iv,imageParams);
        windowManager.addView(floatingView,layoutParams);
        floatingView.setVisibility(View.VISIBLE);
        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();
        timer = (TextView) floatingView.findViewById(R.id.timer2);
        TextView waitTime = (TextView) floatingView.findViewById(R.id.waitTime);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//            timer.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String currentTimeSTR = new SimpleDateFormat("HH:mm:ss").format(new Date());
                LocalTime startTime = LocalTime.parse(startTimeSTR, timeFormatter);
                LocalTime stopTime = LocalTime.parse(currentTimeSTR, timeFormatter);
                Duration difference = Duration.between(startTime, stopTime);
                long seconds = difference.getSeconds();
                long minutes = 0;
                long hours = 0;
                while(seconds >= 60){
                    minutes++;
                    seconds-=60;
                }
                while(minutes >= 60){
                    hours++;
                    minutes-=60;
                }
                if(minutes == totalTime){
                    SharedPreferences sharedPreferences = getSharedPreferences("MyTimer",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    long timeStudied =sharedPreferences.getLong("Study Time", 0);
                    boolean check = sharedPreferences.getBoolean("check1",false);
                    while(hours >= 1){
                        minutes += 60;
                        hours -=1;
                    }
                    while(seconds >= 60){
                        minutes++;
                        seconds-=60;
                    }

                    minutes+= timeStudied;

                    if(!check) {
                        Log.d("MyTime", "P1: ");
                        Log.d("MyTime", "Minutes: " + Long.toString(minutes));
                        Log.d("MyTime", "Time Studies: " + Long.toString(timeStudied));
                        myEdit.putLong("Study Time",minutes );
                        myEdit.putBoolean("check1",true );
                        myEdit.commit();
                    }else{
                        Log.d("MyTime", "P2: ");
                        myEdit.putBoolean("check1",false );
                        myEdit.commit();
                    }
                    windowManager.removeView(floatingView);
                }
                waitTime.setText("Total Time To Wait : " + totalTime + " Minutes");
                timer.setText(hours+" Hours "+minutes+" Minutes "+Long.toString(seconds) + " Seconds");
            handler.postDelayed(this,1000);
            }
        },10);

        return START_STICKY;
    }



}
