package com.praaktis.exerciseengine.RawPlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.praaktis.exerciseengine.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final Button startButton =  (Button) findViewById(R.id.buttonStart);
        final AppCompatActivity thisActivity = this;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCounter++;
                Intent intent = new Intent(thisActivity, H264RawPlayerActivity.class);
                intent.putExtra("FILE_NAME","video1.h264");
                intent.putExtra("CAPTIONS", "|Some val #1|0.1|0.1|Some val #2|.1|.15|@0|.1|.20");
                intent.putExtra("CAPTION_VALS", new Object[] {2, 1, .12, "ok", 20, .14, ""});
                startActivity(intent);
            }
        });

    }
}
