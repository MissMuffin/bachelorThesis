package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

/**
 * Created by Bianca on 13.06.2016.
 */
public class LineEditText extends KeyboardlessEditText2 {

    private Rect rect;
    private Paint paintNumbers;
    private Paint blue;
    private Paint cyan;
    private Paint green;
    private Paint red;
    private Paint paintBackgroundEven;
    private Paint paintBackgroundOdd;
    private int lastLineDrawn = 0; //will act as top position of a line in the edittext


    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        rect = new Rect();
        float textSize = context.getResources().getDimension(R.dimen.row_editor_default_text_size);
        // TODO: 25.06.2016 set same font as edit text and textview in row editor

        paintNumbers = new Paint();
        paintNumbers.setStyle(Paint.Style.FILL);
        paintNumbers.setColor(Color.BLACK);
        paintNumbers.setTextSize(textSize);

        blue = new Paint();
        blue.setStyle(Paint.Style.FILL);
        blue.setColor(Color.BLUE);
        blue.setTextSize(textSize);

        cyan = new Paint();
        cyan.setStyle(Paint.Style.FILL);
        cyan.setColor(Color.CYAN);
        cyan.setTextSize(textSize);

        green = new Paint();
        green.setStyle(Paint.Style.FILL);
        green.setColor(Color.GREEN);
        green.setTextSize(textSize);

        red = new Paint();
        red.setStyle(Paint.Style.FILL);
        red.setColor(Color.RED);
        red.setTextSize(textSize);

        paintBackgroundEven = new Paint();
        paintBackgroundEven.setStyle(Paint.Style.FILL);
        paintBackgroundEven.setColor(ContextCompat.getColor(context, R.color.black_40));

        paintBackgroundOdd = new Paint();
        paintBackgroundOdd.setStyle(Paint.Style.FILL);
        paintBackgroundOdd.setColor(ContextCompat.getColor(context, R.color.black_20));
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

//            as specified by google docs https://developer.android.com/reference/android/graphics/Paint.FontMetrics.html
            canvas.drawLine(0,baseLine+top, 1000, baseLine+top, cyan); //top
            canvas.drawLine(0,baseLine+ascent, 1000, baseLine+ascent, paintNumbers); //ascension
            canvas.drawLine(0,baseLine, 1000, baseLine, blue); //base
            canvas.drawLine(0,baseLine+descent, 1000, baseLine+descent, green); //descension
            canvas.drawLine(0,baseLine+bottom, 1000, baseLine+bottom, red); //bottom


            canvas.drawRect(
                    rect.left,
                    baseLine+bottom - lineHeight,
                    getWidth(),
                    baseLine+bottom,
                    lineNumber % 2 == 1 ? paintBackgroundOdd : paintBackgroundEven);
            
            baseLine += getLineHeight();
        }
        super.onDraw(canvas);
    }
}