package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 11.07.2016.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
public class GridEditorView extends View {


    private final float CELL_WIDTH = 100.0f;
    private final float MARGIN = 40.0f;
    private final float ZOOM_FACTOR_MIN = 0.5f;
    private final float ZOOM_FACTOR_MAX = 2.0f;

    private int rows = 15;
    private int columns = 14;
    private String[][] symbols = new String[rows][columns];

    //buffers for drawing. declared here to avoid allocation during draw calls
    private float[] mXLinePositionsBuffer = new float[]{};
    private float[] mYLinePositionsBuffer = new float[]{};

    private RectF mContentRect = new RectF();

    private Paint mGridPaint;
    private Paint mLabelTextPaint;
    private Paint mSymbolPaint;
    private Paint mHighlightFillerPaint;

    private float mScaleFactor = 1f;
    private PointF mScaleFocusPoint;
    private ScaleGestureDetector mScaleGestureDetector;

    private GestureDetector mGestureDetector;


    public GridEditorView(Context context) {
        super(context);
    }

    public GridEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();

        mScaleGestureDetector = new ScaleGestureDetector(context, new GridScaleListener());
        mGestureDetector = new GestureDetector(context, new GridGestureListener());
    }

    public GridEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaints() {
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
        mSymbolPaint.setTextSize(40);
        Typeface knittingFont = Typeface.createFromAsset(getContext().getAssets(), Constants.KNITTING_FONT_PATH);
        mSymbolPaint.setTypeface(knittingFont);

        mHighlightFillerPaint = new Paint();
        mHighlightFillerPaint.setStrokeWidth(2);
        mHighlightFillerPaint.setColor(Color.RED);
        mHighlightFillerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentRect.set(
                getPaddingLeft() + MARGIN,
                getPaddingTop() + MARGIN,
                (columns * CELL_WIDTH) + MARGIN,//getWidth() - getPaddingRight(),
                (rows * CELL_WIDTH) + MARGIN//getHeight() - getPaddingBottom()
        );
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

    public void setChartSize(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        //create array for symbols in new size
        String[][] newSymbols = new String[rows][columns];
        //fill new array with data from old: data should persist in location, if new array is smaller
        //than old, the data will be cut off and lost
        if (rows > 0 && columns > 0) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    newSymbols[r][c] = symbols[r][c];
                }
            }
        }

        symbols = newSymbols;
    }

    public void setSymbol(int row, int column, String symbol) {
        if (row >= 0
                && row < rows
                && column >= 0
                && column < columns) {
            symbols[row][column] = symbol;
        }
        // TODO: 11.07.2016 throw exception???
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      touch
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    private int calculateRowFromValue(float y) {
        // TODO: 12.07.2016 Math.roof or floor???
        int row = (int)((y - MARGIN) / (CELL_WIDTH * mScaleFactor));
        return row;
    }

    private int calculateColumnFromValue(float x) {
        // TODO: 12.07.2016 Math.roof or floor???
        int column = (int)((x - MARGIN) / (CELL_WIDTH * mScaleFactor));
        return column;
    }

    private RectF getRectFromCell(int row, int column) {
        return new RectF(
                mContentRect.left + (column * (CELL_WIDTH * mScaleFactor)),
                mContentRect.top + (row * (CELL_WIDTH * mScaleFactor)),
                mContentRect.left + (column * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH * mScaleFactor),
                mContentRect.top + (row * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH * mScaleFactor)
        );
    }

    private PointF getCellCenter(int row, int column) {
        // TODO: 12.07.2016 find true center with font offsets.  
        return new PointF(
                mContentRect.left + (column * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH/2 * mScaleFactor),
                mContentRect.top + (row * (CELL_WIDTH * mScaleFactor)) + (CELL_WIDTH/2 + mScaleFactor)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      canvas drawing related stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        drawGrid(canvas);
        drawTextLabels(canvas);
        drawSymbols(canvas);

        canvas.restore();
    }

    private void drawSymbols(Canvas canvas) {
        for (int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                String symbol = symbols[r][c];
                if (symbol != null) {
                    PointF location = getCellCenter(r, c);
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

    private void drawTextLabels(Canvas canvas) {

        //draw row numbers
        for (int r = 0; r < rows; r++) {
            int text = r + 1;
            canvas.drawText(
                    text+"",
                    5f, //text width + offset
                    ((r * CELL_WIDTH)
                            + CELL_WIDTH / 2
                            + ((int) Math.abs(mLabelTextPaint.getFontMetrics().top))/2
                            + MARGIN)
                        * mScaleFactor,
                    mLabelTextPaint);
        }

        //draw column numbers
        for (int c = 0; c < columns; c++) {
            int text = c + 1;
            canvas.drawText(
                    text+"",
                    ((c * CELL_WIDTH)
                            + MARGIN
                            + CELL_WIDTH/2
                            - mLabelTextPaint.measureText(text + "") / 2
                    )
                        * mScaleFactor,
                    25f, //text height + offset
                    mLabelTextPaint);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Listeners
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GridGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            int row = calculateRowFromValue(y);
            int column = calculateColumnFromValue(x);

            Log.d("mm", "row: " + row + " column: " + column);
            if (row >= 0
                    && row < rows
                    && column >= 0
                    && column < columns) {
                symbols[row][column] = "W";
            }

            postInvalidate();

            return true;
        }
    }

    class GridScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

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
