package com.snobwall.touchy;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TouchyActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touchy);
        
        View v;
        
        v = findViewById(R.id.TouchyView1);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(TouchyActivity.this)
                    .setTitle("Click listener")
                    .setMessage("I got a lost click from TouchyView1.")
                    .create().show();
            }
            
        });

        v = findViewById(R.id.frameLayout1);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(TouchyActivity.this)
                    .setTitle("Lazy asses")
                    .setMessage("No one handled that touch event.")
                    .create().show();
            }
            
        });
    
    }
}