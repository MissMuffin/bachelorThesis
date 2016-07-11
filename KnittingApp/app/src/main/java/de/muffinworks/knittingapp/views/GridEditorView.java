package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by Bianca on 11.07.2016.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
public class GridEditorView extends View {


    private final float CELL_WIDTH = 32.0f;
    private final float MARGIN = 40.0f;

    private int rows = 15;
    private int columns = 15;
    private String[][] symbols = new String[rows][columns];

    //buffers for drawing. declared here to avoid allocation during draw calls
    private float[] mXLinePositionsBuffer = new float[]{};
    private float[] mYLinePositionsBuffer = new float[]{};

    private Paint mGridPaint;
    private Paint mLabelTextPaint;

    private float mScaleFactor = 1f;


    public GridEditorView(Context context) {
        super(context);
    }

    public GridEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
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
        if (row > 0
                && row < rows
                && column > 0
                && column < columns) {
            symbols[row][column] = symbol;
        }
        // TODO: 11.07.2016 throw exception???
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

        canvas.restore();
    }

    private void drawGrid(Canvas canvas) {

        // TODO: 11.07.2016 refactor, only have for loops in one place: instead of row + 1 etc draw
        //chart border alone
        if (rows > 0 && columns > 0) {
            for (int i = 0; i < rows + 1; i++) {
                canvas.drawLine(
                        MARGIN,
                        (i * CELL_WIDTH) + MARGIN,
                        (columns * CELL_WIDTH) + MARGIN,
                        (i * CELL_WIDTH) + MARGIN,
                        mGridPaint);
            }

            for (int j = 0; j < columns + 1; j++) {
                canvas.drawLine(
                        (j * CELL_WIDTH) + MARGIN,
                        MARGIN,
                        (j * CELL_WIDTH) + MARGIN,
                        (rows * CELL_WIDTH) + MARGIN,
                        mGridPaint);
            }

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    String symbol = symbols[r][c];
                    if (symbol != null) {
                        canvas.drawText(
                                symbol,
                                c * CELL_WIDTH,
                                r * CELL_WIDTH,
                                mGridPaint
                        );
                    }
                }
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

}
