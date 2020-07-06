package com.omni.y5citysdk.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

public class JustifyTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int mLineY = 0;
    private int mViewWidth;
    private TextPaint paint;

    public JustifyTextView(Context context) {
        super(context);
        init();
    }

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JustifyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mLineY = 0;
        mViewWidth = getMeasuredWidth();
        mLineY += getTextSize();

        String text = getText().toString();

        Layout layout = getLayout();
        int lineCount = layout.getLineCount();
        for (int i = 0; i < lineCount; i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.substring(lineStart, lineEnd);
            if (needScale(lineText)) {
                if (i == lineCount - 1) {
                    canvas.drawText(lineText, 0, mLineY, paint);
                } else {
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                    drawScaleText(canvas, lineText, width);
                }
            } else {
                canvas.drawText(lineText, 0, mLineY, paint);
            }
            mLineY += getLineHeight();
        }
    }

    private void drawScaleText(Canvas canvas, String lineText, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineText)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, paint);
            float width = StaticLayout.getDesiredWidth(blanks, paint);
            x += width;
            lineText = lineText.substring(3);
        }
        float interval = (mViewWidth - lineWidth) / (lineText.length() - 1);
        for (int i = 0; i < lineText.length(); i++) {
            String character = String.valueOf(lineText.charAt(i));
            float cw = StaticLayout.getDesiredWidth(character, paint);
            canvas.drawText(character, x, mLineY, paint);
            x += (cw + interval);
        }
    }

    private boolean isFirstLineOfParagraph(String lineText) {
        return lineText.length() > 3 && lineText.charAt(0) == ' ' && lineText.charAt(1) == ' ';
    }

    private boolean needScale(String lineText) {
        if (lineText.length() == 0) {
            return false;
        } else {
            return lineText.charAt(lineText.length() - 1) != '\n';
        }
    }
}
