package edu.byuh.cis.cs300.slidefall2024;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class GuiButton {

    private RectF bounds;
    private Bitmap unpressedButton;
    private char label;

    public GuiButton(char name, View parent, float x, float y, float width) {
        label = name;
        bounds = new RectF(x, y, x+width, y+width);
        unpressedButton = BitmapFactory.decodeResource(parent.getResources(), R.drawable.unpressed_button);
        unpressedButton = Bitmap.createScaledBitmap(unpressedButton, (int)width, (int)width, true);
    }

    public void draw(Canvas c) {
        c.drawBitmap(unpressedButton, bounds.left, bounds.top, null);
    }

}

