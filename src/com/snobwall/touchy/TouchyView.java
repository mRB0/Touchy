package com.snobwall.touchy;

import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TouchyView extends ViewGroup {

    public TouchyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    public TouchyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public TouchyView(Context context) {
        super(context);
        setup(context, null);
    }
    
    protected void setup(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TouchyView);
        CharSequence s = arr.getString(R.styleable.TouchyView_text);
        if (s != null) {
            mText = s.toString();
            Log.d("TouchyView", String.format("Looks like my text is %s!", s));
        } else {
            mText = "";
        }
        
        paintStyle = new Paint();
        int paintColor = arr.getColor(R.styleable.TouchyView_color, Color.rgb(255, 255, 255));
        Log.d("TouchyView", String.format("Paint color is ARGB=%d, %d, %d, %d",
                Color.alpha(paintColor),
                Color.red(paintColor),
                Color.green(paintColor),
                Color.blue(paintColor)
                ));
        paintStyle.setColor(paintColor);
        
        textPaintStyle = new Paint();
        textPaintStyle.setTextAlign(Paint.Align.LEFT);
        textPaintStyle.setAntiAlias(true);
        textPaintStyle.setTextSize(textPaintStyle.getTextSize() * 3.0f);
        textPaintStyle.setTypeface(Typeface.SANS_SERIF);
        paintColor = arr.getColor(R.styleable.TouchyView_textColor, Color.rgb(0, 0, 0));
        textPaintStyle.setColor(paintColor);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("TouchyView", String.format("I seem to have %d children.", getChildCount()));
        
        int top = (int)Math.ceil(textPaintStyle.getFontSpacing() * 1.5);
        
        for(int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.layout(0, top, r - l, b - t);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(paintStyle);
        canvas.drawText(mText, 0, textPaintStyle.getFontSpacing(), textPaintStyle);
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMeasureMode == MeasureSpec.UNSPECIFIED) {
            width = 100;
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMeasureMode == MeasureSpec.UNSPECIFIED) {
            height = 100;
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        
        Log.d("TouchyView", String.format("I seem to be of size %dx%d", width, height));
        
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        
        setMeasuredDimension(width, height);
    }

    protected Rect where = null;
    protected String mText;
    protected Paint paintStyle;
    protected Paint textPaintStyle;

    public String getText() {
        return mText;
    }

    public void setText(String mTag) {
        this.mText = mTag;
    }
    

    public Paint getPaintStyle() {
        return paintStyle;
    }

    public void setPaintStyle(Paint paintStyle) {
        this.paintStyle = paintStyle;
        invalidate();
    }

    
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Random rnd = new Random();
            int handle = rnd.nextInt(3);
            
            String action = "foo";
            switch(handle) {
            case 0:
                action = "ignore it completely";
                break;
            case 1:
                action = "handle it myself";
                break;
            case 2:
                action = "give it to my onClickListener. If I have one";
                break;
            }
            
            Log.d("TouchyView", String.format("This is %s. I got a touch, and I'm going to %s.", mText, action));
            
            if (handle == 1) {
                new AlertDialog.Builder(getContext())
                    .setTitle("Handled!")
                    .setMessage(String.format("Hey, this is %s and I handled a touch event.", mText))
                    .create().show();
            } else if (handle == 2) {
                return performClick();
            }
            
            return (handle != 0);
        } else { 
            Log.d("TouchyView", String.format("This is %s, and I got a non-down event. I hate those!", mText));
            
            return false; 
        }
    }
    
    
}
