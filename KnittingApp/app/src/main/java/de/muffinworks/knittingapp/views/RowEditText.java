package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EditText;

import de.muffinworks.knittingapp.R;

/**
 * Created by Bianca on 13.07.2016.
 */
public class RowEditText extends EditText {


    private final float LEFT_PADDING = 40.0f;
    private final float DEFAULT_SCALE_FACTOR = 1.0f;
    private final float ZOOM_FACTOR_MIN = 0.5f;
    private final float ZOOM_FACTOR_MAX = 2.0f;


    private RectF rect;
    private Paint paintNumbers;
    private Paint paintBackgroundEven;
    private Paint paintBackgroundOdd;

    private float mScaleFactor = DEFAULT_SCALE_FACTOR;
    private PointF mScaleFocusPoint;
    private PointF mTranslationOffset = new PointF(0,0);
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private RectF mCanvasRect =  new RectF();
    private RectF mContentRect = new RectF();


    public RowEditText(Context context) {
        super(context);
    }

    public RowEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context, new RowGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, new RowScaleListener());

        updateContentRect();

        rect = new RectF();
        paintNumbers = new Paint();
        paintNumbers.setStyle(Paint.Style.FILL);
        paintNumbers.setColor(Color.BLACK);
        paintNumbers.setTextSize(30);

        paintBackgroundEven = new Paint();
        paintBackgroundEven.setStyle(Paint.Style.FILL);
        paintBackgroundEven.setColor(ContextCompat.getColor(context, R.color.black_01));

        paintBackgroundOdd = new Paint();
        paintBackgroundOdd.setStyle(Paint.Style.FILL);
        paintBackgroundOdd.setColor(ContextCompat.getColor(context, R.color.black_10));
    }

    public RowEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO: 13.07.2016 think about old w oldh use
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasRect.set(
                getPaddingLeft() + 0,
                getPaddingTop() + 0,
                getPaddingLeft() + 0 + w,//columns * CELL_WIDTH * mScaleFactor + MARGIN,
                getPaddingTop() + 0 + h//rows * CELL_WIDTH * mScaleFactor + MARGIN
        );
    }

    private void updateContentRect() {
        mContentRect.set(
                LEFT_PADDING,
                0,
                LEFT_PADDING + getWidth(),
                getHeight()
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      touch stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      canvas drawing related stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        int baseLine = getBaseline();
        int lineHeight = getLineHeight();
        int verticalLineCenter = ((int)(paintNumbers.descent() + paintNumbers.ascent())/2);
        int numbersPaddingLeft = 20;

        super.onDraw(canvas);
        canvas.save();

//        canvas.translate(mTranslationOffset.x, mTranslationOffset.y);
        setX(mTranslationOffset.x);
        setY(mTranslationOffset.y);
        canvas.scale(mScaleFactor, mScaleFactor);

        for (int i = 0; i < getLineCount(); i++) {

            int lineNumber = i + 1;

            //draw line background
            canvas.drawRect(
                    rect.left,
                    baseLine - lineHeight,
                    getWidth(),
                    baseLine,
                    lineNumber % 2 == 1 ? paintBackgroundOdd : paintBackgroundEven);

            //draw line number
            canvas.drawText(
                    "" + lineNumber,
                    rect.left + numbersPaddingLeft,
                    baseLine + verticalLineCenter,
                    paintNumbers);

            baseLine += getLineHeight();
        }

        canvas.restore();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Listeners
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class RowScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private PointF viewportFocus = new PointF();
        private float lastSpanX;
        private float lastSpanY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastSpanX = detector.getCurrentSpanX();
            lastSpanY = detector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(Math.min(mScaleFactor, ZOOM_FACTOR_MAX), ZOOM_FACTOR_MIN);

            updateContentRect();

            viewportFocus.set(
                    detector.getFocusX(),
                    detector.getFocusY()
            );

            mScaleFocusPoint = viewportFocus;

            invalidate();
            return true;
        }
    }

    private class RowGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //minus operation because scroll is inverse to dragging
            mTranslationOffset.x -= distanceX;
            mTranslationOffset.y -= distanceY;

            float maxRightOffset = mCanvasRect.width() - mContentRect.width();
            float maxDownOffset = mCanvasRect.height() - mContentRect.height();

//                Log.d(TAG, "offset x     " + mTranslationOffset.x + " y " + mTranslationOffset.y);
//                Log.d(TAG, "max offset x " + maxRightOffset + " y " + maxDownOffset);

//            if (mTranslationOffset.x > 0.0f || maxRightOffset > 0) {
//                mTranslationOffset.x = 0.0f;
//            }
//            if (mTranslationOffset.y > 0.0f || maxDownOffset > 0) {
//                mTranslationOffset.y = 0.0f;
//            }
//            if (maxRightOffset < 0 && mTranslationOffset.x < maxRightOffset) {
//                mTranslationOffset.x = maxRightOffset;
//            }
//            if (maxDownOffset < 0 && mTranslationOffset.y < maxDownOffset) {
//                mTranslationOffset.y = maxDownOffset;
//            }
            postInvalidate();
            return true;
        }
    }
}
