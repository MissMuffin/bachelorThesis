package de.muffinworks.knittingapp.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.views.LineNumberTextView;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorLinearLayout extends LinearLayout {

    LineNumberTextView lineNumbers;
    KeyboardlessEditText2 editText;
    int lines; //first line starts at 0

    //Scroll
    private boolean mIsBeingDragged = false;
    private long mLastScroll;
    private Scroller mScroller;
    private PointF mLastMotion = new PointF();
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      initial setup
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public RowEditorLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public RowEditorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RowEditorLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);

        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.row_editor, this, true);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight
        lineNumbers = (LineNumberTextView) findViewById(R.id.row_editor_line_numbers);
        editText = (KeyboardlessEditText2) findViewById(R.id.row_editor_edit_text);

        mScroller = new Scroller(context);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //cant set min width in init() because views haven't finished inflating yet
        editText.setMinimumWidth(getWidth() - editText.getLeft() - editText.getPaddingLeft());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      line numbers textview and edittext functionality
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
//        return mScaleDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) return false;
        if (!canScroll()) return false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //interrupt fling
                if (!mScroller.isFinished()) mScroller.abortAnimation();
                mLastMotion.set(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotion.x - x);
                int deltaY = (int) (mLastMotion.y - y);
                mLastMotion.set(x, y);

                if (deltaX < 0) {
                    if (getScrollX() < 0) {
                        deltaX = 0;
                    }
                } else if (deltaX > 0) {
                    final int rightEdge = getWidth() - getPaddingRight();
                    final int availableToScroll = getChildAt(1).getRight() - getScrollX() - rightEdge;
                    if (availableToScroll > 0) {
                        deltaX = Math.min(availableToScroll, deltaX);
                    } else {
                        deltaX = 0;
                    }
                }
                if (deltaY < 0) {
                    if (getScrollY() < 0) {
                        deltaY = 0;
                    }
                } else if (deltaY > 0) {
                    final int bottomEdge = getHeight() - getPaddingBottom();
                    final int availableToScroll = getChildAt(0).getBottom() - getScrollY() - bottomEdge;
                    if (availableToScroll > 0) {
                        deltaY = Math.min(availableToScroll, deltaY);
                    } else {
                        deltaY = 0;
                    }
                }
                if (deltaY != 0 || deltaX != 0) {
                    scrollBy(deltaX, deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initialXVelocity = (int) velocityTracker.getXVelocity();
                int initialYVelocity = (int) velocityTracker.getYVelocity();

                if ((Math.abs(initialXVelocity) + Math.abs(initialYVelocity) > mMinimumVelocity) && getChildCount() > 0) {
                    fling(-initialXVelocity, -initialYVelocity);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
        }
        return true;
    }

    private void doScroll(int deltaX, int deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            smoothScrollBy(deltaX, deltaY);
        }
    }

    public final void smoothScrollBy(int dx, int dy) {
        long duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll;
        if (duration > 250) {
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
            // awakenScrollBars(mScroller.getDuration());
            invalidate();
        } else {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            scrollBy(dx, dy);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      scroll
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean canScroll() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int childrenHeight = 0;
            int childrenWidth = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                childrenHeight += child.getHeight();
                childrenWidth += child.getWidth();
            }
            return (getHeight() < childrenHeight + getPaddingTop() + getPaddingBottom())
                   || (getWidth() < childrenWidth + getPaddingRight() + getPaddingLeft());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action =ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
            return true;
        }

        if (!canScroll()) {
            mIsBeingDragged = false;
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotion.x);
                final int yDiff = (int) Math.abs(y - mLastMotion.y);
                if (xDiff > mTouchSlop || yDiff > mTouchSlop) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotion.x = x;
                mLastMotion.y = y;
                mIsBeingDragged = !mScroller.isFinished();
                break;
            case MotionEvent.ACTION_UP:
                //release drag
                mIsBeingDragged = false;
                break;
        }
        //only intercept motion events if we are dragging
        return mIsBeingDragged;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return getChildCount() == 0 ? getHeight() : getChildrenDimensions().bottom;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return getChildCount() == 0 ? getWidth() : getChildrenDimensions().right;
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                lp.width);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.leftMargin + lp.rightMargin,
                MeasureSpec.UNSPECIFIED);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin,
                MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (getChildCount() > 0) {
                Rect childrenDimens = getChildrenDimensions();
                scrollTo(clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), childrenDimens.width()),
                        clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), childrenDimens.height()));
            } else {
                scrollTo(x, y);
            }
            if (oldX != getScrollX() || oldY != getScrollY()) {
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            // Keep on drawing until the animation has finished.
            postInvalidate();
        }
    }

    private Rect getChildrenDimensions() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int width = 0;
            int height = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                width += child.getWidth();
                height += child.getHeight();
            }
            return new Rect(0, 0, width, height);
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // initial clam
        scrollTo(getScrollX(), getScrollY());
    }

    public void fling(int velocityX, int velocityY) {
        if (getChildCount() > 0) {
            int height = getHeight() - getPaddingBottom() - getPaddingTop();
            int bottom = getChildAt(0).getHeight();
            int width = getWidth() - getPaddingRight() - getPaddingLeft();
            int right = getChildAt(1).getRight();

            mScroller.fling(getScrollX(), getScrollY(), velocityX, velocityY, 0, right - width, 0, bottom - height);
            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        // we rely on the fact the View.scrollBy calls scrollTo.
        if (getChildCount() > 0) {
            Rect childrenDimens = getChildrenDimensions();
            x = clamp(x, getWidth() - getPaddingRight() - getPaddingLeft(), childrenDimens.width());
            y = clamp(y, getHeight() - getPaddingBottom() - getPaddingTop(), childrenDimens.height());
            if (x != getScrollX() || y != getScrollY()) {
                super.scrollTo(x, y);
            }
        }
    }

    private int clamp(int currentPos, int viewDimens, int childDimens) {
        if (viewDimens >= childDimens || currentPos < 0) {
            return 0;
        }
        if (viewDimens + currentPos > childDimens) {
            return childDimens - viewDimens;
        }
        return currentPos;
    }
}
