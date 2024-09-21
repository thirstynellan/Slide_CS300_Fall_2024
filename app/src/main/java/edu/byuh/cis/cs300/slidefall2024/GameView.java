package edu.byuh.cis.cs300.slidefall2024;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * the main "view" class of the program.
 * All drawing and user interaction "begins" here, although
 * this class delegates much of the work to other classes.
 */
public class GameView extends View {

    private Grid grid;
    private boolean firstRun;

    /**
     * the public constructor. Like all view subclasses,
     * it requires a Context object, which gets forwarded
     * on to the superclass
     * @param context a reference to the Activity that created this view
     */
    public GameView(Context context) {
        super(context);
        firstRun = true;
    }

    /**
     * Whatever you want the user to see, needs to get
     * drawn from this method
     * @param c The Canvas object, provided by the OS when onDraw is called
     */
    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawColor(Color.WHITE);
        if (firstRun) {
            float w = c.getWidth();
            float h = c.getHeight();
            float unit = w/16f;
            float gridX = unit * 2.5f;
            float cellSize = unit * 2.3f;
            float gridY = unit * 9;
            grid = new Grid(gridX, gridY, cellSize);
            firstRun = false;
        }

        grid.draw(c);
    }
}
