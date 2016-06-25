package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorLinearLayout extends LinearLayout {

    TextView lineNumbers;
    KeyboardlessEditText2 editText;
    int lines; //number of lines - 1

    public RowEditorLinearLayout(Context context) {
        super(context);
    }

    public RowEditorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(HORIZONTAL);

        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.row_editor, this, true);

        lineNumbers = (TextView) findViewById(R.id.row_editor_line_numbers);
        editText = (KeyboardlessEditText2) findViewById(R.id.row_editor_edit_text);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight

//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//        mTranslateMatrix.setTranslate(0, 0);
//        mScaleMatrix.setScale(1, 1);
    }

    public void initLineNumbers() {
        lines = editText.getLineCount();
        String linesString = "1";

        for(int i = 1; i < lines; i++) {
            linesString += "\n" + (i+1);
        }

        lineNumbers.setText(linesString);
    }

    public void incrementLineNumber() {
        lines = editText.getLineCount();
        String currentLineNumbers = lineNumbers.getText().toString();
        String newLineNumbers = "";
        newLineNumbers = currentLineNumbers + "\n" + (lines);
        lineNumbers.setText(newLineNumbers);
    }

    public void decrementLineNumber() {
        lines = editText.getLineCount();
        String currentLineNumbers = lineNumbers.getText().toString();
        String newLineNumbers = "";
        int lastLine = currentLineNumbers.lastIndexOf("\n");
        newLineNumbers = currentLineNumbers.substring(0, lastLine);
        lineNumbers.setText(newLineNumbers);
    }

    public KeyboardlessEditText2 getEditText() {
        return editText;
    }

//    private static final int INVALID_POINTER_ID = 1;
//    private int mActivePointerId = INVALID_POINTER_ID;
//
//    private float mScaleFactor = 1;
//    private ScaleGestureDetector mScaleDetector;
//    private Matrix mScaleMatrix = new Matrix();
//    private Matrix mScaleMatrixInverse = new Matrix();
//
//    private float mPosX;
//    private float mPosY;
//    private Matrix mTranslateMatrix = new Matrix();
//    private Matrix mTranslateMatrixInverse = new Matrix();
//
//    private float mLastTouchX;
//    private float mLastTouchY;
//
//    private float mFocusY;
//
//    private float mFocusX;
//
//    private float[] mInvalidateWorkingArray = new float[6];
//    private float[] mDispatchTouchEventWorkingArray = new float[2];
//    private float[] mOnTouchEventWorkingArray = new float[2];
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                measureChild(child, widthMeasureSpec, heightMeasureSpec);
//            }
//        }
//    }
//
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        canvas.save();
//        canvas.translate(mPosX, mPosY);
//        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
//        super.dispatchDraw(canvas);
//        canvas.restore();
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        mDispatchTouchEventWorkingArray[0] = ev.getX();
//        mDispatchTouchEventWorkingArray[1] = ev.getY();
//        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
//        ev.setLocation(mDispatchTouchEventWorkingArray[0],
//                mDispatchTouchEventWorkingArray[1]);
//        return super.dispatchTouchEvent(ev);
//    }
//
//    /**
//     * Although the docs say that you shouldn't override this, I decided to do
//     * so because it offers me an easy way to change the invalidated area to my
//     * likening.
//     */
//    @Override
//    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
//
//        mInvalidateWorkingArray[0] = dirty.left;
//        mInvalidateWorkingArray[1] = dirty.top;
//        mInvalidateWorkingArray[2] = dirty.right;
//        mInvalidateWorkingArray[3] = dirty.bottom;
//
//
//        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
//        dirty.set(Math.round(mInvalidateWorkingArray[0]), Math.round(mInvalidateWorkingArray[1]),
//                Math.round(mInvalidateWorkingArray[2]), Math.round(mInvalidateWorkingArray[3]));
//
//        location[0] *= mScaleFactor;
//        location[1] *= mScaleFactor;
//        return super.invalidateChildInParent(location, dirty);
//    }
//
//    private float[] scaledPointsToScreenPoints(float[] a) {
//        mScaleMatrix.mapPoints(a);
//        mTranslateMatrix.mapPoints(a);
//        return a;
//    }
//
//    private float[] screenPointsToScaledPoints(float[] a){
//        mTranslateMatrixInverse.mapPoints(a);
//        mScaleMatrixInverse.mapPoints(a);
//        return a;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        mOnTouchEventWorkingArray[0] = ev.getX();
//        mOnTouchEventWorkingArray[1] = ev.getY();
//
//        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);
//
//        ev.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
//        mScaleDetector.onTouchEvent(ev);
//
//        final int action = ev.getAction();
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                final float x = ev.getX();
//                final float y = ev.getY();
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//
//                // Save the ID of this pointer
//                mActivePointerId = ev.getPointerId(0);
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                // Find the index of the active pointer and fetch its position
//                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
//                final float x = ev.getX(pointerIndex);
//                final float y = ev.getY(pointerIndex);
//
//                final float dx = x - mLastTouchX;
//                final float dy = y - mLastTouchY;
//
//                mPosX += dx;
//                mPosY += dy;
//                mTranslateMatrix.preTranslate(dx, dy);
//                mTranslateMatrix.invert(mTranslateMatrixInverse);
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//
//                invalidate();
//                break;
//            }
//
//            case MotionEvent.ACTION_UP: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_CANCEL: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_POINTER_UP: {
//                // Extract the index of the pointer that left the touch sensor
//                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//                final int pointerId = ev.getPointerId(pointerIndex);
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                    mLastTouchX = ev.getX(newPointerIndex);
//                    mLastTouchY = ev.getY(newPointerIndex);
//                    mActivePointerId = ev.getPointerId(newPointerIndex);
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//            if (detector.isInProgress()) {
//                mFocusX = detector.getFocusX();
//                mFocusY = detector.getFocusY();
//            }
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//            mScaleMatrix.setScale(mScaleFactor, mScaleFactor,
//                    mFocusX, mFocusY);
//            mScaleMatrix.invert(mScaleMatrixInverse);
//            invalidate();
//            requestLayout();
//
//            return true;
//        }
//    }
}
