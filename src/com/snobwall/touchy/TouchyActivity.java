package com.snobwall.touchy;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Paint;
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
        
        // Put a click listener on TouchyView1 (outermost). Sometimes it'll call this,
        // which it has to do explicitly in onTouchEvent (at least, if it has
        // overridden onTouchEvent).
        v = findViewById(R.id.TouchyView1);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(TouchyActivity.this)
                    .setTitle("Click listener")
                    .setMessage("I received a click from TouchyView1.")
                    .create().show();
                
                // Grab TouchyView2 and make it a bit bigger.
                TouchyView who = (TouchyView)findViewById(R.id.TouchyView2);
                Paint textStyle = who.getTextPaintStyle();
                textStyle.setTextSize(textStyle.getTextSize() * 1.15f);
                who.setTextPaintStyle(textStyle);
            }
            
        });

        // Put a click listener on the frame layout holding everything.
        // This'll get called if the click isn't handled by anything
        // in the view hierarchy that's also involved with the touch.
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