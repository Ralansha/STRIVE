package com.example.appblocker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
public class MainActivity extends AppCompatActivity {

    Button buttonAddWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPermission();
        buttonAddWidget = (Button) findViewById(R.id.openWidget);
        EditText time = (EditText) findViewById(R.id.time);
        SharedPreferences sh = getSharedPreferences("MyTimer", Context.MODE_PRIVATE);
        long timeStudied =sh.getLong("Study Time", 0);
        TextView ts = (TextView) findViewById(R.id.tss);
        TextView hrs = (TextView) findViewById(R.id.hrs);
        long hours = 0;
        while(timeStudied >= 60){
            hours +=1;
            timeStudied -=60;
        }

        ts.setText(Long.toString(timeStudied) + " Minutes");
        hrs.setText(Long.toString(hours) + " Hours");
        buttonAddWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Settings.canDrawOverlays(MainActivity.this)){
                    getPermission();
                }else{
                    Intent intent = new Intent(MainActivity.this,WidgetService.class);
                    String num =time.getText().toString();
                    intent.putExtra("time", num);
                    startService(intent);
                }
            }
        });
    }

    public void getPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(!Settings.canDrawOverlays(MainActivity.this)){
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}