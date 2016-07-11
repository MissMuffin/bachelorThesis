package de.muffinworks.knittingapp.layouts;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import de.muffinworks.knittingapp.util.Zoomer;

/**
 * Created by Bianca on 28.06.2016.
 */
public class TestLinearLayout extends LinearLayout {

    private static final float ZOOM_STEP = 0.25f;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private OverScroller mScroller;
    private Zoomer mZoomer;
    private PointF mZoomFocalPoint = new PointF();

    //edge effect / overscroll tracking objects
    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private EdgeEffectCompat mEdgeEffectLeft;
    private EdgeEffectCompat mEdgeEffectRight;
    private boolean mEdgeEffectTopActive;
    private boolean mEdgeEffectBottomActive;
    private boolean mEdgeEffectLeftActive;
    private boolean mEdgeEffectRightActive;

    private int mTouchSlop;
    private int mMinimumFlingVelocity;


    public TestLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public TestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mZoomer = new Zoomer(context);

        mEdgeEffectLeft = new EdgeEffectCompat(context);
        mEdgeEffectRight = new EdgeEffectCompat(context);
        mEdgeEffectTop = new EdgeEffectCompat(context);
        mEdgeEffectBottom = new EdgeEffectCompat(context);

        mScroller = new OverScroller(context);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setOrientation(HORIZONTAL);
        setWillNotDraw(false);

        final ViewConfiguration viewConfig = ViewConfiguration.get(context);
        mTouchSlop = viewConfig.getScaledTouchSlop();
        mMinimumFlingVelocity = viewConfig.getScaledMinimumFlingVelocity();
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    //      Listener instances
    //
    ////////////////////////////////////////////////////////////////////////////

    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            ViewCompat.postInvalidateOnAnimation(TestLinearLayout.this);
            //let children handle event this way???
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mZoomer.forceFinished(true);
            mZoomer.startZoom(ZOOM_STEP);
            ViewCompat.postInvalidateOnAnimation(TestLinearLayout.this);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        private PointF focus = new PointF();
        private float lastSpanX;
        private float lastSpanY;

        private final float MIN_ZOOM_LEVEL = 0.1f;
        private final float MAX_ZOOM_LEVEL = 2.0f;

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

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

//            mScaleFactor *= detector.getScaleFactor();
//            //restrict zoom levels
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 2.0f));
//            invalidate();
            return true;
        }
    };
}
