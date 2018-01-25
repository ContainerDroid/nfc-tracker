package com.eden.apps.nfctracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mHubDevice = null;
    Button mReaderDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHubDevice = findViewById(R.id.welcome_setup_hub);
        mReaderDevice = findViewById(R.id.welcome_setup_reader);

        mReaderDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
                startActivity(intent);
            }
        });

        mHubDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), HubActivity.class);
                startActivity(intent);
            }
        });
    }
}
