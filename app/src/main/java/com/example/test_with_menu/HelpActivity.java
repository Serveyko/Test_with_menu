package com.example.test_with_menu;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.example.test_with_menu.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class HelpActivity extends AppCompatActivity {

    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        close = findViewById(R.id.close);

        close.setOnClickListener(
                btnClick
        );
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close: {
                    Intent helpIntent = new Intent(HelpActivity.this, MainActivity.class);
                    startActivity(helpIntent);
                    break;
                }
            }
        }
    };


}