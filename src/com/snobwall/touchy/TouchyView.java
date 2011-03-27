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
    
    /** 
     * Call from constructor.
     * Initialize some of the custom values from XML.
     * We look at "text", which we will display,
     * and "color", which we draw in the background,
     * and "textColor", which colours the text that we draw.
     */
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
        
        mPaintStyle = new Paint();
        int paintColor = arr.getColor(R.styleable.TouchyView_color, Color.rgb(255, 255, 255));
        Log.d("TouchyView", String.format("Paint color is ARGB=%d, %d, %d, %d",
                Color.alpha(paintColor),
                Color.red(paintColor),
                Color.green(paintColor),
                Color.blue(paintColor)
                ));
        mPaintStyle.setColor(paintColor);
        
        mTextPaintStyle = new Paint();
        mTextPaintStyle.setTextAlign(Paint.Align.LEFT);
        mTextPaintStyle.setAntiAlias(true);
        mTextPaintStyle.setTextSize(mTextPaintStyle.getTextSize() * 3.0f);
        mTextPaintStyle.setTypeface(Typeface.SANS_SERIF);
        paintColor = arr.getColor(R.styleable.TouchyView_textColor, Color.rgb(0, 0, 0));
        mTextPaintStyle.setColor(paintColor);
    }
    
    /**
     * Perform layout of children.
     * 
     * We allocate some space at the top for our text display, and to let our colours
     * shine through. Then we give the rest of the space to our children Ð layering
     * them directly on top of each other if we have more than one. Because we're
     * hardcore.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("TouchyView", String.format("I seem to have %d children.", getChildCount()));
        
        int top = (int)Math.ceil(mTextPaintStyle.getFontSpacing() * 1.5);
        
        for(int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.layout(0, top, r - l, b - t);
        }
    }
    
    /**
     * Just show our label. And our gang colour.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mPaintStyle);
        canvas.drawText(mText, 0, mTextPaintStyle.getFontSpacing(), mTextPaintStyle);
    }

    /**
     * Perform our measurements.
     * 
     * We take up the size that our parent gives us, if it's EXACTLY or AT_MOST.
     * Otherwise we take the suggested minimum width, but never smaller than 100
     * in either dimension.
     * 
     * We trim some amount off the height when calling measureChildren, so that
     * we can reserve some space at the top for our label.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        
        int childWidthMeasureSpec, childHeightMeasureSpec;
        
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMeasureMode == MeasureSpec.UNSPECIFIED) {
            width = Math.max(100, getSuggestedMinimumWidth());
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);

        // Height of top bar with the text label.
        int top = (int)Math.ceil(mTextPaintStyle.getFontSpacing() * 1.5);
        
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMeasureMode == MeasureSpec.UNSPECIFIED) {
            height = Math.max(100, getSuggestedMinimumHeight());
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height - top, MeasureSpec.AT_MOST);
        
        Log.d("TouchyView", String.format("I seem to be of size %dx%d", width, height));
        
        setMeasuredDimension(width, height);
        measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    protected String mText;
    protected Paint mPaintStyle;
    protected Paint mTextPaintStyle;
    
    public String getText() {
        return mText;
    }

    public void setText(String mTag) {
        this.mText = mTag;
        postInvalidate();
    }

    public Paint getPaintStyle() {
        return new Paint(mPaintStyle);
    }

    public void setPaintStyle(Paint paintStyle) {
        this.mPaintStyle = paintStyle;
        postInvalidate();
    }
    
    public Paint getTextPaintStyle() {
        return new Paint(mTextPaintStyle);
    }

    public void setTextPaintStyle(Paint mTextPaintStyle) {
        this.mTextPaintStyle = mTextPaintStyle;
        
        // Changing our text style means we might have a new text height.
        // That means our top bar needs to change size,
        // and also means our children need to move.
        requestLayout();
        postInvalidate();
    }
    
    /**
     * TODO: Let's intercept a touch event sometime.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Do something awesome on a touch event.
     * 
     * For an ACTION_DOWN event, we randomly choose between 3 possibilities here:
     * 
     *  1. Ignore the touch event (and return false).
     *     This will cause our parent's onTouchEvent to be called.
     *     
     *  2. Handle the touch event (and return true).
     *     No one else gets their onTouchEvent called.
     *     We'll popup a dialog box to show what happened.
     *  
     *  3. Call our onClickListener if we have one (and return true),
     *     or return false if we don't have one.
     */
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
