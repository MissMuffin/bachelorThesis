package de.muffinworks.knittingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import net.simplyadvanced.widgets.KeyboardlessEditText2;

/**
 * Created by Bianca on 13.06.2016.
 */
public class LineNumberEditText extends KeyboardlessEditText2 {

    private Rect rect;
    private Paint paintNumbers;
    private Paint paintBackgroundEven;
    private Paint paintBackgroundOdd;


    public LineNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        rect = new Rect();
        paintNumbers = new Paint();
        paintNumbers.setStyle(Paint.Style.FILL);
        paintNumbers.setColor(Color.BLACK);
        paintNumbers.setTextSize(30);

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
        int verticalLineCenter = ((int)(paintNumbers.descent() + paintNumbers.ascent())/2);
        int numbersPaddingLeft = 20;

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
        super.onDraw(canvas);
    }
}