package de.muffinworks.knittingapp.layouts;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Scroller;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.KnittingParser;
import de.muffinworks.knittingapp.views.LineNumberTextView;
import de.muffinworks.knittingapp.views.LinedEditorEditText;

/**
 * Created by Bianca on 18.06.2016.
 */
public class RowEditorLinearLayout extends LinearLayout {

    private LineNumberTextView lineNumbers;
    private LinedEditorEditText editText;

    private boolean canBeEdited = true;

    //Scroll
    private boolean mIsBeingDragged = false;
    private long mLastScroll;
    private Point mLastScrollTo = new Point();
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
        inflater.inflate(R.layout.view_row_editor, this, true);

        //// TODO: 25.06.2016 line number textview and edit text should have same font for same lineheight
        lineNumbers = (LineNumberTextView) findViewById(R.id.row_editor_line_numbers);

        editText = (LinedEditorEditText) findViewById(R.id.row_editor_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                scrollToTextChange();
            }
        });

        mScroller = new Scroller(context);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();

//        addDebugText();

        callOnClick();
    }

    private void addDebugText() {
        editText.setText(getResources().getString(R.string.lorem_long_with_breaks));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateEditorLines();
    }

    public void updateEditorLines() {
        mScroller.forceFinished(true);
        int lineCount = editText.getLineCount();
        lineNumbers.updateLineNumbers(lineCount);
        editText.setMinWidth(getWidth() - lineNumbers.getWidth());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      pattern stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPattern(String[] patternRows) {
        String pattern = KnittingParser.parsePojoToRowFormat(patternRows);
        editText.setText(pattern);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      line numbers textview and edittext functionality
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

     public KeyboardlessEditText2 getEditText() {
        return editText;
    }

    public void setCanBeEdited(boolean editable) {
        canBeEdited = editable;
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);
        editText.setTextIsSelectable(false);
        editText.clearFocus();
    }


    public boolean isCanBeEdited() {
        return canBeEdited;
    }

    public void onEnterPressed() {
        editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        updateEditorLines();
    }

    public void onDeletePressed() {
        editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        updateEditorLines();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
//            scrollBy(dx, dy);
            mScroller.startScroll(getScrollX(), getScrollY(), dx, dy);
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
            int height = getChildAt(0).getHeight();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                width += child.getWidth();
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
//        Log.i("mm", "onlayout cotainer: " + editText.getLineCount());
//        updateEditorLines();
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

                mLastScrollTo.set(x, y);
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

    /**
     * scroll behavior so far: checking if point is in visible area works and scrolls to point if it is
     * not visible. edge behavior is faulty:
     * bottom: scrollTo() will clamp if line gets added at the bottom, will only scroll to second to last line,
     * since textview height has not been adjusted yet at that point.
     * right: {#LinedEditorEditText#getCursorPosition} gives wrong x position if character is added
     * on last word from {#@string/lorem_long_with_breaks}. I believe that is because a normal edittext
     * is made to wrap at the end of it's width. Since I am suppressing that behavior, and setting
     * the edittext's minimum width new after each interaction by calling {#updateEditorLines}, it is
     * likely that the implementation gets confused. The x position that is returned is always
     * where the added character would be if we wrapped the line.
     */
    public void scrollToTextChange() {
        //add line numbers width to get total width
        Point position = editText.getCursorPosition();
        position.x = position.x + lineNumbers.getWidth();

        Point center = getScreenCenter();

        //DEBUG
        int visibleWidth = getWidth();
        int visibleHeight = getHeight();
        int currScrollerX = mScroller.getCurrX();
        int currScrollerY = mScroller.getCurrY();
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int lastScrollX = mLastScrollTo.x;
        int lastScrollY = mLastScrollTo.y;

        boolean visible = new Rect(scrollX, scrollY, getWidth() + scrollX, getHeight() + scrollY).contains(
                position.x,
                position.y
        );
        // TODO: 18.07.2016 scroll on text change, not only on size change
        if (!visible) {
            int x = position.x - center.x;
            int y = position.y - center.y;
            scrollTo(x, y);
            invalidate();
        }
    }

    private Point getScreenCenter() {
        return new Point(getWidth() / 2, getHeight() / 2);
    }
}