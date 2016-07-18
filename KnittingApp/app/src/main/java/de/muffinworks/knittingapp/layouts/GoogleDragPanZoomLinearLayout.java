package de.muffinworks.knittingapp.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Zoomer;
import de.muffinworks.knittingapp.views.LineNumberTextView;

/**
 * Created by Bianca on 18.06.2016.
 */
public class GoogleDragPanZoomLinearLayout extends LinearLayout {

    LineNumberTextView lineNumbers;
    KeyboardlessEditText2 editText;
    int lines; //first line starts at 0

    //scale
//    private ScaleGestureDetector mScaleDetector;
//    private float mScaleFactor = 1.f;

    //Scroll
//    private boolean mIsScrolling = false;

    //GOOGLE CODE
    /**
     * initial fling velocity for pan operations, in screen widths or heights
     */
    private static final float PAN_VELOCITY_FACTOR = 2f;

    /**
     * scaling factor for a single zoom step
     */
    private static final float ZOOM_AMOUNT = 0.25f;

    /**
     * viewport extremes
     */
    private static final float AXIS_X_MIN = -1f;
    private static final float AXIS_X_MAX = 1f;
    private static final float AXIS_Y_MIN = -1f;
    private static final float AXIS_Y_MAX = 1f;
    /**
     * current viewport: this rectangle represents the currentlz visible chart domain and range
     */
    private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);
    /**
     * current destination rectangle in pixel coordinates into which the content should be drawn
     */
    private Rect mContentRect = new Rect();

    //state objects and values related to gesture tracking
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private Zoomer mZoomer;
    private PointF mZoomFocalPoint = new PointF();
    //used only for zooms and flings
    private RectF mScrollerStartViewport = new RectF();

    //edge effect / overscroll tracking objects
    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private EdgeEffectCompat mEdgeEffectLeft;
    private EdgeEffectCompat mEdgeEffectRight;
    private boolean mEdgeEffectTopActive;
    private boolean mEdgeEffectBottomActive;
    private boolean mEdgeEffectLeftActive;
    private boolean mEdgeEffectRightActive;

    //Buffers used during drawing
    private Point mSurfaceSizeBuffer = new Point();

    //???
    private Paint mPaint;


    public GoogleDragPanZoomLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public GoogleDragPanZoomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoogleDragPanZoomLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);

        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.row_editor, this, true);

        lineNumbers = (LineNumberTextView) findViewById(R.id.row_editor_line_numbers);
        editText = (KeyboardlessEditText2) findViewById(R.id.row_editor_edit_text);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight

//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        //setup interactions
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        mScroller = new OverScroller(context);
        mZoomer = new Zoomer(context);
        //setup edge effects
        mEdgeEffectLeft = new EdgeEffectCompat(context);
        mEdgeEffectRight = new EdgeEffectCompat(context);
        mEdgeEffectTop = new EdgeEffectCompat(context);
        mEdgeEffectBottom = new EdgeEffectCompat(context);

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.amber_200));
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mContentRect.set(
                getPaddingLeft(),
                getPaddingTop(),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom()
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minChartSize = 100;
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(minChartSize + getPaddingLeft() + getPaddingRight(),
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(minChartSize + getPaddingTop() + getPaddingBottom(),
                                heightMeasureSpec))
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //clips next few drawing operations to the content area
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mContentRect);
        //removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);
        //draws container
        if (mPaint != null)canvas.drawRect(mContentRect, mPaint);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Methods related to editor functionality
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void initLineNumbers() {
        if (lineNumbers != null) lineNumbers.updateLineNumbers(editText.getLineCount());
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Methods and objects related to gesture handling
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Finds the chart point (i.e. within the chart's domain and range) represented by the
     * given pixel coordinates, if that pixel is within the chart region described by
     * {@link #mContentRect}. If the point is found, the "dest" argument is set to the point and
     * this function returns true. Otherwise, this function returns false and "dest" is unchanged.
     */
    private boolean hitTest(float x, float y, PointF dest) {
        if (!mContentRect.contains((int)x, (int)y)) {
            return false;
        }
        dest.set(
                mCurrentViewport.left
                        +mCurrentViewport.width()
                        *(x - mContentRect.left) / mContentRect.width(),
                mCurrentViewport.top
                        +mCurrentViewport.height()
                        *(y - mContentRect.bottom) / -mContentRect.height()
        );
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(ev);
        retVal = mGestureDetector.onTouchEvent(ev) || retVal;
        return retVal || super.onTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //clips next few drawing operations to the content area
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mContentRect);
        //removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);
        //draws container
        if (mPaint != null)canvas.drawRect(mContentRect, mPaint);
    }

    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations
         */
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
            float spanX = detector.getCurrentSpanX();
            float spanY = detector.getCurrentSpanY();

            float newWidth = lastSpanX / spanX * mCurrentViewport.width();
            float newHeight = lastSpanY / spanY * mCurrentViewport.height();

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            mCurrentViewport.set(
                    viewportFocus.x
                            - newWidth * (focusX - mContentRect.left)
                            / mContentRect.width(),
                    viewportFocus.y
                            - newHeight * (mContentRect.bottom - focusY)
                            / mContentRect.height(),
                    0,
                    0
            );
            mCurrentViewport.right = mCurrentViewport.left + newWidth;
            mCurrentViewport.bottom = mCurrentViewport.top + newHeight;
            constrainViewport();
            ViewCompat.postInvalidateOnAnimation(GoogleDragPanZoomLinearLayout.this);

            lastSpanX = spanX;
            lastSpanY = spanY;
            return true;
        }
    };

    /**
     * Ensures that current viewport is inside the viewport extremes defined by
     * {@link #AXIS_X_MIN},
     * {@link #AXIS_X_MAX}, {@link #AXIS_Y_MIN} and {@link #AXIS_Y_MAX}
     */
    private void constrainViewport() {
        mCurrentViewport.left = Math.max(AXIS_X_MIN, mCurrentViewport.left);
        mCurrentViewport.top = Math.max(AXIS_Y_MIN, mCurrentViewport.top);
        mCurrentViewport.bottom = Math.max(Math.nextUp(mCurrentViewport.top),
                Math.min(AXIS_Y_MAX, mCurrentViewport.bottom));
        mCurrentViewport.right = Math.max(Math.nextUp(mCurrentViewport.left),
                Math.min(AXIS_X_MAX, mCurrentViewport.right));
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            releaseEdgeEffects();
            mScrollerStartViewport.set(mCurrentViewport);
            mScroller.forceFinished(true);
            ViewCompat.postInvalidateOnAnimation(GoogleDragPanZoomLinearLayout.this);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mZoomer.forceFinished(true);
            if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
                mZoomer.startZoom(ZOOM_AMOUNT);
            }
            ViewCompat.postInvalidateOnAnimation(GoogleDragPanZoomLinearLayout.this);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            /**
             * Pixel offset is the offset in screen pixels, while viewport offset is the
             * offset within the current viewport. For additional information on surface sizes
             * and pixel offsets, see the docs for {@link computeScrollSurfaceSize()}. For
             * additional information about the viewport, see the comments for
             * {@link mCurrentViewport}.
             */
            float viewportOffsetX = distanceX * mCurrentViewport.width() / mContentRect.width();
            float viewportOffsetY = distanceY * mCurrentViewport.height() / mContentRect.height();
            computeScrollSurfaceSize(mSurfaceSizeBuffer);
            int scrolledX = (int)(mSurfaceSizeBuffer.x
                    * (mCurrentViewport.left + viewportOffsetX - AXIS_X_MIN)
                    / (AXIS_X_MAX - AXIS_X_MIN));
            int scrolledY = (int)(mSurfaceSizeBuffer.y
                    * (AXIS_Y_MAX - mCurrentViewport.bottom - viewportOffsetY)
                    / (AXIS_Y_MAX - AXIS_Y_MIN));
            boolean canScrollX = mCurrentViewport.left > AXIS_Y_MIN
                    || mCurrentViewport.right < AXIS_X_MAX;
            boolean canScrollY = mCurrentViewport.top > AXIS_Y_MIN
                    || mCurrentViewport.bottom < AXIS_Y_MAX;
            setViewportBottomLeft(
                    mCurrentViewport.left + viewportOffsetX,
                    mCurrentViewport.bottom + viewportOffsetY
            );

            if (canScrollX && scrolledX < 0) {
                mEdgeEffectLeft.onPull(scrolledX / (float)mContentRect.width(), 0.5f);
                mEdgeEffectLeftActive = true;
            }
            if (canScrollY && scrolledY < 0) {
                mEdgeEffectTop.onPull(scrolledY / (float)mContentRect.height(), 0.5f);
                mEdgeEffectTopActive = true;
            }
            if (canScrollX && scrolledX > mSurfaceSizeBuffer.x - mContentRect.width()) {
                mEdgeEffectRight.onPull((scrolledX - mSurfaceSizeBuffer.x + mContentRect.width())
                        / (float)mContentRect.width(), 0.5f);
                mEdgeEffectRightActive = true;
            }
            if (canScrollY && scrolledY > mSurfaceSizeBuffer.y - mContentRect.height()) {
                mEdgeEffectBottom.onPull((scrolledY - mSurfaceSizeBuffer.y + mContentRect.height())
                        / (float)mContentRect.height(), 0.5f);
                mEdgeEffectBottomActive = true;
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fling((int) -velocityX, (int) -velocityY);
            return true;
        }
    };

    private void releaseEdgeEffects() {
        mEdgeEffectLeftActive
                = mEdgeEffectTopActive
                = mEdgeEffectBottomActive
                = mEdgeEffectRightActive
                = false;
        mEdgeEffectLeft.onRelease();
        mEdgeEffectTop.onRelease();
        mEdgeEffectRight.onRelease();
        mEdgeEffectBottom.onRelease();
    }

    private void fling(int velocityX, int velocityY) {
        releaseEdgeEffects();
        //flings use math in pixels (oposed to math based on the viewport)
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCurrentViewport);
        int startX = (int)(mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - AXIS_X_MIN) / (
                AXIS_X_MAX - AXIS_X_MIN));
        int startY = (int)(mSurfaceSizeBuffer.y * (AXIS_Y_MAX - mScrollerStartViewport.bottom) / (
                AXIS_Y_MAX - AXIS_Y_MIN));
        mScroller.forceFinished(true);
        mScroller.fling(
                startX,
                startY,
                velocityX,
                velocityY,
                0,
                mSurfaceSizeBuffer.x - mContentRect.width(),
                0,
                mSurfaceSizeBuffer.y - mContentRect.height(),
                mContentRect.width() / 2,
                mContentRect.height() / 2
        );
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Computes the current scrollable surface size, in pixels. For example, if the entire chart
     * area is visible, this is simply the current size of {@link #mContentRect}. If the chart
     * is zoomed in 200% in both directions, the returned size will be twice as large horizontally
     * and vertically.
     */
    private void computeScrollSurfaceSize(Point out) {
        out.set(
                (int)(mContentRect.width() * (AXIS_X_MAX - AXIS_X_MIN)
                        / mCurrentViewport.width()),
                (int)(mContentRect.height() * (AXIS_Y_MAX - AXIS_Y_MIN)
                        / mCurrentViewport.height())
        );
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        boolean needsInvalidate = false;

        if (mScroller != null) {
            if (mScroller.computeScrollOffset()) {
                //scroller isnt finished: fling or programmatic pan is currently active
                computeScrollSurfaceSize(mSurfaceSizeBuffer);
                int currX = mScroller.getCurrX();
                int currY = mScroller.getCurrY();

                boolean canScrollX = (mCurrentViewport.left > AXIS_X_MIN
                        || mCurrentViewport.right < AXIS_X_MAX);
                boolean canScrollY = (mCurrentViewport.top > AXIS_Y_MIN
                        || mCurrentViewport.bottom < AXIS_Y_MAX);

                if (canScrollX
                        && currX < 0
                        && mEdgeEffectLeft.isFinished()
                        && !mEdgeEffectLeftActive) {
                    mEdgeEffectLeft.onAbsorb((int)mScroller.getCurrVelocity());
                    mEdgeEffectLeftActive = true;
                    needsInvalidate = true;
                } else if (canScrollX
                        && currX > (mSurfaceSizeBuffer.x - mContentRect.width())
                        && mEdgeEffectRight.isFinished()
                        && !mEdgeEffectRightActive) {
                    mEdgeEffectRight.onAbsorb((int)mScroller.getCurrVelocity());
                    mEdgeEffectRightActive = true;
                    needsInvalidate = true;
                }

                if (canScrollY
                        && currY < 0
                        && mEdgeEffectTop.isFinished()
                        && !mEdgeEffectTopActive) {
                    mEdgeEffectTop.onAbsorb((int)mScroller.getCurrVelocity());
                    mEdgeEffectTopActive = true;
                    needsInvalidate = true;
                } else if (canScrollY
                        && currY > (mSurfaceSizeBuffer.y - mContentRect.height())
                        && mEdgeEffectBottom.isFinished()
                        && !mEdgeEffectBottomActive) {
                    mEdgeEffectBottom.onAbsorb((int)mScroller.getCurrVelocity());
                    mEdgeEffectBottomActive = true;
                    needsInvalidate = true;
                }

                float currXRange = AXIS_X_MIN + (AXIS_X_MAX - AXIS_X_MIN)
                        * currX / mSurfaceSizeBuffer.x;
                float currYRange = AXIS_Y_MAX - (AXIS_Y_MAX - AXIS_Y_MIN)
                        * currY / mSurfaceSizeBuffer.y;
                setViewportBottomLeft(currXRange, currYRange);
            }
        }

        if (mZoomer != null) {
            if (mZoomer.computeZoom()) {
                //Perfomrs the zoom since a zoom is in progress (programmatically or double touch)
                float newWidth = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.width();
                float newHeight = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.height();
                float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
                        / mScrollerStartViewport.width();
                float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
                        / mScrollerStartViewport.height();
                mCurrentViewport.set(
                        mZoomFocalPoint.x - newWidth * pointWithinViewportX,
                        mZoomFocalPoint.y - newHeight * pointWithinViewportY,
                        mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX),
                        mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY)
                );
                constrainViewport();
                needsInvalidate = true;
            }
        }

        if (needsInvalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Sets the current viewport (defined by {@link #mCurrentViewport}) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position, and thus
     * the bottom of the {@link #mCurrentViewport} rectangle. For more details on why top and
     * bottom are flipped, see {@link #mCurrentViewport}.
     */
    private void setViewportBottomLeft(float x, float y) {
        /**
         * Constrains within the scroll range. The scroll range is simply the viewport extremes
         * (AXIS_X_MAX, etc.) minus the viewport size. For example, if the extrema were 0 and 10,
         * and the viewport size was 2, the scroll range would be 0 to 8.
         */
        float curWidth = mCurrentViewport.width();
        float curHeight = mCurrentViewport.height();
        x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - curWidth));
        y = Math.max(AXIS_Y_MIN + curHeight, Math.min(y, AXIS_Y_MAX));

        mCurrentViewport.set(x, y - curHeight, x + curWidth, y);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Methods for programmatically changing the viewport
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Smoothly zooms one step in
     */
    private void zoomIn() {
        mScrollerStartViewport.set(mCurrentViewport);
        mZoomer.forceFinished(true);
        mZoomer.startZoom(ZOOM_AMOUNT);
        mZoomFocalPoint.set(
                (mCurrentViewport.right + mCurrentViewport.left) / 2,
                (mCurrentViewport.bottom + mCurrentViewport.top) / 2
        );
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Smoothly zooms one step out
     */
    private void zoomOut() {
        mScrollerStartViewport.set(mCurrentViewport);
        mZoomer.forceFinished(true);
        mZoomer.startZoom(-ZOOM_AMOUNT);
        mZoomFocalPoint.set(
                (mCurrentViewport.right + mCurrentViewport.left) / 2,
                (mCurrentViewport.bottom + mCurrentViewport.top) / 2
        );
        ViewCompat.postInvalidateOnAnimation(this);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        canvas.save(Canvas.MATRIX_SAVE_FLAG);
//        canvas.scale(mScaleFactor, mScaleFactor);
//        super.dispatchDraw(canvas);
//        canvas.restore();
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = MotionEventCompat.getActionMasked(ev);
//
//        //case that touch gesture is complete
//        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            //release scroll
//            mIsScrolling = false;
//            //let children handle touch event, do not intercept it
//            return false;
//        }
//
//        switch(action) {
//            case MotionEvent.ACTION_MOVE: {
//                if (mIsScrolling) {
//                    //currently scrolling: interept touch event
//                    return true;
//                }
//                //if horizontal finger drag exceeded touch slop, start scroll
////                final int xDiff = calculateDistanceX(ev);
//
//                //calculate touch slop using ViewConfiguration constants
////                if (xDiff > mTouchSlop) {
//                    //start scrolling
//                    mIsScrolling = true;
//                    return true;
////                }
////                break;
//            }
//        }
//        //generally don't handle touch events, should be handled by child view
//        return false;
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//            //restrict zoom levels
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 2.0f));
//            invalidate();
//            return true;
//        }
//    }
//
//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }
//    }
}
