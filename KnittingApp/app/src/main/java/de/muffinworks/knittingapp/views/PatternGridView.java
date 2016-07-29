package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.util.PatternParser;

/**
 * Created by Bianca on 11.07.2016.
 */
@SuppressWarnings({
        "UnusedDeclaration",
        "UnusedParameters",
        "FieldCanBeLocal"
})
public class PatternGridView extends View {


    private static final String TAG = "GridEditorView";
    private static final int MAX_CLICK_DURATION = 100;

    private boolean canBeEdited = true;
    private final float CELL_WIDTH = 100.0f;
    private final float MARGIN = 40.0f;
    private final float ZOOM_FACTOR_MIN = 0.5f;
    private final float ZOOM_FACTOR_MAX = 2.0f;
    private final float DEFAULT_SYMBOL_TEXTSIZE = 60.0f;

    private int rows = Constants.DEFAULT_ROWS;
    private int columns = Constants.DEFAULT_COLUMNS;
    private String[][] symbols = new String[columns][rows];

    //buffers for drawing. declared here to avoid allocation during draw calls
    private float[] mXLinePositionsBuffer = new float[]{};
    private float[] mYLinePositionsBuffer = new float[]{};

    private RectF mContentRect = new RectF();
    private RectF mCanvasRect = new RectF();
    private RectF mRowHighlightRect = new RectF();

    private Paint mRowHighlight;
    private Paint mGridPaint;
    private Paint mLabelTextPaint;
    private Paint mSymbolPaint;

    private float mScaleFactor = 1f;
    private PointF mScaleFocusPoint;
    private ScaleGestureDetector mScaleGestureDetector;

    private GestureDetector mGestureDetector;
    private PointF mTranslationOffset = new PointF(0, 0);

    private String mSelectedSymbol = null;
    private int mCurrentRow = 0;


    public PatternGridView(Context context) {
        super(context);
        init(context);
    }

    public PatternGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PatternGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initPaints();
        updateContentRect();
        mScaleGestureDetector = new ScaleGestureDetector(context, new GridScaleListener());
        mGestureDetector = new GestureDetector(context, new GridGestureListener());
    }

    private void updateContentRect() {
        mContentRect.set(
                MARGIN,
                MARGIN,
                MARGIN + columns * CELL_WIDTH * mScaleFactor,
                MARGIN + rows * CELL_WIDTH * mScaleFactor
        );
    }

    private void initPaints() {
        mRowHighlight = new Paint();
        mRowHighlight.setStrokeWidth(1);
        mRowHighlight.setColor(getResources().getColor(R.color.highlight_current_row));
        mRowHighlight.setStyle(Paint.Style.FILL);

        mGridPaint = new Paint();
        mGridPaint.setStrokeWidth(1);
        mGridPaint.setColor(Color.BLACK);
        mGridPaint.setStyle(Paint.Style.STROKE);

        mLabelTextPaint = new Paint();
        mLabelTextPaint.setAntiAlias(true);
        mLabelTextPaint.setTextAlign(Paint.Align.LEFT);
        mLabelTextPaint.setTextSize(20);
        mLabelTextPaint.setColor(Color.BLACK);

        mSymbolPaint = new Paint();
        mSymbolPaint.setAntiAlias(true);
        mSymbolPaint.setTextAlign(Paint.Align.CENTER);
        mSymbolPaint.setTextSize(DEFAULT_SYMBOL_TEXTSIZE);
        Typeface knittingFont = Typeface.createFromAsset(getContext().getAssets(), Constants.KNITTING_FONT_PATH);
        mSymbolPaint.setTypeface(knittingFont);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasRect.set(
                getPaddingLeft(),
                getPaddingTop(),
                getPaddingLeft() + w,
                getPaddingTop() + h
        );
        if (mCurrentRow != 0) {
            scrollCurrentRowToCenter();
        }
    }

    public void scrollCurrentRowToCenter() {
        float currentRowTop = getPixelPositionTopForRow(mCurrentRow);
        mTranslationOffset.set(
                mTranslationOffset.x,
                mCanvasRect.height()/2 - currentRowTop + CELL_WIDTH
        );
        clampOffset();
        invalidate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      chart logic
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public String[][] getSymbols() {
        return symbols;
    }

    public void setChartSize(int columns, int rows) {
        this.rows = rows;
        this.columns = columns;
        //create array for symbols in new size
        String[][] newSymbols = new String[columns][rows];

        //fill new array with data from old: data should persist in location, if new array is smaller
        //than old, the data will be cut off and lost
        if (rows > 0 && columns > 0) {
            for (int c = 0; c < columns; c++) {
                for (int r = 0; r < rows; r++) {
                    if (c < symbols[0].length && r < symbols.length) {
                        newSymbols[c][r] = symbols[r][c];
                    } else {
                        newSymbols[c][r] = Constants.EMPTY_SYMBOL;
                    }
                }
            }
        }
        symbols = newSymbols;
        updateContentRect();
        invalidate();
    }

    public void setSymbol(int column, int row) {
        if (row >= 0
                && row < rows
                && column >= 0
                && column < columns) {

            symbols[column][row] = mSelectedSymbol;
        }
    }

    public void setPattern(String[] patternRows) {
        String[][] pattern = PatternParser.parsePojoToGridFormat(patternRows);
        setChartSize(pattern.length, pattern[0].length);
        symbols = pattern;
        invalidate();
    }

    public String[] getPattern() {
        String[] newPattern = PatternParser.parseGridFormatToPojo(symbols);
        return newPattern;
    }

    public void setDeleteActive(boolean active) {
        mSelectedSymbol = Constants.EMPTY_SYMBOL;
    }

    public void setSelectedKey(String key) {
        mSelectedSymbol = key;
    }

    public void setCurrentRow(int newCurrentRow) {
        mCurrentRow = newCurrentRow;
        if (mCanvasRect.height() != 0.0 && mCurrentRow > 0) {
            scrollCurrentRowToCenter();
        }
    }

    private void scrollCurrentRowIntoVisibleArea() {
        float posTop = getPixelPositionTopForRow(mCurrentRow);
        float posBot = getPixelPositionBottomForRow(mCurrentRow);

        if (posTop < mCanvasRect.top - mTranslationOffset.y) {
            mTranslationOffset.set(
                    mTranslationOffset.x,
                    mTranslationOffset.y + CELL_WIDTH
            );
            Log.i(TAG, "TOP top: " + mCanvasRect.top + " offset: " + mTranslationOffset.y + " pos: " + posTop);
        } else if (posBot > mCanvasRect.bottom - mTranslationOffset.y) {
            mTranslationOffset.set(
                    mTranslationOffset.x,
                    mTranslationOffset.y - CELL_WIDTH
            );
            Log.i(TAG, "BOTTOM height: "+ mCanvasRect.bottom +" offset: " + mTranslationOffset.y + " pos: " + posBot);
        }
        clampOffset();
        invalidate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      touch
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setCanBeEdited(boolean editable) {
        canBeEdited = editable;
    }

    public boolean isCanBeEdited() {
        return canBeEdited;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //see https://stackoverflow.com/questions/9965695/how-to-distinguish-between-move-and-click-in-ontouchevent
        //handle simple click tap
        if (event.getAction() == MotionEvent.ACTION_UP && canBeEdited) {
            long clickDuration = event.getEventTime() - event.getDownTime();
            if(clickDuration < MAX_CLICK_DURATION) {
                if (mSelectedSymbol != null) {
                    float x = event.getX();
                    float y = event.getY();
                    int row = calculateRowFromValue(y);
                    int column = calculateColumnFromValue(x);

                    setSymbol(column, row);
                    postInvalidate();
                }
                return true;
            }
        }

        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    private int calculateRowFromValue(float y) {
        // TODO: 12.07.2016 Math.roof or floor???
        int row = (int)((y - mTranslationOffset.y - MARGIN) / (CELL_WIDTH * mScaleFactor));
        return row;
    }

    private int calculateColumnFromValue(float x) {
        // TODO: 12.07.2016 Math.roof or floor???
        int column = (int)((x - mTranslationOffset.x - MARGIN) / (CELL_WIDTH * mScaleFactor));
        return column;
    }

    private RectF getRectFromCell(int column, int row) {
        return new RectF(
                mContentRect.left + (column * (CELL_WIDTH * mScaleFactor)),
                mContentRect.top + (row * (CELL_WIDTH * mScaleFactor)),
                mContentRect.left + (column * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH * mScaleFactor),
                mContentRect.top + (row * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH * mScaleFactor)
        );
    }

    private PointF getCellCenter(int column, int row) {
        // TODO: 12.07.2016 find true center with font offsets. ???
        return new PointF(
                MARGIN
                        + column * CELL_WIDTH * mScaleFactor
                        + CELL_WIDTH / 2 * mScaleFactor,
                MARGIN
                        + row * CELL_WIDTH * mScaleFactor
                        + CELL_WIDTH / 2 * mScaleFactor
                        + ((int) Math.abs(mSymbolPaint.getFontMetrics().top))/2
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      canvas drawing related stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private float getPixelPositionTopForRow(int row) {
        return mContentRect.top + (row - 1) * CELL_WIDTH * mScaleFactor;
    }

    private float getPixelPositionBottomForRow(int row) {
        return mContentRect.top + row * CELL_WIDTH * mScaleFactor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mTranslationOffset.x, mTranslationOffset.y);

        if (mCurrentRow != 0) {
            mRowHighlightRect.set(
                    mContentRect.left,
                    getPixelPositionTopForRow(mCurrentRow),
                    mContentRect.left + columns * CELL_WIDTH * mScaleFactor,
                    getPixelPositionBottomForRow(mCurrentRow)
            );
            canvas.drawRect(mRowHighlightRect, mRowHighlight);
        }

        drawGrid(canvas);
        drawAxisLabels(canvas);

        drawSymbols(canvas);

        canvas.restore();
    }

    private void drawSymbols(Canvas canvas) {
        for(int c = 0; c < columns; c++) {
            for (int r = 0; r < rows; r++) {
                String symbol = symbols[c][r];
                if (symbol != null) {
                    mSymbolPaint.setTextSize(DEFAULT_SYMBOL_TEXTSIZE * mScaleFactor);
                    PointF location = getCellCenter(c, r);
                    canvas.drawText(
                            symbol,
                            location.x,
                            location.y,
                            mSymbolPaint
                    );
                }
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        // TODO: 11.07.2016 refactor, only have for loops in one place: instead of row + 1 etc draw
        //chart border alone
        if (rows > 0 && columns > 0) {
            for (int i = 0; i < rows + 1; i++) {
                canvas.drawLine(
                        MARGIN,
                        (i * CELL_WIDTH * mScaleFactor) + MARGIN,
                        (columns * CELL_WIDTH * mScaleFactor) + MARGIN,
                        (i * CELL_WIDTH * mScaleFactor) + MARGIN,
                        mGridPaint);
            }

            for (int j = 0; j < columns + 1; j++) {
                canvas.drawLine(
                        (j * CELL_WIDTH * mScaleFactor) + MARGIN,
                        MARGIN,
                        (j * CELL_WIDTH * mScaleFactor) + MARGIN,
                        (rows * CELL_WIDTH * mScaleFactor) + MARGIN,
                        mGridPaint);
            }
        }
    }

    private void drawAxisLabels(Canvas canvas) {
        //draw column labels
        for (int r = 0; r < rows; r++) {
            int text = r + 1;
            canvas.drawText(
                    text+"",
                    5f  - mTranslationOffset.x, //text width + offset
                    (
                            r * CELL_WIDTH * mScaleFactor
                                    + CELL_WIDTH / 2 * mScaleFactor
                                    + ((int) Math.abs(mLabelTextPaint.getFontMetrics().top))/2
                                    + MARGIN
                    ),
                    mLabelTextPaint);
        }
        //draw row labels
        for (int c = 0; c < columns; c++) {
            int text = c + 1;
            canvas.drawText(
                    text+"",
                    (
                            c * CELL_WIDTH * mScaleFactor
                                    + MARGIN
                                    + CELL_WIDTH / 2 * mScaleFactor
                                    - mLabelTextPaint.measureText(text + "") / 2
                    ),
                    25f  - mTranslationOffset.y, //text height + offset
                    mLabelTextPaint);
        }
    }

    public void resetZoomAtOffset(float x, float y) {
        mScaleFactor = 1.0f;
        mTranslationOffset.set(x, y);
        postInvalidate();
    }

    public void resetZoomAndTranslation() {
        resetZoomAtOffset(0.0f, 0.0f);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Listeners
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GridGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //minus operation because scroll is inverse to dragging
            mTranslationOffset.x -= distanceX;
            mTranslationOffset.y -= distanceY;

            clampOffset();

            postInvalidate();
            return true;
        }
    }

    private void clampOffset() {
        float maxRightOffset = mCanvasRect.width() - mContentRect.width() - 2 * MARGIN;
        float maxDownOffset = mCanvasRect.height() - mContentRect.height() - 2 * MARGIN;

//                Log.d(TAG, "offset x     " + mTranslationOffset.x + " y " + mTranslationOffset.y);
//                Log.d(TAG, "max offset x " + maxRightOffset + " y " + maxDownOffset);

        if (mTranslationOffset.x > 0.0f || maxRightOffset > 0) {
            mTranslationOffset.x = 0.0f;
        }
        if (mTranslationOffset.y > 0.0f || maxDownOffset > 0) {
            mTranslationOffset.y = 0.0f;
        }
        if (maxRightOffset < 0 && mTranslationOffset.x < maxRightOffset) {
            mTranslationOffset.x = maxRightOffset;
        }
        if (maxDownOffset < 0 && mTranslationOffset.y < maxDownOffset) {
            mTranslationOffset.y = maxDownOffset;
        }
    }

    class GridScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        // TODO: 13.07.2016 implement focus point for scaling

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
}
