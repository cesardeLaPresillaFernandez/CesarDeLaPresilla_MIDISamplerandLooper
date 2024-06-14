package com.example.nativemidi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityMenu extends AppCompatActivity implements View.OnClickListener {
    Button botonSampler,botonLooper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button botonSampler= (Button) findViewById(R.id.btpad);
        Button botonLooper= (Button) findViewById(R.id.btlooper);
        botonSampler.setOnClickListener(this);
        botonLooper.setOnClickListener(this);
        botonSampler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Start ActivitySampler
                Intent intent = new Intent(view.getContext(), ActivitySampler.class);
                startActivity(intent);
            }
        });
        botonLooper.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Start LooperActivity
                Intent intent = new Intent(view.getContext(), LooperActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onClick(View view) {

    }
}