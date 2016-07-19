package de.muffinworks.knittingapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

import de.muffinworks.knittingapp.R;

/**
 * Created by Bianca on 13.06.2016.
 */
public class LinedEditorEditText extends KeyboardlessEditText2 {

    private Rect rect;
    private Paint paintNumbers;
    private Paint blue;
    private Paint cyan;
    private Paint green;
    private Paint red;
    private Paint paintBackgroundEven;
    private Paint paintBackgroundOdd;
    private int lastLineDrawn = 0; //will act as top position of a line in the edittext


    public LinedEditorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        //is not set first touch will not show cursor
        setCursorVisible(true);
        setSelection(0);

        rect = new Rect();
        float textSize = context.getResources().getDimension(R.dimen.row_editor_default_text_size);
        // TODO: 25.06.2016 set same font as edit text and textview in row editor

        paintNumbers = new Paint();
        paintNumbers.setStyle(Paint.Style.FILL);
        paintNumbers.setColor(Color.BLACK);
        paintNumbers.setTextSize(textSize);

//        DEBUG
//        blue = new Paint();
//        blue.setStyle(Paint.Style.FILL);
//        blue.setColor(Color.BLUE);
//        blue.setTextSize(textSize);
//
//        cyan = new Paint();
//        cyan.setStyle(Paint.Style.FILL);
//        cyan.setColor(Color.CYAN);
//        cyan.setTextSize(textSize);
//
//        green = new Paint();
//        green.setStyle(Paint.Style.FILL);
//        green.setColor(Color.GREEN);
//        green.setTextSize(textSize);
//
//        red = new Paint();
//        red.setStyle(Paint.Style.FILL);
//        red.setColor(Color.RED);
//        red.setTextSize(textSize);

        paintBackgroundEven = new Paint();
        paintBackgroundEven.setStyle(Paint.Style.FILL);
        paintBackgroundEven.setColor(ContextCompat.getColor(context, R.color.black_10));

        paintBackgroundOdd = new Paint();
        paintBackgroundOdd.setStyle(Paint.Style.FILL);
        paintBackgroundOdd.setColor(ContextCompat.getColor(context, R.color.black_01));
    }

    public Point getCursorPosition() {
        //https://stackoverflow.com/questions/5044342/how-to-get-cursor-position-x-y-in-edittext-android
        Layout layout = getLayout();
        if (layout != null) { //check if called before layout is inflated
            int pos = getSelectionStart();
            int line = layout.getLineForOffset(pos);
            int baseline = layout.getLineBaseline(line);
            int bl = (int)layout.getPrimaryHorizontal(pos);
            Point test = new Point(bl, baseline);
            Log.i("bbb", "pos: "+bl+" "+baseline);
            return test;
        }
        return new Point(0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int baseLine = getBaseline();
        int lineHeight = getLineHeight();

        Paint.FontMetricsInt metrics = paintNumbers.getFontMetricsInt();
        int ascent = metrics.ascent;
        int descent = metrics.descent;
        int bottom = metrics.bottom;
        int top = metrics.top;
        int leading = (top-ascent)+(bottom-descent);
        int verticalLineCenter = (descent + ascent)/2;

        for (int i = 0; i < getLineCount(); i++) {

            int lineNumber = i + 1;

//            DEBUG
//            as specified by google docs https://developer.android.com/reference/android/graphics/Paint.FontMetrics.html
//            canvas.drawLine(0,baseLine+top, 1000, baseLine+top, cyan); //top
//            canvas.drawLine(0,baseLine+ascent, 1000, baseLine+ascent, paintNumbers); //ascension
//            canvas.drawLine(0,baseLine, 1000, baseLine, blue); //base
//            canvas.drawLine(0,baseLine+descent, 1000, baseLine+descent, green); //descension
//            canvas.drawLine(0,baseLine+bottom, 1000, baseLine+bottom, red); //bottom


            canvas.drawRect(
                    rect.left,
                    baseLine+bottom - lineHeight,
                    getWidth(),
                    baseLine+bottom,
                    lineNumber % 2 == 1 ? paintBackgroundEven : paintBackgroundOdd);
            
            baseLine += getLineHeight();
        }
        super.onDraw(canvas);
    }
}