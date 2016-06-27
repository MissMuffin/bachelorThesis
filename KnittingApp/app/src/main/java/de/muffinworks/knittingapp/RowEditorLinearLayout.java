package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorLinearLayout extends LinearLayout {

    LineNumberTextView lineNumbers;
    KeyboardlessEditText2 editText;
    int lines; //first line starts at 0

    //scale
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    //Scroll
    private boolean mIsScrolling = false;

    public RowEditorLinearLayout(Context context) {
        super(context);
    }

    public RowEditorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.row_editor, this, true);

        lineNumbers = (LineNumberTextView) findViewById(R.id.row_editor_line_numbers);
        editText = (KeyboardlessEditText2) findViewById(R.id.row_editor_edit_text);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void initLineNumbers() {
        lineNumbers.initLineNumbers(editText.getLineCount());
    }

    public void incrementLineNumber() {
        lineNumbers.incrementLineNumber();
    }

    public void decrementLineNumber() {
        lineNumbers.decrementLineNumber();
    }

    public KeyboardlessEditText2 getEditText() {
        return editText;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //let ScaleGestureDetector handle event
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScaleFactor, mScaleFactor);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        //case that touch gesture is complete
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            //release scroll
            mIsScrolling = false;
            //let children handle touch event, do not intercept it
            return false;
        }

        switch(action) {
            case MotionEvent.ACTION_MOVE: {
                if (mIsScrolling) {
                    //currently scrolling: interept touch event
                    return true;
                }
                //if horizontal finger drag exceeded touch slop, start scroll
//                final int xDiff = calculateDistanceX(ev);

                //calculate touch slop using ViewConfiguration constants
//                if (xDiff > mTouchSlop) {
                    //start scrolling
                    mIsScrolling = true;
                    return true;
//                }
//                break;
            }
        }
        //generally don't handle touch events, should be handled by child view
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            //restrict zoom levels
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 2.0f));
            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
